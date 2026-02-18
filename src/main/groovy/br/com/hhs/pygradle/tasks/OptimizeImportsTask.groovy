package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task to optimize Python imports.
 */
class OptimizeImportsTask extends PyGradleBaseTask {
  /**
   * Create a new optimize imports task.
   */
  OptimizeImportsTask() {
    group = 'Documentation'
    description = 'Optimize python imports'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void optimizeImports() {
    def extension = getExtension()
    project.exec {
      commandLine extension.python, '-m', 'isort', '--fas', '--ds', extension.sourceRoot
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
   * Get the output directory for formatted sources.
   *
   * @return Output directory.
   */
  @OutputDirectory
  File getOutputRootDir() {
    project.file(getExtension().sourceRoot)
  }
}
