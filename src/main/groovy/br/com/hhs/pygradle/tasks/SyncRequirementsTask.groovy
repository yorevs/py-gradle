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
    def requirements = extension.reqsFile
    requirements.setText("###### AUTO-GENERATED Requirements file for: ${project.name} ######\n\n")
    extension.deps.each { dep ->
      def mode = PyGradleUtils.MODES_MAP[dep.mode]
      if ('latest' == dep.version) {
        requirements.append("${dep.package}\n")
      } else {
        if (mode != null) {
          requirements.append("${dep.package}${mode}${dep.version}\n")
        } else {
          requirements.append("${dep.package}\n")
        }
      }
    }
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
