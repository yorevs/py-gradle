package br.com.hhs.pygradle

import org.gradle.api.Project
import org.gradle.internal.os.OperatingSystem

/**
 * Extension for configuring the PyGradle plugin.
 */
class PyGradleExtension {
  /** Root directory for project sources. */
  String sourceRoot

  /** Application entrypoint path. */
  String application

  /** Python path to include main/test sources. */
  String pythonPath

  /** Pylint fail score threshold. */
  double failScore

  /** Version file path. */
  String versionFile

  /** Dependencies definition file. */
  File depsFile

  /** Generated requirements file. */
  File reqsFile

  /** Detected OS name. */
  String os

  /** Detected python executable. */
  String python

  /** User space to install packages. */
  String space

  /** Qt resource compiler binary. */
  String pyrcc

  /** Build tools file. */
  File buildToolsFile

  /** Build tools list (comma-separated). */
  String buildTools

  /** Verbose mode. */
  boolean verbose

  /** Application name. */
  String appName

  /** Application version. */
  String appVersion

  /** Disabled pylint checks. */
  List<String> pylintDisabledChecks

  /** Parsed dependency list. */
  List<Map<String, String>> deps

  /** Parsed system applications list. */
  List<Map<String, String>> apps

  /** PyPI module base URL. */
  String pypiModuleUrl

  /** API docs output directory. */
  String apiDocsDir

  /** Pyproject.toml file path. */
  File projTomlFile

  /** setup.py file path. */
  File setupFile

  /** Docker container names. */
  List<String> containers

  /** Author name. */
  String author

  /** Project site URL. */
  String siteUrl

  /** Enable dry-run mode for destructive tasks. */
  boolean dryRun

  /** Oracle client download URL. */
  String oracleClientUrl

  /** Oracle client app directory name. */
  String oracleClientAppFile

  /** Oracle client library directory. */
  String oracleClientLibDir

  /**
   * Create a new extension instance.
   *
   * @param project The Gradle project hosting this extension.
   */
  PyGradleExtension(Project project) {
    this.sourceRoot = project.layout.projectDirectory.dir('src').asFile.path
    this.application = "${this.sourceRoot}/main/python/__main__.py"
    this.pythonPath = project.findProperty('pythonPath') ?: "${this.sourceRoot}/main:${this.sourceRoot}/test"
    this.failScore = (project.findProperty('failScore') ?: 8.0) as double
    this.versionFile = project.findProperty('versionFile') ?: "${this.sourceRoot}/main/${project.name}/.version"
    this.depsFile = project.file("${project.projectDir}/dependencies.hspd")
    this.reqsFile = project.file("${project.projectDir}/src/main/requirements.txt")
    this.os = OperatingSystem.current().getName()
    this.pyrcc = 'pyrcc5'
    this.buildToolsFile = project.file("${project.projectDir}/buildTools.txt")
    this.buildTools = project.findProperty('buildTools') ?: ''
    this.verbose = Boolean.parseBoolean((project.findProperty('verbose') ?: 'false').toString())
    this.appName = project.findProperty('app_name') ?: project.name
    this.appVersion = project.findProperty('app_version') ?: project.version?.toString()
    this.pylintDisabledChecks = [
      'C0103',
      'C0114',
      'C0115',
      'C0116',
      'C0303',
      'C0411',
      'E0603',
      'E1101',
      'R0801',
      'W0511',
      'W1113',
      'C0301'
    ]
    this.deps = []
    this.apps = []
    this.pypiModuleUrl = project.findProperty('pypiModuleUrl') ?: 'https://pypi.org/pypi'
    this.apiDocsDir = "${project.rootDir}/docs/api"
    this.projTomlFile = project.file("${project.projectDir}/pyproject.toml")
    this.setupFile = project.file("${project.projectDir}/src/main/setup.py")
    this.containers = project.findProperty('containers') ?:
      []
    this.author = project.findProperty('author') ?: ''
    this.siteUrl = project.findProperty('siteUrl') ?: ''
    this.dryRun = Boolean.parseBoolean((project.findProperty('dryRun') ?: 'false').toString())
    this.oracleClientUrl = project.findProperty('oracleClientUrl')
    this.oracleClientAppFile = project.findProperty('oracleClientAppFile')
    this.oracleClientLibDir = project.findProperty('oracleClientLibDir') ?: "${project.rootDir}/src/main/resources/lib"
  }
}
