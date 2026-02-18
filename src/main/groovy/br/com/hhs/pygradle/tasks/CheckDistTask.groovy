package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to check distribution files.
 */
class CheckDistTask extends PyGradleBaseTask {
  /**
   * Create a new check dist task.
   */
  CheckDistTask() {
    group = 'Publish'
    description = 'Check files created in dist folder'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void checkDist() {
    def extension = getExtension()
    project.fileTree(extension.sourceRoot).matching {
      include '**/setup.py'
    }.each { File module ->
      def distDir = "${project.buildDir}/dist"
      println("Checking distribution files -> ${project.buildDir}")
      project.exec {
        workingDir project.buildDir
        commandLine extension.python, '-m', 'twine', 'check', "${distDir}/*"
      }
    }
  }
}
