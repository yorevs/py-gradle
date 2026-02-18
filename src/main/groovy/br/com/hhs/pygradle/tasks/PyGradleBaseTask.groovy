package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.PyGradleExtension
import br.com.hhs.pygradle.internal.PyGradleUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal

/**
 * Base class for PyGradle tasks.
 */
abstract class PyGradleBaseTask extends DefaultTask {
  /**
   * Get the configured PyGradle extension.
   *
   * @return Extension instance.
   */
  @Internal
  protected PyGradleExtension getExtension() {
    project.extensions.getByType(PyGradleExtension)
  }

  /**
   * Check if dry-run is enabled.
   *
   * @return True when dry-run is enabled.
   */
  @Internal
  protected boolean isDryRun() {
    getExtension().dryRun
  }

  /**
   * Execute a command, activating a venv when the python executable is from one.
   *
   * @param args Command args.
   * @param pythonExec Python executable used to detect venv.
   */
  protected void execWithVenvIfAvailable(
      List<String> args,
      String pythonExec,
      Map<String, Object> env = [:],
      String workingDir = null
  ) {
    def venvDir = PyGradleUtils.detectVenvDir(pythonExec)
    def workDir = workingDir
    if (venvDir == null) {
      project.exec {
        if (workDir) {
          workingDir = workDir
        }
        if (env) {
          environment env
        }
        commandLine args
      }
      return
    }
    println("Activating virtual environment: ${venvDir}")
    def os = getExtension().os?.toLowerCase()
    def isWindows = os?.contains('win')
    if (isWindows) {
      def activate = new File(venvDir, 'Scripts/activate.bat').path
      def envCmd = env ? env.collect { key, value -> "set ${key}=${value}" }.join(' && ') + ' && ' : ''
      def command = "call \"${activate}\" && ${envCmd}${args.join(' ')} && deactivate"
      if (isDryRun()) {
        println("DRY-RUN: ${command}")
        return
      }
      project.exec {
        commandLine 'cmd', '/c', command
      }
      return
    }
    def activate = new File(venvDir, 'bin/activate').path
    def envCmd = env ? env.collect { key, value -> "export ${key}=\"${value}\"" }.join('; ') + '; ' : ''
    def command = "set -e; source \"${activate}\"; trap 'deactivate' EXIT; ${envCmd}${args.join(' ')}"
    if (isDryRun()) {
      println("DRY-RUN: ${command}")
      return
    }
    project.exec {
      if (workDir) {
        workingDir = workDir
      }
      commandLine 'bash', '-c', command
    }
  }

  /**
   * Check if pip supports --break-system-packages for the given python.
   *
   * @param pythonExec Python executable.
   * @return True when supported.
   */
  protected boolean pipSupportsBreakSystemPackages(String pythonExec) {
    def output = new ByteArrayOutputStream()
    def result = project.exec {
      commandLine pythonExec, '-m', 'pip', '--help'
      ignoreExitValue true
      standardOutput = output
    }
    if (result.exitValue != 0) {
      return false
    }
    PyGradleUtils.supportsBreakSystemPackages(output.toString())
  }

  /**
   * Upgrade pip before installing packages.
   *
   * @param pythonExec Python executable.
   */
  protected void upgradePip(String pythonExec) {
    println('Upgrading pip before installing packages.')
    def args = [pythonExec, '-m', 'pip', 'install', '--upgrade', 'pip']
    if (isDryRun()) {
      println("DRY-RUN: ${args.join(' ')}")
      return
    }
    execWithVenvIfAvailable(args, pythonExec)
  }
}
