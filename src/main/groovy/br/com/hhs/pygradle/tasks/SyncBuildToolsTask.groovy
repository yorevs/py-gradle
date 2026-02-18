package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Task to synchronize the build tools file.
 */
class SyncBuildToolsTask extends PyGradleBaseTask {
  /**
   * Create a new sync build tools task.
   */
  SyncBuildToolsTask() {
    group = 'Build'
    description = 'Synchronize the buildTools file'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void syncBuildTools() {
    def extension = getExtension()
    extension.buildToolsFile.setText("###### Build Tools Files for ${project.name} ######\n\n")
    def String[] buildTools = extension.buildTools.split(',')
    buildTools.each { tool ->
      extension.buildToolsFile.append("${tool.trim()}\n")
    }
  }

  /**
   * Get the build tools list.
   *
   * @return Build tools list.
   */
  @Input
  String getBuildTools() {
    getExtension().buildTools
  }

  /**
   * Get the build tools output file.
   *
   * @return Build tools file.
   */
  @OutputFile
  File getBuildToolsFile() {
    getExtension().buildToolsFile
  }
}
