package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to uninstall project dependencies.
 */
class RemovePackagesTask extends PyGradleBaseTask {
  /**
   * Create a new remove packages task.
   */
  RemovePackagesTask() {
    group = 'Dependencies'
    description = 'Uninstall all installed dependencies'
    dependsOn 'syncRequirements'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void removePackages() {
    def extension = getExtension()
    def reqFile = project.file("${extension.sourceRoot}/main/requirements.txt")
    println("\nUninstalling \"${project.name}\" dependencies using ${extension.python}")
    println("Requirements file: \n\t${reqFile}")
    println('Installed Packages:')
    reqFile.each { dep ->
      if (dep && !dep.startsWith('#')) {
        println("  |-${dep}")
      }
    }
    println("Space: ${extension.space}")
    def args = [
      extension.python, '-m', 'pip', 'uninstall', '-y',
      '-r', reqFile.toString(), '--break-system-packages'
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
