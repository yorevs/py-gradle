package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.internal.PyGradleUtils
import org.gradle.api.tasks.TaskAction

/**
 * Task to install the python module into the system.
 */
class InstallTask extends PyGradleBaseTask {
  /**
   * Create a new install task.
   */
  InstallTask() {
    group = 'Install'
    description = 'Installs the python module into the system'
    dependsOn 'syncRequirements'
    dependsOn 'installPackages'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void install() {
    def extension = getExtension()
    project.fileTree(extension.sourceRoot).matching {
      include '**/setup.py'
    }.each { File module ->
      def moduleDir = PyGradleUtils.dirName(module)
      def args = [
        extension.python, '-m', 'pip', 'install', '-q',
        extension.space, '--upgrade', moduleDir,
        '--break-system-packages'
      ]
      println("Installing ${project.name} v${extension.appVersion} using ${extension.python}")
      println("Space: ${extension.space}")
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
