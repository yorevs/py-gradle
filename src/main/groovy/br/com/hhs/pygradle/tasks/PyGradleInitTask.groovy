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
    def appName = project.findProperty('initAppName') ?: extension.appName ?: project.name
    def rootName = project.findProperty('initRootName') ?: project.name
    def groupId = project.findProperty('initGroup') ?: project.group ?: 'com.example'
    def version = project.findProperty('initVersion') ?: project.version ?: '0.1.0'

    def files = [
      [path: 'settings.gradle', content: settingsGradleContent(rootName)],
      [path: 'build.gradle', content: buildGradleContent()],
      [path: 'gradle.properties', content: gradlePropertiesContent(appName, version)],
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

    dirs.each { dir -> ensureDir(dir, force) }
    files.each { file -> writeFile(file.path, file.content, force) }

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
    """rootProject.name = '${rootName}'\n"""
  }

  private String buildGradleContent() {
    """plugins {
  id 'br.com.hhs.pygradle'
}
"""
  }

  private String gradlePropertiesContent(String appName, String version) {
    """app_name      = ${appName}
app_version   = ${version}
"""
  }

  private String dependenciesContent() {
    """/*
  Python dependencies file
*/

"""
  }

  private String mainPyContent(String appName) {
    """#!/usr/bin/env python3
# -*- coding: utf-8 -*-

\"\"\"Entry point.\"\"\"

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
    """${appName}.message=Hello from ${appName}\n"""
  }

  private String testMainContent() {
    """\"\"\"Unit tests.\"\"\"

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
    """any.property=12345\n"""
  }
}
