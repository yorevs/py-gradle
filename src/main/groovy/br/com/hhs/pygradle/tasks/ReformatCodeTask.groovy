package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task to reformat Python code using black.
 */
class ReformatCodeTask extends PyGradleBaseTask {
  /**
   * Create a new reformat code task.
   */
  ReformatCodeTask() {
    group = 'Documentation'
    description = 'Reformat the python code using black'
    finalizedBy 'optimizeImports'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void reformatCode() {
    def extension = getExtension()
    project.exec {
      commandLine extension.python, '-m', 'black', '--fast', '-C', '-t', 'py310', '-l', 120, extension.sourceRoot
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
