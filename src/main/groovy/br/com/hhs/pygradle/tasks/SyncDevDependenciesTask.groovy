package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Task to sync development dependencies into pyproject.toml.
 */
class SyncDevDependenciesTask extends PyGradleBaseTask {
  /**
   * Create a new sync dev dependencies task.
   */
  SyncDevDependenciesTask() {
    group = 'Poetry'
    description = 'Gather all project requirements and sync with the pyproject file'
    dependsOn 'syncRequirements'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void syncDevDependencies() {
    def extension = getExtension()
    def requirementsFile = project.file(extension.reqsFile)
    def pyprojectFile = extension.projTomlFile

    println("\nReading requirements: \n  > From: ${extension.reqsFile} \n  > Into: ${extension.projTomlFile}\n")
    println('Packages:')

    def dependencies = [:]
    requirementsFile.eachLine { line ->
      line = line.trim()
      if (line && !line.startsWith('#')) {
        def extrasMatch = line =~ /(.*)\[(.*)\](.*)/
        def pkgMatch = line =~ /(.*)(.*)/
        if (extrasMatch) {
          def name = extrasMatch[0][1].trim()
          def extras = extrasMatch[0][2].trim().split(',').collect { "\"${it.trim()}\"" }
          def version = extrasMatch[0][3].trim()
          dependencies[name] = [version: version, extras: extras]
          println("  |-${name}${extras} ${version}")
        } else if (pkgMatch) {
          def parts = line.split('<|<=|==|!=|>|>=|~=| ')
          def name = parts[0].trim()
          def version = line.replace(name, '').trim()
          dependencies[name] = [version: version]
          println("  |-${name} ${version}")
        }
      }
    }

    def pyproject = pyprojectFile.text

    def devDepsRegex = /(?s)\[tool\.poetry\.dependencies\](?:.*?)(?=\n\[|\z)/
    pyproject = pyproject.replaceAll(devDepsRegex, '')

    def newSection = "[tool.poetry.dependencies]\n\n"
    dependencies.each { name, data ->
      if (data.extras) {
        newSection += "${name} = { version = \"${data.version}\", extras = [${data.extras.join(', ')}] }\n"
      } else {
        newSection += "${name} = \"${data.version}\"\n"
      }
    }

    pyproject += newSection

    pyprojectFile.text = pyproject

    println('\nPoetry dependencies have been updated!.')
  }

  /**
   * Get the requirements file.
   *
   * @return Requirements file.
   */
  @InputFile
  File getRequirementsFile() {
    project.file(getExtension().reqsFile)
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
