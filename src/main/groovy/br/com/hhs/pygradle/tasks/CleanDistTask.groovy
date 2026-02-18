package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.internal.PyGradleUtils
import org.gradle.api.tasks.TaskAction

/**
 * Task to clean distribution files.
 */
class CleanDistTask extends PyGradleBaseTask {
  /**
   * Create a new clean dist task.
   */
  CleanDistTask() {
    group = 'Build'
    description = 'Cleanup distribution files from buildDir and sourceRoot'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void cleanDist() {
    def extension = getExtension()
    println('Cleanup distribution files from buildDir and sourceRoot')
    project.delete PyGradleUtils.dirsByPattern(project, "${project.buildDir}", /.*dist$/)
    project.delete PyGradleUtils.dirsByPattern(project, "${project.buildDir}", /.*build$/)
    project.delete PyGradleUtils.dirsByPattern(project, "${project.buildDir}", /.*\.egg-info$/)
    project.delete PyGradleUtils.dirsByPattern(project, extension.sourceRoot, /.*dist$/)
    project.delete PyGradleUtils.dirsByPattern(project, extension.sourceRoot, /.*build$/)
    project.delete PyGradleUtils.dirsByPattern(project, extension.sourceRoot, /.*\.egg-info$/)
  }
}
