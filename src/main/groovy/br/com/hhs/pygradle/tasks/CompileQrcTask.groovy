package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task to compile Qt resource files.
 */
class CompileQrcTask extends PyGradleBaseTask {
  /**
   * Create a new compile QRC task.
   */
  CompileQrcTask() {
    group = 'Build'
    description = 'Compile all Qt resource files from sourceRoot'
    dependsOn 'installPackages'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void compileQrc() {
    def extension = getExtension()
    project.fileTree(extension.sourceRoot).matching {
      include '**/*.qrc'
    }.each { File file ->
      if (extension.verbose) {
        println("Compiling Qt Resource -> ${file.name}")
      }
      project.exec {
        commandLine extension.pyrcc, file.path
      }
    }
    writeMarker(getOutputRootDir())
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
   * Get the output root directory for compiled resources.
   *
   * @return Output directory.
   */
  @OutputDirectory
  File getOutputRootDir() {
    project.file("${project.buildDir}/pygradle/compileQrc")
  }

  /**
   * Write a marker file for task outputs.
   *
   * @param outputDir Output directory.
   */
  private void writeMarker(File outputDir) {
    if (isDryRun()) {
      return
    }
    outputDir.mkdirs()
    new File(outputDir, 'compiled.marker').setText("compiled:${System.currentTimeMillis()}\n")
  }
}
