package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.internal.PyGradleUtils
import org.gradle.api.tasks.TaskAction

/**
 * Task to list Gradle properties.
 */
class ListPropertiesTask extends PyGradleBaseTask {
  /**
   * Create a new list properties task.
   */
  ListPropertiesTask() {
    group = 'Versioning'
    description = 'List all properties from gradle.properties'
    outputs.upToDateWhen { false }
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void listProperties() {
    def extension = getExtension()
    def regex = /.*=.*$/
    File propsFile = project.file('gradle.properties')
    if (propsFile.exists()) {
      println("\n[${project.name}] Checking properties from: " + PyGradleUtils.relPath(project, "${propsFile}") + "\n")
      propsFile.eachLine { line ->
        if (line?.trim() && line ==~ regex) {
          def (name, value) = line.tokenize('=')
          println("${name.padRight(40, ' ')} => ${value.padRight(10, ' ')}")
        }
      }
    }
    println(''.padRight(80, '-'))
    println("SOURCE_ROOT".padRight(40, ' ') + " =>  " + PyGradleUtils.relPath(project, extension.sourceRoot))
    println("BUILD_DIR".padRight(40, ' ') + " =>  " + PyGradleUtils.relPath(project, "${project.buildDir}"))
    println("PYTHON_PATH".padRight(40, ' ') + " =>  " + PyGradleUtils.relPath(project, extension.pythonPath).split(':').join(', '))
    def appPath = project.ext.has('application') ? project.ext.application : 'N/A'
    println("Application".padRight(40, ' ') + " =>  " + PyGradleUtils.relPath(project, "${appPath}"))
  }
}
