package br.com.hhs.pygradle.internal

import br.com.hhs.pygradle.PyGradleExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.FileVisitDetails

/**
 * Shared utilities for PyGradle tasks.
 */
class PyGradleUtils {
  /**
   * Map of dependency mode to pip comparator.
   */
  static final Map<String, String> MODES_MAP = [
    lt: '<',
    le: '<=',
    eq: '==',
    ne: '!=',
    gt: '>',
    ge: '>=',
    compat: '~=',
    none: null
  ]

  /**
   * Detect python executable path.
   *
   * @param project The Gradle project.
   * @return Python executable path.
   */
  static String detectPython(Project project) {
    def pythonHome = System.getenv('PYTHON_HOME') ?: null
    if (pythonHome != null) {
      return pythonHome + '/python3'
    }

    def output = new ByteArrayOutputStream()
    project.exec {
      commandLine 'bash', '-c', 'command -v python3'
      standardOutput = output
    }

    pythonHome = output.toString().trim()
    assert pythonHome != null && !pythonHome.isEmpty() : 'Could not find any installed python3.11 version'

    project.logger.lifecycle("Python successfully detected: ${pythonHome}")

    project.exec {
      commandLine 'bash', '-c', 'python3 -V'
    }

    return pythonHome
  }

  /**
   * Detect the package installation space.
   *
   * @param project The Gradle project.
   * @param pythonPath Python executable path.
   * @return The pip space flag.
   */
  static String detectSpace(Project project, String pythonPath) {
    def output = new ByteArrayOutputStream()
    project.exec {
      commandLine pythonPath, '-c', 'from sys import prefix, base_prefix; print(prefix != base_prefix)'
      standardOutput = output
    }
    def isVenv = Boolean.parseBoolean(output.toString().trim())
    project.logger.lifecycle("> Build is${isVenv ? ' ' : ' NOT '}running under a virtual environment (venv).")
    return isVenv ? '--global' : '--user'
  }

  /**
   * Find directory paths matching a pattern.
   *
   * @param project The Gradle project.
   * @param baseDir Base directory to scan.
   * @param pattern Directory name pattern.
   * @return Matching directory paths.
   */
  static Collection<String> dirsByPattern(Project project, String baseDir, String pattern) {
    def paths = []
    project.fileTree(baseDir).visit { FileVisitDetails details ->
      if (details.isDirectory() && details.name ==~ pattern) {
        paths << details.file.path
      }
    }
    return paths
  }

  /**
   * Find files matching a pattern.
   *
   * @param project The Gradle project.
   * @param baseDir Base directory to scan.
   * @param pattern File name pattern.
   * @return Matching files.
   */
  static Collection<File> filesByPattern(Project project, String baseDir, String pattern) {
    def files = []
    project.fileTree(baseDir).visit { FileVisitDetails details ->
      if (!details.isDirectory() && details.name ==~ pattern) {
        files << details.file
      }
    }
    return files
  }

  /**
   * Get the directory name of a file.
   *
   * @param file The input file.
   * @return The file parent path.
   */
  static String dirName(File file) {
    file.getParentFile().getPath()
  }

  /**
   * Return a project-relative path when possible.
   *
   * @param project The Gradle project.
   * @param absPath Absolute path.
   * @return Relative path.
   */
  static String relPath(Project project, String absPath) {
    absPath.replace("${project.projectDir}/", '')
  }

  /**
   * Read dependencies from an HSPD file and populate extension lists.
   *
   * @param extension The PyGradle extension.
   */
  static void readDependencies(PyGradleExtension extension) {
    def depsFile = extension.depsFile
    depsFile.eachLine { line ->
      def dep = null
      if ((dep = line =~ /package: (([\w.-]+)(\[[\w.-]+\])?), version: (latest|\d+(\.\w+){0,5}), mode: (lt|le|eq|compat|ne|gt|ge|none)/)) {
        dep.each {
          extension.deps << [package: "${it[1]}", version: "${it[4]}", mode: "${it[6]}"]
        }
      } else if ((dep = line =~ /package: (([\w.-]+)(\[[\w.-]+\])?), version: (latest|\d+(\.\w+){0,5})/)) {
        dep.each {
          extension.deps << [package: "${it[1]}", version: "${it[4]}", mode: it[4] && it[4] != 'latest' ? 'compat' : 'ge']
        }
      } else if ((dep = line =~ /package: (([\w.-]+)(\[[\w.-]+\])?)/)) {
        dep.each {
          extension.deps << [package: "${it[1]}", version: 'latest', mode: 'ge']
        }
      } else if ((dep = line =~ /binary: (([\w.-]+)(\[[\w.-]+\])?), version: (latest|\d+(\.\w+){0,5})/)) {
        dep.each {
          extension.apps << [binary: "${it[1]}", version: "${it[4]}"]
        }
      } else if ((dep = line =~ /binary: (([\w.-]+)(\[[\w.-]+\])?)/)) {
        dep.each {
          extension.apps << [binary: "${it[1]}", version: 'latest']
        }
      } else {
        if (line.startsWith('package: ')) {
          throw new GradleException("Invalid hspd syntax ${line}. Usage: " +
            'package: <pkg_name>, version: [<latest>|versionNum], ' +
            '[mode: <lt|le|eq|compat|ne|gt|ge|none>]')
        }
      }
    }
  }
}
