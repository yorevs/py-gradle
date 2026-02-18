package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.internal.PyGradleUtils
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task to generate PyPI distribution files.
 */
class SdistTask extends PyGradleBaseTask {
  /**
   * Create a new sdist task.
   */
  SdistTask() {
    group = 'Publish'
    description = 'Generate PyPi distribution files'
    dependsOn 'cleanDist'
    dependsOn 'copyLicenseAndReadme'
    dependsOn 'syncRequirements'
    finalizedBy 'checkDist'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void sdist() {
    def extension = getExtension()
    def distDir = "${project.buildDir}/dist"
    project.fileTree(extension.sourceRoot).matching {
      include '**/setup.py'
    }.each { File module ->
      def moduleDir = PyGradleUtils.dirName(module)
      println("Generating distribution for '${moduleDir}' => ${distDir}")
      project.exec {
        workingDir moduleDir
        commandLine extension.python, '-m', 'build', '-o', distDir, '--sdist'
      }
      project.exec {
        workingDir moduleDir
        commandLine extension.python, '-m', 'build', '-o', distDir, '--wheel'
      }
    }
  }

  /**
   * Get the source root directory.
   *
   * @return Source root directory.
   */
  @InputDirectory
  File getSourceRootDir() {
    project.file(getExtension().sourceRoot)
  }

  /**
   * Get the distribution output directory.
   *
   * @return Distribution directory.
   */
  @OutputDirectory
  File getDistOutputDir() {
    new File("${project.buildDir}/dist")
  }
}
