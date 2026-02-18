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

  /** Pip extra index URL (private index). */
  String pipExtraIndexUrl

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

  /** Python version for virtual env creation. */
  String pythonVersion

  /** Python executable override. */
  String pythonExec

  /** Python home override. */
  String pythonHome

  /**
   * Create a new extension instance.
   *
   * @param project The Gradle project hosting this extension.
   */
  PyGradleExtension(Project project) {
    def config = loadConfig(project)
    this.sourceRoot = configValue(config, project, 'sourceRoot') ?: project.layout.projectDirectory.dir('src').asFile.path
    this.application = configValue(config, project, 'application') ?: "${this.sourceRoot}/main/python/__main__.py"
    this.pythonPath = configValue(config, project, 'pythonPath') ?: "${this.sourceRoot}/main:${this.sourceRoot}/test"
    this.failScore = (configValue(config, project, 'failScore') ?: 8.0) as double
    this.versionFile = configValue(config, project, 'versionFile') ?: "${this.sourceRoot}/main/${project.name}/.version"
    this.depsFile = project.file(configValue(config, project, 'depsFile') ?: "${project.projectDir}/dependencies.hspd")
    this.reqsFile = project.file(configValue(config, project, 'reqsFile') ?: "${project.projectDir}/src/main/requirements.txt")
    this.os = OperatingSystem.current().getName()
    this.pyrcc = configValue(config, project, 'pyrcc') ?: 'pyrcc5'
    this.buildToolsFile = project.file(configValue(config, project, 'buildToolsFile') ?: "${project.projectDir}/buildTools.txt")
    this.buildTools = configValue(config, project, 'buildTools') ?: ''
    this.verbose = Boolean.parseBoolean((configValue(config, project, 'verbose') ?: 'false').toString())
    this.appName = configValue(config, project, 'application.name') ?: configValue(config, project, 'app_name') ?: project.name
    this.appVersion = configValue(config, project, 'application.version') ?: configValue(config, project, 'app_version') ?: project.version?.toString()
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
    this.pypiModuleUrl = configValue(config, project, 'pypiModuleUrl') ?: 'https://pypi.org/pypi'
    this.pipExtraIndexUrl = configValue(config, project, 'pip.extraIndexUrl')
    this.apiDocsDir = "${project.rootDir}/docs/api"
    this.projTomlFile = project.file(configValue(config, project, 'projTomlFile') ?: "${project.projectDir}/pyproject.toml")
    this.setupFile = project.file(configValue(config, project, 'setupFile') ?: "${project.projectDir}/src/main/setup.py")
    this.containers = configValue(config, project, 'containers') ?:
      []
    this.author = configValue(config, project, 'author') ?: ''
    this.siteUrl = configValue(config, project, 'siteUrl') ?: ''
    this.dryRun = Boolean.parseBoolean((configValue(config, project, 'dryRun') ?: 'false').toString())
    this.oracleClientUrl = configValue(config, project, 'oracleClientUrl')
    this.oracleClientAppFile = configValue(config, project, 'oracleClientAppFile')
    this.oracleClientLibDir = configValue(config, project, 'oracleClientLibDir') ?: "${project.rootDir}/src/main/resources/lib"
    this.pythonVersion = configValue(config, project, 'python.version') ?: configValue(config, project, 'pythonVersion') ?: '3.11.14'
    this.pythonExec = configValue(config, project, 'python.executable') ?: configValue(config, project, 'pythonExec')
    this.pythonHome = configValue(config, project, 'python.home') ?: configValue(config, project, 'pythonHome')
  }

  /**
   * Load configuration from pygradle.properties or pygradle.yaml.
   *
   * @param project The Gradle project.
   * @return Config map.
   */
  private Map<String, Object> loadConfig(Project project) {
    def config = [:]
    def propsFile = project.file("${project.projectDir}/pygradle.properties")
    if (propsFile.exists()) {
      def props = new Properties()
      propsFile.withInputStream { stream -> props.load(stream) }
      props.each { key, value -> config[key.toString()] = value }
    }

    def yamlFile = project.file("${project.projectDir}/pygradle.yaml")
    if (yamlFile.exists()) {
      def currentSection = null
      yamlFile.eachLine { line ->
        def trimmed = line.trim()
        if (!trimmed || trimmed.startsWith('#')) {
          return
        }
        if (trimmed.endsWith(':') && !trimmed.contains(' ')) {
          currentSection = trimmed.substring(0, trimmed.length() - 1)
          return
        }
        def parts = trimmed.split(':', 2)
        if (parts.length == 2) {
          def key = parts[0].trim()
          def value = parts[1].trim().replaceAll('^\"|\"$', '')
          def fullKey = currentSection ? "${currentSection}.${key}" : key
          config[fullKey] = value
        }
      }
    }

    config
  }

  /**
   * Return the first non-empty config value.
   *
   * @param config Config map.
   * @param project The Gradle project.
   * @param key Config key.
   * @return Config value or null.
   */
  private Object configValue(Map<String, Object> config, Project project, String key) {
    def cfg = config[key]
    if (cfg != null && cfg.toString().trim()) {
      return cfg
    }
    def prop = project.findProperty(key)
    if (prop != null && prop.toString().trim()) {
      return prop
    }
    null
  }
}
