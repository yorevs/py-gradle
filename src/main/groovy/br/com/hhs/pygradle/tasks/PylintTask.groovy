package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to run pylint against the source tree.
 */
class PylintTask extends PyGradleBaseTask {
  /**
   * Create a new pylint task.
   */
  PylintTask() {
    group = 'Verification'
    description = 'Execute pylint against sourceRoot'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void pylint() {
    def extension = getExtension()
    def pylintCfg = "${project.rootDir}/.pylintrc"
    assert new File(pylintCfg).exists()
    def srcDir = "${extension.sourceRoot}/main/${project.name}"
    def disabledChecks = "${extension.pylintDisabledChecks.join(',')}"
    println("[${project.name}] Execute pylint \n\t from: ${srcDir}")
    project.exec {
      workingDir project.rootDir
      commandLine extension.python, '-m', 'pylint', "--rcfile=${pylintCfg}",
        "--disable=${disabledChecks}", srcDir, "--fail-under=${extension.failScore}"
    }
  }
}
