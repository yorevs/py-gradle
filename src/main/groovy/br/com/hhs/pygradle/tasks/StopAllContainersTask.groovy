package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to stop and remove all containers.
 */
class StopAllContainersTask extends PyGradleBaseTask {
  /**
   * Create a new stop all containers task.
   */
  StopAllContainersTask() {
    group = 'Docker'
    description = 'Stop and remove all containers '
    dependsOn 'isDockerUp'
    dependsOn 'listContainers'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void stopAllContainers() {
    println('Warning: Docker tasks require Docker to be installed and running.')
    def extension = getExtension()
    extension.containers.each { String container ->
      println("Stopping and removing container: ${container}")
      def args = ['docker', 'compose', 'rm', '--stop', '--force']
      if (isDryRun()) {
        println("DRY-RUN: (cd ${project.rootDir}/docker/${container}) ${args.join(' ')}")
      } else {
        project.exec {
          workingDir = "${project.rootDir}/docker/${container}"
          commandLine args
        }
      }
    }
  }
}
