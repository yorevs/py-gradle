package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to scaffold Gradle and project files for PyGradle.
 */
class PyGradleInitTask extends PyGradleBaseTask {
  /**
   * Create a new init task.
   */
  PyGradleInitTask() {
    group = 'Setup'
    description = 'Scaffold Gradle and project files for PyGradle'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void initProject() {
    def extension = getExtension()
    def force = project.hasProperty('forceInit') && Boolean.valueOf(project.getProperty('forceInit'))
    def skipVenv = project.hasProperty('skipVenv') && Boolean.valueOf(project.getProperty('skipVenv'))
    def appName = project.findProperty('initAppName') ?: extension.appName ?: project.name
    def rootName = project.findProperty('initRootName') ?: project.name
    def version = project.findProperty('initVersion') ?: project.version ?: '0.1.0'
    def pythonVersion = project.findProperty('pythonVersion') ?: extension.pythonVersion ?: '3.11.14'
    def venvDir = project.file('.venv')
    def pythonSystemExec = resolvePythonExecutable(pythonVersion)
    def venvPythonPath = resolveVenvPythonPath(venvDir)
    def pythonExecForConfig = skipVenv ? pythonSystemExec : venvPythonPath

    def files = [
      [path: 'settings.gradle', content: settingsGradleContent(rootName)],
      [path: 'build.gradle', content: buildGradleContent()],
      [path: 'gradle.properties', content: gradlePropertiesContent(appName, version)],
      [path: 'pygradle.properties', content: pygradlePropertiesContent(appName, version, pythonVersion, pythonExecForConfig, skipVenv)],
      [path: 'pygradle.yaml', content: pygradleYamlContent(appName, version, pythonVersion, pythonExecForConfig, skipVenv)],
      [path: 'dependencies.hspd', content: dependenciesContent()],
      [path: 'src/main/python/__main__.py', content: mainPyContent(appName)],
      [path: 'src/main/python/resources/application.properties', content: applicationPropertiesContent(appName)],
      [path: 'src/test/test_main.py', content: testMainContent()],
      [path: 'src/test/resources/application-test.properties', content: testPropertiesContent()]
    ]

    def dirs = [
      'src/main/python',
      'src/main/python/resources',
      'src/test',
      'src/test/resources'
    ]

    for (String dir : dirs) {
      ensureDir(dir, force)
    }
    for (def file : files) {
      writeFile(file.path, file.content, force)
    }

    if (!skipVenv) {
      createVirtualEnv(venvDir, pythonSystemExec, force)
    }

    println('PyGradle init complete.')
    println('Next steps:')
    println('  - ./gradlew validateEnvironment')
    println('  - ./gradlew tasks --all')
  }

  /**
   * Ensure a directory exists.
   *
   * @param path Directory path.
   * @param force Overwrite flag.
   */
  private void ensureDir(String path, boolean force) {
    def dir = project.file(path)
    if (dir.exists()) {
      return
    }
    if (isDryRun()) {
      println("DRY-RUN: mkdir -p ${dir}")
      return
    }
    project.mkdir(dir)
  }

  /**
   * Write a file if missing or forced.
   *
   * @param path File path.
   * @param content File content.
   * @param force Overwrite flag.
   */
  private void writeFile(String path, String content, boolean force) {
    def file = project.file(path)
    if (file.exists() && !force) {
      println("Skip existing file: ${path}")
      return
    }
    if (isDryRun()) {
      println("DRY-RUN: write ${path}")
      return
    }
    file.parentFile.mkdirs()
    file.setText(content)
  }

  private String settingsGradleContent(String rootName) {
    """// Project settings (Gradle needs a project name).\n// Change the name if you want a different folder/app name.\nrootProject.name = '${rootName}'\n"""
  }

  private String buildGradleContent() {
    """// Build configuration (apply the PyGradle plugin).\n// This gives you tasks like build, lint, test, and packaging.\nplugins {
  id 'br.com.hhs.pygradle'
}
"""
  }

  private String gradlePropertiesContent(String appName, String version) {
    """# Project metadata used by the plugin.\n# app_name: used in outputs and metadata.\n# app_version: used in versioning and packaging tasks.\napp_name      = ${appName}
app_version   = ${version}
"""
  }

  private String pygradlePropertiesContent(String appName, String version, String pythonVersion, String pythonExec, boolean skipVenv) {
    def venvNote = skipVenv ?
      '# pythonExec uses the system Python because venv creation was skipped.' :
      '# pythonExec points to .venv/bin/python (or Scripts/python.exe on Windows).'
    """# PyGradle configuration (editable; overrides defaults).\n# app_name/app_version are used in versioning and publishing tasks.\n# pythonVersion selects the Python used to create the virtual env.\n${venvNote}\napp_name=${appName}\napp_version=${version}\npythonVersion=${pythonVersion}\npythonExec=${pythonExec}\n"""
  }

  private String pygradleYamlContent(String appName, String version, String pythonVersion, String pythonExec, boolean skipVenv) {
    def venvNote = skipVenv ?
      '# pythonExec uses the system Python because venv creation was skipped.' :
      '# pythonExec points to .venv/bin/python (or Scripts/python.exe on Windows).'
    """# PyGradle configuration (editable; overrides defaults).\n# application.name/version are used in versioning and publishing tasks.\n# python.version selects the Python used to create the virtual env.\n${venvNote}\napplication:\n  name: ${appName}\n  version: ${version}\n\npython:\n  version: ${pythonVersion}\n  executable: ${pythonExec}\n"""
  }

  private String dependenciesContent() {
    """/*
  Python dependencies file (used to generate requirements.txt).

  Examples:
    package: requests, version: latest
    package: numpy, version: 1.26.4, mode: ge
    binary: git, version: latest
    # You can also include raw requirements.txt lines:
    git+https://github.com/user/repo.git@main#egg=demo
    --index-url https://pypi.org/simple
    -r extra-requirements.txt
*/
"""
  }

  private String mainPyContent(String appName) {
    """#!/usr/bin/env python3
# -*- coding: utf-8 -*-

\"\"\"Entry point for running the app.

This file is where your app starts when you run ./gradlew run (or python -m).
\"\"\"

import sys


def main():
    \"\"\"Run the application.\"\"\"
    print('Hello from ${appName}')
    return 0


if __name__ == '__main__':
    sys.exit(main())
"""
  }

  private String applicationPropertiesContent(String appName) {
    """# Application configuration (simple key/value pairs).\n${appName}.message=Hello from ${appName}\n"""
  }

  private String testMainContent() {
    """\"\"\"Unit tests.

This is a starter test to confirm the test harness works.
\"\"\"

import unittest


class TestMain(unittest.TestCase):
    \"\"\"Basic test case.\"\"\"

    def test_sanity(self):
        \"\"\"Verify test harness.\"\"\"
        self.assertTrue(True)


if __name__ == '__main__':
    unittest.main()
"""
  }

  private String testPropertiesContent() {
    """# Test configuration (separate from main config).\nany.property=12345\n"""
  }

  /**
   * Resolve a python executable for a requested version.
   *
   * @param pythonVersion Python version string.
   * @return Python executable command.
   */
  private String resolvePythonExecutable(String pythonVersion) {
    def versionParts = pythonVersion.split('\\.')
    def majorMinor = versionParts.length >= 2 ? "${versionParts[0]}.${versionParts[1]}" : pythonVersion
    def candidates = ["python${majorMinor}", 'python3', 'python']
    for (String candidate : candidates) {
      if (commandExists(candidate)) {
        return candidate
      }
    }
    return 'python3'
  }

  /**
   * Resolve the venv python path for the OS.
   *
   * @param venvDir Venv directory.
   * @return Python executable path.
   */
  private String resolveVenvPythonPath(File venvDir) {
    def os = getExtension().os?.toLowerCase()
    def isWindows = os?.contains('win')
    def relPath = isWindows ? 'Scripts/python.exe' : 'bin/python'
    return new File(venvDir, relPath).path
  }

  /**
   * Create a virtual environment.
   *
   * @param venvDir Venv directory.
   * @param pythonExec Python executable.
   * @param force Overwrite flag.
   */
  private void createVirtualEnv(File venvDir, String pythonExec, boolean force) {
    if (venvDir.exists() && !force) {
      println("Skip existing venv: ${venvDir}")
      return
    }
    if (isDryRun()) {
      println("DRY-RUN: ${pythonExec} -m venv ${venvDir}")
      return
    }
    project.exec {
      commandLine pythonExec, '-m', 'venv', venvDir.path
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
}
