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
    println('Warning: Poetry tasks require Poetry to be installed and available on PATH.')
    project.exec {
      commandLine 'poetry', 'publish', '--build'
    }
  }
}
