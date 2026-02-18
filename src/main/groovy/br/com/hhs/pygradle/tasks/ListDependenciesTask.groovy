package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.internal.PyGradleUtils
import org.gradle.api.tasks.TaskAction

/**
 * Task to list project dependencies.
 */
class ListDependenciesTask extends PyGradleBaseTask {
  /**
   * Create a new list dependencies task.
   */
  ListDependenciesTask() {
    group = 'Dependencies'
    description = 'List project dependencies'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void listDependencies() {
    def extension = getExtension()
    println("\nListing dependencies from:\n\t${extension.depsFile}\n")
    PyGradleUtils.readDependencies(extension)
    extension.deps.each { dep ->
      println("Package: ${dep.package}, Version: ${dep.version}, Mode: ${dep.mode}")
    }
  }
}
