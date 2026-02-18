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
  static String detectPython(Project project, String pythonExecOverride = null, String pythonHomeOverride = null) {
    def pythonExec = pythonExecOverride ?: project.findProperty('pythonExec')
    if (pythonExec != null && pythonExec.toString().trim()) {
      return pythonExec.toString().trim()
    }

    def pythonHome = pythonHomeOverride ?: project.findProperty('pythonHome') ?: System.getenv('PYTHON_HOME') ?: null
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
    return isVenv ? '' : '--user'
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
   * Check if pip help text supports the --break-system-packages flag.
   *
   * @param helpText pip --help output.
   * @return True when the flag is supported.
   */
  static boolean supportsBreakSystemPackages(String helpText) {
    helpText?.contains('--break-system-packages')
  }

  /**
   * Detect the venv directory from a python executable path.
   *
   * @param pythonExec Python executable path.
   * @return Venv directory or null when not detected.
   */
  static File detectVenvDir(String pythonExec) {
    if (pythonExec == null || pythonExec.trim().isEmpty()) {
      return null
    }
    def normalized = pythonExec.replace('\\', '/')
    def normalizedLower = normalized.toLowerCase()
    if (normalizedLower.endsWith('/bin/python') || normalizedLower.endsWith('/bin/python3')) {
      return new File(normalized).getParentFile().getParentFile()
    }
    if (normalizedLower.endsWith('/scripts/python.exe')) {
      return new File(normalized).getParentFile().getParentFile()
    }
    null
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
    extension.deps = []
    extension.apps = []
    extension.requirementLines = []
    def inBlockComment = false
    depsFile.eachLine { line ->
      def working = line ?: ''
      if (inBlockComment) {
        def endIdx = working.indexOf('*/')
        if (endIdx == -1) {
          return
        }
        working = working.substring(endIdx + 2)
        inBlockComment = false
      }
      while (true) {
        def startIdx = working.indexOf('/*')
        if (startIdx == -1) {
          break
        }
        def endIdx = working.indexOf('*/', startIdx + 2)
        if (endIdx == -1) {
          working = working.substring(0, startIdx)
          inBlockComment = true
          break
        }
        working = working.substring(0, startIdx) + working.substring(endIdx + 2)
      }
      def trimmed = working.trim()
      if (!trimmed ||
          trimmed.startsWith('#') ||
          trimmed.startsWith('/*') ||
          trimmed.startsWith('*') ||
          trimmed.startsWith('*/')) {
        return
      }
      def dep = null
      if ((dep = trimmed =~ /package: (([\w.-]+)(\[[\w.-]+\])?), version: (latest|\d+(\.\w+){0,5}), mode: (lt|le|eq|compat|ne|gt|ge|none)/)) {
        dep.each {
          extension.deps << [package: "${it[1]}", version: "${it[4]}", mode: "${it[6]}"]
          extension.requirementLines << buildRequirementLine(it[1], it[4], it[6])
        }
      } else if ((dep = trimmed =~ /package: (([\w.-]+)(\[[\w.-]+\])?), version: (latest|\d+(\.\w+){0,5})/)) {
        dep.each {
          def mode = it[4] && it[4] != 'latest' ? 'compat' : 'ge'
          extension.deps << [package: "${it[1]}", version: "${it[4]}", mode: mode]
          extension.requirementLines << buildRequirementLine(it[1], it[4], mode)
        }
      } else if ((dep = trimmed =~ /package: (([\w.-]+)(\[[\w.-]+\])?)/)) {
        dep.each {
          extension.deps << [package: "${it[1]}", version: 'latest', mode: 'ge']
          extension.requirementLines << buildRequirementLine(it[1], 'latest', 'ge')
        }
      } else if ((dep = trimmed =~ /binary: (([\w.-]+)(\[[\w.-]+\])?), version: (latest|\d+(\.\w+){0,5})/)) {
        dep.each {
          extension.apps << [binary: "${it[1]}", version: "${it[4]}"]
        }
      } else if ((dep = trimmed =~ /binary: (([\w.-]+)(\[[\w.-]+\])?)/)) {
        dep.each {
          extension.apps << [binary: "${it[1]}", version: 'latest']
        }
      } else {
        if (trimmed.startsWith('package: ')) {
          throw new GradleException("Invalid hspd syntax ${line}. Usage: " +
            'package: <pkg_name>, version: [<latest>|versionNum], ' +
            '[mode: <lt|le|eq|compat|ne|gt|ge|none>]')
        }
        extension.requirementLines << trimmed
      }
    }
  }

  /**
   * Build a requirements.txt line from a parsed dependency.
   *
   * @param packageName Package name.
   * @param version Version or "latest".
   * @param mode Comparator mode.
   * @return Requirements line.
   */
  static String buildRequirementLine(String packageName, String version, String mode) {
    if ('latest' == version) {
      return packageName
    }
    def comparator = MODES_MAP[mode]
    comparator ? "${packageName}${comparator}${version}" : packageName
  }

  /**
   * Write the requirements file using parsed requirement lines.
   *
   * @param extension PyGradle extension.
   * @param projectName Project name.
   */
  static void writeRequirementsFile(PyGradleExtension extension, String projectName) {
    def requirements = extension.reqsFile
    requirements.parentFile.mkdirs()
    requirements.setText("###### AUTO-GENERATED Requirements file for: ${projectName} ######\n\n")
    extension.requirementLines.each { line ->
      requirements.append("${line}\n")
    }
  }
}
