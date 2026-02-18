package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.internal.PyGradleUtils
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Task to run the Python application entrypoint.
 */
class RunTask extends PyGradleBaseTask {
  /**
   * Create a new run task.
   */
  RunTask() {
    group = 'Application'
    description = 'Run the configured Python application'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void runApp() {
    def extension = getExtension()
    def appPath = extension.application
    if (!appPath) {
      throw new GradleException('Application entrypoint not configured (pyGradle.application).')
    }
    def appFile = project.file(appPath)
    if (!appFile.exists()) {
      throw new GradleException("Application entrypoint not found: ${appFile}")
    }
    def pythonExec = project.file(extension.python).path
    def args = [pythonExec, appFile.path]
    def env = [PYTHONPATH: extension.pythonPath]
    def workDir = PyGradleUtils.dirName(appFile)
    execWithVenvIfAvailable(args, pythonExec, env, workDir)
  }
}
