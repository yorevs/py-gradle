package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to publish Poetry distributions to PyPI.
 */
class PoetryPublishTask extends PyGradleBaseTask {
  /**
   * Create a new poetry publish task.
   */
  PoetryPublishTask() {
    group = 'Poetry'
    description = 'Publish the project to PyPI'
    dependsOn 'syncPyProject'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void poetryPublish() {
    project.exec {
      commandLine 'poetry', 'publish', '--build'
    }
  }
}
