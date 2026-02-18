package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.internal.PyGradleUtils
import org.gradle.api.tasks.TaskAction

/**
 * Task to clean compiled and cached Python artifacts.
 */
class CleanPythonTask extends PyGradleBaseTask {
  /**
   * Create a new clean python task.
   */
  CleanPythonTask() {
    group = 'Build'
    description = 'Clean all compiled (*.py,*.qrc) and cached python files from sourceRoot'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void cleanPython() {
    def extension = getExtension()
    println("Cleaning all compiled files and cache directories \n\t from ${extension.sourceRoot}")
    project.delete project.fileTree(extension.sourceRoot).matching {
      include '**/*.pyc'
      include '**/*.qrc'
      include '**/*.log'
      include '**/requirements.txt'
    }
    project.delete PyGradleUtils.dirsByPattern(project, extension.sourceRoot, /.*__pycache__$/)
  }
}
