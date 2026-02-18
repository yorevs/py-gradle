package br.com.hhs.pygradle.tasks

import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Task to install PyGradle toolchain dependencies.
 */
class InstallPyGradleToolsTask extends PyGradleBaseTask {
  /**
   * Create a new install tools task.
   */
  InstallPyGradleToolsTask() {
    group = 'Setup'
    description = 'Install required external tools for PyGradle'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void installTools() {
    def extension = getExtension()
    def os = extension.os?.toLowerCase()
    def isMac = os?.contains('mac')
    def isLinux = os?.contains('linux')
    def executeInstall = project.hasProperty('executeInstall') &&
      Boolean.valueOf(project.getProperty('executeInstall'))

    def commands = []

    if (isMac) {
      commands << 'brew install --cask docker'
      commands << 'brew install poetry'
    } else if (isLinux) {
      commands << '# Install Docker: https://docs.docker.com/engine/install/'
      commands << '# Install Poetry: https://python-poetry.org/docs/#installation'
    } else {
      commands << '# Install Docker: https://docs.docker.com/get-docker/'
      commands << '# Install Poetry: https://python-poetry.org/docs/#installation'
    }

    commands << "${extension.python} -m pip install bumpver twine pdoc isort black mypy pylint"

    println('Install commands:')
    commands.each { cmd -> println("  ${cmd}") }

    if (!executeInstall) {
      println('\nTo run automatically, re-run with -PexecuteInstall=true')
      return
    }

    commands.each { cmd ->
      if (cmd.startsWith('#')) {
        println("Skipping manual step: ${cmd}")
      } else {
        if (cmd.startsWith('brew')) {
          if (!isMac) {
            throw new GradleException('Homebrew is only supported on macOS in this task.')
          }
        }
        if (isDryRun()) {
          println("DRY-RUN: ${cmd}")
        } else {
          project.exec {
            commandLine 'bash', '-c', cmd
          }
        }
      }
    }
  }
}
