package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task to sync Python package __init__.py files.
 */
class SyncPythonPackagesTask extends PyGradleBaseTask {
  private static final String INIT_TEMPLATE = """# _*_ coding: utf-8 _*_
#
# \$app_name v%version%
#
# Package: %package%
\"\"\"Package initialization.\"\"\"

__all__ = %modules%
__version__ = '%version%'
"""

  /**
   * Create a new sync python packages task.
   */
  SyncPythonPackagesTask() {
    group = 'Build'
    description = 'Find and update __init__ files'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void syncPythonPackages() {
    def extension = getExtension()
    project.file(extension.sourceRoot).traverse(type: groovy.io.FileType.DIRECTORIES) { directory ->
      directory.eachFileMatch(~/^__init__\.py$/) { initFile ->
        def pkg = initFile.getCanonicalPath()
          .replaceAll("${extension.sourceRoot}/", '')
          .replaceAll('\\/', '\\.')
          .replaceAll('\\.?__init__\\.py', '')
        def modules = []
        initFile.getParentFile().eachFile() { module ->
          if (!module.name.startsWith('__') &&
              !module.name.endsWith('setup.py') &&
              ((module.isFile() && module.name.endsWith('.py')) ||
              new File(module.getCanonicalPath() + '/__init__.py').exists())) {
            if (!module.isFile() && extension.verbose) {
              println("Syncing package: ${initFile.getParentFile()}/${module.name}")
            }
            modules << "'${module.name.replaceAll('\\.py', '')}'"
          }
        }
        def verFile = new File(extension.versionFile)
        def curVersion = verFile.getText().trim()
        initFile.write INIT_TEMPLATE
          .replaceAll('%package%', pkg)
          .replaceAll('%modules%', modules.sort().toString())
          .replaceAll('%version%', curVersion)
          .replaceAll('\\[', '\\[\n    ')
          .replaceAll(', ', ', \n    ')
          .replaceAll('\\]', '\n]')
      }
    }
  }

  /**
   * Get the source root directory.
   *
   * @return Source root directory.
   */
  @InputDirectory
  File getSourceRootDir() {
    project.file(getExtension().sourceRoot)
  }

  /**
   * Get the output directory for updated packages.
   *
   * @return Output directory.
   */
  @OutputDirectory
  File getOutputRootDir() {
    project.file(getExtension().sourceRoot)
  }
}
