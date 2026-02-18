package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to synchronize the pyproject.toml file.
 */
class SyncPyProjectTask extends PyGradleBaseTask {
  /**
   * Create a new sync pyproject task.
   */
  SyncPyProjectTask() {
    group = 'Poetry'
    description = 'Synchronize the pyproject.file'
    dependsOn 'syncSetupPy'
    dependsOn 'syncBuildSystemRequirements'
    dependsOn 'syncDevDependencies'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void syncPyProject() {
    // This task delegates to its dependencies.
  }
}
