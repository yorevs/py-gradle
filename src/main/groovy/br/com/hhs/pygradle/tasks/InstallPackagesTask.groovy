package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.internal.PyGradleUtils
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
    if (!reqFile.exists()) {
      println("Requirements file missing. Regenerating from ${extension.depsFile}.")
      PyGradleUtils.readDependencies(extension)
      reqFile.parentFile.mkdirs()
      reqFile.setText("###### AUTO-GENERATED Requirements file for: ${project.name} ######\n\n")
      extension.deps.each { dep ->
        def mode = PyGradleUtils.MODES_MAP[dep.mode]
        if ('latest' == dep.version) {
          reqFile.append("${dep.package}\n")
        } else {
          if (mode != null) {
            reqFile.append("${dep.package}${mode}${dep.version}\n")
          } else {
            reqFile.append("${dep.package}\n")
          }
        }
      }
    }
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
    println("Space: ${extension.space ?: 'venv default'}")
    upgradePip(extension.python)
    def breakSystemPackages = pipSupportsBreakSystemPackages(extension.python)
    def args = [
      extension.python, '-m', 'pip', 'install', '--no-warn-script-location',
      '--upgrade', '-r', reqFile.toString()
    ]
    if (extension.space) {
      args << extension.space
    }
    if (extension.pipExtraIndexUrl) {
      println("Pip extra index: ${extension.pipExtraIndexUrl}")
      args << '--extra-index-url'
      args << extension.pipExtraIndexUrl
    }
    if (breakSystemPackages) {
      args << '--break-system-packages'
    }
    if (!extension.verbose) {
      args += '-q'
    }
    if (isDryRun()) {
      println("DRY-RUN: ${args.flatten().join(' ')}")
      return
    }
    execWithVenvIfAvailable(args.flatten(), extension.python)
  }
}
