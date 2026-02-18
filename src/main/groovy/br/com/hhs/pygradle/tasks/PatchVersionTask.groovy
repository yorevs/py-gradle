package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to increment the patch version.
 */
class PatchVersionTask extends PyGradleBaseTask {
  /**
   * Create a new patch version task.
   */
  PatchVersionTask() {
    group = 'Versioning'
    description = 'Increment the build number for publishing (patch)'
    outputs.upToDateWhen { false }
    finalizedBy 'syncPythonPackages'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void patchVersion() {
    def extension = getExtension()
    println('Patching the build number')
    project.exec {
      commandLine extension.python, '-m', 'bumpver', 'update', '--patch'
    }
  }
}
