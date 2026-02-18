package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to uninstall the python module from the system.
 */
class UninstallTask extends PyGradleBaseTask {
  /**
   * Create a new uninstall task.
   */
  UninstallTask() {
    group = 'Install'
    description = 'Uninstall the python module from the system'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void uninstall() {
    def extension = getExtension()
    project.fileTree("${extension.sourceRoot}/main").matching {
      include '**/setup.py'
    }.each { File module ->
      def moduleDir = module.path.replace(module.name, '')
      def args = [
        extension.python, '-m', 'pip', 'uninstall', '-y', '-q', extension.appName,
        '--break-system-packages'
      ]
      println("Uninstall ${project.name} : ${moduleDir} -> ${module.path}")
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
