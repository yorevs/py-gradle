package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to print the current project version.
 */
class VersionTask extends PyGradleBaseTask {
  /**
   * Create a new version task.
   */
  VersionTask() {
    group = 'Versioning'
    description = 'Check current program version'
    outputs.upToDateWhen { false }
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void showVersion() {
    def extension = getExtension()
    println("Module: [${project.name}] Current Version: ${extension.appVersion}")
  }
}
