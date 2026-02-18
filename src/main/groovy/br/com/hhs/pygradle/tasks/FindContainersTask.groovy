package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.internal.PyGradleUtils
import org.gradle.api.tasks.TaskAction

/**
 * Task to find docker-compose files and register containers.
 */
class FindContainersTask extends PyGradleBaseTask {
  /**
   * Create a new find containers task.
   */
  FindContainersTask() {
    group = 'Docker'
    description = 'Find all available docker-compose.yml files from docker directory'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void findContainers() {
    def extension = getExtension()
    def dockerDir = "${project.projectDir}/docker/composes"
    def dockerFiles = PyGradleUtils.filesByPattern(project, dockerDir, /.*docker-compose\.ya?ml$/)
    dockerFiles.each { File file ->
      extension.containers << file.getParentFile().getName()
    }
  }
}
