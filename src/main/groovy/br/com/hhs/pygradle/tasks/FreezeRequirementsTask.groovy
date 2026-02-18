package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.internal.PyGradleUtils
import org.gradle.api.tasks.TaskAction

/**
 * Task to freeze dependencies to current installed versions.
 */
class FreezeRequirementsTask extends PyGradleBaseTask {
  /**
   * Create a new freeze requirements task.
   */
  FreezeRequirementsTask() {
    group = 'Dependencies'
    description = 'Freeze dependencies to current installed versions'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void freezeRequirements() {
    def extension = getExtension()
    PyGradleUtils.readDependencies(extension)
    def output = new ByteArrayOutputStream()
    project.exec {
      commandLine extension.python, '-m', 'pip', 'freeze', '--all'
      standardOutput = output
    }
    def pipDeps = [:]
    output.toString().split('\n').each { line ->
      def dep = null
      if (dep = line =~ /([\w.-]+)==(\d+(\.\d+){0,3})/) {
        dep.each {
          pipDeps[it[1]] = it[2]
        }
      } else if (dep = line =~ /([\w.-]+) @ (file:\/\/{3}.+)/) {
        dep.each {
          pipDeps[it[1]] = 'latest'
        }
      } else {
        println("INVALID DEP: ${dep}  LINE: ${line}")
      }
    }
    println("\nFreezing [${project.name}] Dependencies\n")
    extension.deps.each { dep ->
      def pkgVersion = pipDeps[dep.package]
      if (pkgVersion) {
        println("package: ${dep.package}, version: ${pkgVersion}, mode: ${dep.mode}")
      } else {
        println("package: ${dep.package} -> 'Not-Installed' ${pkgVersion}")
      }
    }
  }
}
