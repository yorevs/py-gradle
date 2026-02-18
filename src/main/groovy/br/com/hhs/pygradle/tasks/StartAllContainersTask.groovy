package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to start all containers.
 */
class StartAllContainersTask extends PyGradleBaseTask {
  /**
   * Create a new start all containers task.
   */
  StartAllContainersTask() {
    group = 'Docker'
    description = 'Start all containers as detached daemons'
    dependsOn 'isDockerUp'
    dependsOn 'listContainers'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void startAllContainers() {
    def extension = getExtension()
    extension.containers.each { String container ->
      println("=> Starting container: ${container}")
      def args = ['docker', 'compose', 'up', '--force-recreate', '--build', '--remove-orphans', '--detach']
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
