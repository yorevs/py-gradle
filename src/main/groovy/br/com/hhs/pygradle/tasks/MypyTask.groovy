package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to run mypy against the source tree.
 */
class MypyTask extends PyGradleBaseTask {
  /**
   * Create a new mypy task.
   */
  MypyTask() {
    group = 'Verification'
    description = 'Execute mypy against sourceRoot'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void mypy() {
    def extension = getExtension()
    def srcDir = "${extension.sourceRoot}/main/${project.name}"
    println("[${project.name}] Execute mypy \n\t from: ${srcDir}")
    project.exec {
      workingDir project.rootDir
      commandLine extension.python, '-m', 'mypy', srcDir
    }
  }
}
