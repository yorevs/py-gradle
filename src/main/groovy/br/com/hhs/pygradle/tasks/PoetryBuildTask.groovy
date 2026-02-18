package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to build Poetry distributions.
 */
class PoetryBuildTask extends PyGradleBaseTask {
  /**
   * Create a new poetry build task.
   */
  PoetryBuildTask() {
    group = 'Poetry'
    description = 'Create a source distribution and a wheel'
    dependsOn 'syncPyProject'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void poetryBuild() {
    project.exec {
      commandLine 'poetry', 'build'
    }
  }
}
