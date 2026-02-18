package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to increment the major version.
 */
class UpdateMajorTask extends PyGradleBaseTask {
  /**
   * Create a new update major task.
   */
  UpdateMajorTask() {
    group = 'Versioning'
    description = 'Increment the build number (major)'
    outputs.upToDateWhen { false }
    finalizedBy 'syncPythonPackages'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void updateMajor() {
    def extension = getExtension()
    println('Updating the major number')
    project.exec {
      commandLine extension.python, '-m', 'bumpver', 'update', '--major'
    }
  }
}
