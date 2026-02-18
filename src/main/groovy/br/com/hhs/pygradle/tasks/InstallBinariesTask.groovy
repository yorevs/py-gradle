package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.internal.PyGradleUtils
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Task to install required system packages.
 */
class InstallBinariesTask extends PyGradleBaseTask {
  /**
   * Create a new install binaries task.
   */
  InstallBinariesTask() {
    group = 'Dependencies'
    description = 'Install required system packages (applications)'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void installBinaries() {
    def extension = getExtension()
    PyGradleUtils.readDependencies(extension)
    def pkgManager = detectPackageManager()
    if (!pkgManager) {
      throw new GradleException('No supported package manager found (brew, apt, yum).')
    }

    extension.apps.each { app ->
      def name = app.binary
      def version = app.version
      def command = buildInstallCommand(pkgManager, name, version)
      println("${extension.os}: Installing '${name}'" + (version == 'latest' ? '' : " v${version}"))
      if (isDryRun()) {
        println("DRY-RUN: ${command}")
      } else {
        project.exec {
          commandLine 'bash', '-c', command
        }
      }
    }
  }

  /**
   * Detect a supported package manager.
   *
   * @return Package manager name.
   */
  private String detectPackageManager() {
    if (commandExists('brew')) {
      return 'brew'
    }
    if (commandExists('apt-get')) {
      return 'apt'
    }
    if (commandExists('yum')) {
      return 'yum'
    }
    return null
  }

  /**
   * Build an install command for a package manager.
   *
   * @param manager Package manager name.
   * @param name Package name.
   * @param version Package version.
   * @return Shell command.
   */
  private String buildInstallCommand(String manager, String name, String version) {
    def latest = (version == null || version == 'latest')
    if (manager == 'brew') {
      return "brew install ${name}"
    }
    if (manager == 'apt') {
      if (latest) {
        return "sudo apt-get install -y ${name}"
      }
      return "sudo apt-get install -y ${name}=${version}"
    }
    if (manager == 'yum') {
      if (latest) {
        return "sudo yum install -y ${name}"
      }
      return "sudo yum install -y ${name}-${version}"
    }
    throw new GradleException("Unsupported package manager: ${manager}")
  }

  /**
   * Check if a command exists in PATH.
   *
   * @param command Command name.
   * @return True if present.
   */
  private boolean commandExists(String command) {
    def output = new ByteArrayOutputStream()
    def result = project.exec {
      commandLine 'bash', '-c', "command -v ${command}"
      ignoreExitValue true
      standardOutput = output
    }
    return result.exitValue == 0 && output.toString().trim()
  }
}
