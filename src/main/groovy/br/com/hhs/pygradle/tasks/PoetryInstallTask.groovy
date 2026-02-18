package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to install Poetry dependencies.
 */
class PoetryInstallTask extends PyGradleBaseTask {
  /**
   * Create a new poetry install task.
   */
  PoetryInstallTask() {
    group = 'Poetry'
    description = 'Install poetry dependencies'
    dependsOn 'syncPyProject'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void poetryInstall() {
    println('Warning: Poetry tasks require Poetry to be installed and available on PATH.')
    project.exec {
      commandLine 'poetry', 'install'
    }
  }
}
