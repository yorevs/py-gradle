package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to install build tools for the project.
 */
class InstallBuildToolsTask extends PyGradleBaseTask {
  /**
   * Create a new install build tools task.
   */
  InstallBuildToolsTask() {
    group = 'Build'
    description = 'Install application build tools using Global space'
    dependsOn 'syncBuildTools'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void installBuildTools() {
    def extension = getExtension()
    println("Installing \"${project.name}\" build tools: ")
    println('Packages:')
    def String[] buildTools = extension.buildTools.split(',')
    buildTools.each { tool ->
      println("  |-${tool.trim()}")
    }
    println('Space: --global')
    def args = [
      extension.python, '-m', 'pip', 'install', '-q',
      extension.space, '--upgrade', '-r', extension.buildToolsFile,
      '--no-warn-script-location', '--break-system-packages'
    ]
    if (isDryRun()) {
      println("DRY-RUN: ${args.flatten().join(' ')}")
      return
    }
    project.exec {
      commandLine args.flatten()
    }
  }
}
