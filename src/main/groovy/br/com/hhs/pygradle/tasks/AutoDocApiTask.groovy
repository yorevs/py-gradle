package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task to generate API documentation based on docstrings.
 */
class AutoDocApiTask extends PyGradleBaseTask {
  /**
   * Create a new auto doc task.
   */
  AutoDocApiTask() {
    group = 'Documentation'
    description = 'Generate API documentation based on docstrings'
    dependsOn 'syncPythonPackages'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void autoDocApi() {
    def extension = getExtension()
    def apiSrcDir = "${extension.sourceRoot}/main/${project.name.toLowerCase()}"
    project.exec {
      commandLine extension.python, '-m', 'pdoc', '-o', "${extension.apiDocsDir}/${project.name}", apiSrcDir
    }
  }

  /**
   * Get the API source directory.
   *
   * @return API source directory.
   */
  @InputDirectory
  File getApiSourceDir() {
    project.file("${getExtension().sourceRoot}/main/${project.name.toLowerCase()}")
  }

  /**
   * Get the API docs output directory.
   *
   * @return API docs output directory.
   */
  @OutputDirectory
  File getApiDocsDir() {
    new File("${getExtension().apiDocsDir}/${project.name}")
  }
}
