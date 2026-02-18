package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.internal.PyGradleUtils
import org.gradle.api.tasks.TaskAction

/**
 * Task to run Python unittests.
 */
class CheckTask extends PyGradleBaseTask {
  /**
   * Create a new check task.
   */
  CheckTask() {
    group = 'Verification'
    description = 'Run all python unittests from sourceRoot'
    dependsOn 'compilePython'
    dependsOn 'compileQrc'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void checkTests() {
    def extension = getExtension()
    println("Executing all unittests from ${extension.sourceRoot}")
    project.fileTree("${extension.sourceRoot}/test").matching {
      include '**/test_*.py'
    }.each { File file ->
      def pythonExec = project.file(extension.python).path
      if (extension.verbose) {
        println('')
        println('  PYTHONPATH: ')
        println("\t|- ${extension.pythonPath.split(':').join('\n\t|- ')}")
        println("Executing unittests from -> ${file.name}")
        println('')
      }
      def args = [pythonExec, '-m', 'unittest', '-b', '-f', '-v', file.path]
      if (isDryRun()) {
        println("DRY-RUN: ${args.join(' ')}")
        return
      }
      project.exec {
        workingDir = PyGradleUtils.dirName(file)
        environment PYTHONPATH: extension.pythonPath
        commandLine args
      }
    }
  }
}
