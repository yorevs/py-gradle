package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to increment the minor version.
 */
class UpdateMinorTask extends PyGradleBaseTask {
  /**
   * Create a new update minor task.
   */
  UpdateMinorTask() {
    group = 'Versioning'
    description = 'Increment the build number (minor)'
    outputs.upToDateWhen { false }
    finalizedBy 'syncPythonPackages'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void updateMinor() {
    def extension = getExtension()
    println('Updating the minor number')
    project.exec {
      commandLine extension.python, '-m', 'bumpver', 'update', '--minor'
    }
  }
}
