package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to list docker containers.
 */
class ListContainersTask extends PyGradleBaseTask {
  /**
   * Create a new list containers task.
   */
  ListContainersTask() {
    group = 'Docker'
    description = 'List all available docker containers'
    dependsOn 'findContainers'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void listContainers() {
    println('Warning: Docker tasks require Docker to be installed and running.')
    def extension = getExtension()
    extension.containers.each { container ->
      println("Container available: ${container}")
    }
  }
}
