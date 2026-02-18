package br.com.hhs.pygradle.tasks

import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * Task to validate required tools and configuration.
 */
class ValidateEnvironmentTask extends PyGradleBaseTask {
  /**
   * Create a new validate environment task.
   */
  ValidateEnvironmentTask() {
    group = 'Verification'
    description = 'Validate required tools and configuration'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void validateEnvironment() {
    def extension = getExtension()
    def missing = []
    def hints = []

    def pythonFile = new File(extension.python)
    if (!pythonFile.exists()) {
      missing << "python (${extension.python})"
      hints << 'Install Python 3: https://www.python.org/downloads/'
    }

    def tools = [
      'docker',
      'poetry',
      'bumpver',
      'twine',
      'pdoc',
      'isort',
      'black',
      'mypy',
      'pylint'
    ]

    tools.each { tool ->
      if (!commandExists(tool)) {
        missing << tool
        hints << hintFor(tool, extension)
      }
    }

    if (!extension.pypiModuleUrl) {
      missing << 'pypiModuleUrl'
      hints << 'Set pypiModuleUrl in pyGradle extension or via gradle.properties.'
    }

    if (!missing.isEmpty()) {
      println('Missing required tools/config:')
      missing.each { item -> println("  - ${item}") }
      println('\nHints:')
      hints.findAll { it }.each { hint -> println("  - ${hint}") }
      println('\nYou can run: gradlew installPyGradleTools -PexecuteInstall=true')
      throw new GradleException("Missing required tools/config: ${missing.join(', ')}")
    }
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

  /**
   * Provide an install hint for a given tool.
   *
   * @param tool Tool name.
   * @param extension Plugin extension.
   * @return Hint string.
   */
  private String hintFor(String tool, Object extension) {
    def os = extension.os?.toLowerCase()
    def isMac = os?.contains('mac')
    def isLinux = os?.contains('linux')

    if (tool == 'docker') {
      if (isMac) {
        return 'Install Docker Desktop: brew install --cask docker'
      }
      if (isLinux) {
        return 'Install Docker: https://docs.docker.com/engine/install/'
      }
      return 'Install Docker: https://docs.docker.com/get-docker/'
    }

    if (tool == 'poetry') {
      if (isMac) {
        return 'Install Poetry: brew install poetry'
      }
      return 'Install Poetry: https://python-poetry.org/docs/#installation'
    }

    return "Install via pip: ${extension.python} -m pip install ${tool}"
  }
}
