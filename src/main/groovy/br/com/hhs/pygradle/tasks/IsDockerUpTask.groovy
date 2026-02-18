package br.com.hhs.pygradle.tasks

import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Task to check if Docker is running.
 */
class IsDockerUpTask extends PyGradleBaseTask {
  /**
   * Create a new Docker status task.
   */
  IsDockerUpTask() {
    group = 'Docker'
    description = 'Check if docker agent is running'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void isDockerUp() {
    println('Warning: Docker tasks require Docker to be installed and running.')
    def result = project.exec {
      commandLine 'docker', 'ps'
      ignoreExitValue true
      standardOutput = OutputStream.nullOutputStream()
      errorOutput = OutputStream.nullOutputStream()
    }
    if (result.exitValue == 0) {
      println('Docker agent is up')
    } else {
      throw new GradleException('Docker agent is down')
    }
  }
}
