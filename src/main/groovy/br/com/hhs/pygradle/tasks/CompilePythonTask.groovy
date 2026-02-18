package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task to compile Python sources.
 */
class CompilePythonTask extends PyGradleBaseTask {
  /**
   * Create a new compile python task.
   */
  CompilePythonTask() {
    group = 'Build'
    description = 'Compile all python files from sourceRoot'
    dependsOn 'installPackages'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void compilePython() {
    def extension = getExtension()
    println("Compiling Python files from ${project.name} \n\t ${extension.sourceRoot}")
    project.fileTree(extension.sourceRoot).matching {
      include '**/*.py'
      exclude '**/__init__.py'
    }.each { File file ->
      if (extension.verbose) {
        println("Compiling Python file -> ${file.name}")
      }
      project.exec {
        commandLine extension.python, '-m', 'py_compile', file.path
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
   * Get the output root directory for compiled files.
   *
   * @return Output directory.
   */
  @OutputDirectory
  File getOutputRootDir() {
    project.file(getExtension().sourceRoot)
  }
}
