package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.internal.PyGradleUtils
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Task to generate requirements.txt from dependencies.
 */
class SyncRequirementsTask extends PyGradleBaseTask {
  /**
   * Create a new sync requirements task.
   */
  SyncRequirementsTask() {
    group = 'Dependencies'
    description = 'Gather all project requirements and generate a requirements file'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void syncRequirements() {
    def extension = getExtension()
    PyGradleUtils.readDependencies(extension)
    PyGradleUtils.writeRequirementsFile(extension, project.name)
  }

  /**
   * Get the dependencies definition file.
   *
   * @return Dependencies file.
   */
  @InputFile
  File getDependenciesFile() {
    getExtension().depsFile
  }

  /**
   * Get the generated requirements file.
   *
   * @return Requirements file.
   */
  @OutputFile
  File getRequirementsFile() {
    getExtension().reqsFile
  }
}
