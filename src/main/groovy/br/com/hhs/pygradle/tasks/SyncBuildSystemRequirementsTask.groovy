package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Task to sync build system requirements into pyproject.toml.
 */
class SyncBuildSystemRequirementsTask extends PyGradleBaseTask {
  /**
   * Create a new sync build system requirements task.
   */
  SyncBuildSystemRequirementsTask() {
    group = 'Poetry'
    description = 'Gather all project requirements and sync with the pyproject file'
    dependsOn 'installBuildTools'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void syncBuildSystemRequirements() {
    def extension = getExtension()
    def buildToolsFile = project.file(extension.buildToolsFile)
    def pyprojectFile = extension.projTomlFile

    def requirements = []
    buildToolsFile.eachLine { line ->
      line = line.trim()
      if (line && !line.startsWith('#')) {
        requirements << "\"${line}\""
      }
    }

    if (requirements.isEmpty()) {
      println('No valid entries found in buildToolsFile. The requires attribute will be empty.')
    }

    def pyproject = pyprojectFile.text

    def buildSystemRegex = /(?s)\[build-system\](.*?)(?=\n\[|\z)/
    pyproject = pyproject.replaceAll(buildSystemRegex) { match, content ->
      def newBuildSystemSection = "[build-system]\n\n"
      newBuildSystemSection += "requires = [\n"
      requirements.each { req ->
        newBuildSystemSection += "  ${req},\n"
      }
      newBuildSystemSection += "]\n"
      newBuildSystemSection += "build-backend = \"poetry.core.masonry.api\"\n"
      return newBuildSystemSection
    }

    pyprojectFile.text = pyproject

    println('Build-system requires attribute has been updated with entries from buildToolsFile.')
  }

  /**
   * Get the build tools file.
   *
   * @return Build tools file.
   */
  @InputFile
  File getBuildToolsFile() {
    project.file(getExtension().buildToolsFile)
  }

  /**
   * Get the pyproject.toml file.
   *
   * @return Pyproject file.
   */
  @OutputFile
  File getPyprojectFile() {
    getExtension().projTomlFile
  }
}
