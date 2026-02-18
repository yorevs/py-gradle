package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.internal.PyGradleUtils
import org.gradle.api.tasks.TaskAction

/**
 * Task to install the module in editable mode.
 */
class InstallModuleTask extends PyGradleBaseTask {
  /**
   * Create a new install module task.
   */
  InstallModuleTask() {
    group = 'Install'
    description = "Install module in editable mode (i.e. setuptools 'develop mode')"
    dependsOn 'syncRequirements'
    dependsOn 'installPackages'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void installModule() {
    def extension = getExtension()
    project.fileTree(extension.sourceRoot).matching {
      include '**/setup.py'
    }.each { File module ->
      def moduleDir = PyGradleUtils.dirName(module)
      def args = [
        extension.python, '-m', 'pip', 'install', '-e', "${extension.sourceRoot}/main",
        '--break-system-packages'
      ]
      println("Install [Editable] module ${project.name} : ${moduleDir} -> ${module.path}")
      if (isDryRun()) {
        println("DRY-RUN: (cd ${moduleDir}) ${args.flatten().join(' ')}")
        return
      }
      project.exec {
        workingDir moduleDir
        commandLine args.flatten()
      }
    }
  }
}
