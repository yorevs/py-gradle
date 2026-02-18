package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to install project dependencies via pip.
 */
class InstallPackagesTask extends PyGradleBaseTask {
  /**
   * Create a new install packages task.
   */
  InstallPackagesTask() {
    group = 'Dependencies'
    description = 'Install all required python packages'
    dependsOn 'syncRequirements'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void installPackages() {
    def extension = getExtension()
    def reqFile = extension.reqsFile
    print("\nInstalling \"${project.name}\" dependencies using: ${extension.python} => ")
    project.exec {
      commandLine extension.python, '-V'
    }
    println("Requirements file: \n\t${reqFile}")
    println('Required Packages:')
    reqFile.each { dep ->
      if (dep && !dep.startsWith('#')) {
        println("  |-${dep}")
      }
    }
    println("Space: ${extension.space}")
    def args = [
      extension.python, '-m', 'pip', 'install', '--no-warn-script-location',
      extension.space, '--upgrade', '-r', reqFile.toString(),
      '--break-system-packages'
    ]
    if (!extension.verbose) {
      args += '-q'
    }
    if (isDryRun()) {
      println("DRY-RUN: ${args.flatten().join(' ')}")
      return
    }
    project.exec {
      commandLine args.flatten()
    }
  }
}
