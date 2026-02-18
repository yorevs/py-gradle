package br.com.hhs.pygradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

/**
 * Functional smoke tests using Gradle TestKit.
 */
class PluginSmokeTest {
  /**
   * Temporary folder for test projects.
   */
  @Rule
  public final TemporaryFolder temp = new TemporaryFolder()

  /**
   * Verify that the plugin registers listDependencies successfully.
   */
  @Test
  void shouldRunListDependenciesTask() {
    def projectDir = temp.newFolder('smoke-project')
    def settingsFile = new File(projectDir, 'settings.gradle')
    settingsFile.text = "rootProject.name = 'smoke-project'\n"

    def buildFile = new File(projectDir, 'build.gradle')
    buildFile.text = """
plugins {
  id 'br.com.hhs.pygradle'
}
""".trim() + "\n"

    def depsFile = new File(projectDir, 'dependencies.hspd')
    depsFile.text = 'package: demo, version: 1.0.0, mode: ge\n'

    def result = GradleRunner.create()
      .withProjectDir(projectDir)
      .withArguments('listDependencies')
      .withPluginClasspath()
      .build()

    assert result.task(':listDependencies').outcome == SUCCESS
    assert result.output.contains('Listing dependencies from')
    assert result.output.contains('Package: demo, Version: 1.0.0, Mode: ge')
  }

  /**
   * Verify that syncRequirements generates a requirements file.
   */
  @Test
  void shouldGenerateRequirementsFile() {
    def projectDir = temp.newFolder('sync-project')
    def settingsFile = new File(projectDir, 'settings.gradle')
    settingsFile.text = "rootProject.name = 'sync-project'\n"

    def buildFile = new File(projectDir, 'build.gradle')
    buildFile.text = """
plugins {
  id 'br.com.hhs.pygradle'
}
""".trim() + "\n"

    def depsFile = new File(projectDir, 'dependencies.hspd')
    depsFile.text = '''
package: demo, version: 1.2.3, mode: ge
package: other, version: latest
'''.trim() + "\n"
    new File(projectDir, 'src/main').mkdirs()

    def result = GradleRunner.create()
      .withProjectDir(projectDir)
      .withArguments('syncRequirements')
      .withPluginClasspath()
      .build()

    assert result.task(':syncRequirements').outcome == SUCCESS

    def requirementsFile = new File(projectDir, 'src/main/requirements.txt')
    assert requirementsFile.exists()
    def contents = requirementsFile.text
    assert contents.contains('demo>=1.2.3')
    assert contents.contains('other')
  }

  /**
   * Verify the plugin registers the ported tasks in task listing.
   */
  @Test
  void shouldListPortedTasks() {
    def projectDir = temp.newFolder('tasks-project')
    def settingsFile = new File(projectDir, 'settings.gradle')
    settingsFile.text = "rootProject.name = 'tasks-project'\n"

    def buildFile = new File(projectDir, 'build.gradle')
    buildFile.text = """
plugins {
  id 'br.com.hhs.pygradle'
}
""".trim() + "\n"

    def result = GradleRunner.create()
      .withProjectDir(projectDir)
      .withArguments('tasks', '--all')
      .withPluginClasspath()
      .build()

    assert result.output.contains('Versioning tasks')
    assert result.output.contains('patchVersion')
    assert result.output.contains('Publish tasks')
    assert result.output.contains('sdist')
    assert result.output.contains('Docker tasks')
    assert result.output.contains('isDockerUp')
    assert result.output.contains('Poetry tasks')
    assert result.output.contains('syncPyProject')
    assert result.output.contains('Documentation tasks')
    assert result.output.contains('autoDocApi')
    assert result.output.contains('Idea tasks')
    assert result.output.contains('exportRunConfigurations')
  }

  /**
   * Verify that cleanDist removes dist/build artifacts.
   */
  @Test
  void shouldCleanDistArtifacts() {
    def projectDir = temp.newFolder('clean-project')
    def settingsFile = new File(projectDir, 'settings.gradle')
    settingsFile.text = "rootProject.name = 'clean-project'\n"

    def buildFile = new File(projectDir, 'build.gradle')
    buildFile.text = """
plugins {
  id 'br.com.hhs.pygradle'
}
""".trim() + "\n"

    def buildDist = new File(projectDir, 'build/dist')
    def buildBuild = new File(projectDir, 'build/build')
    def srcDist = new File(projectDir, 'src/dist')
    buildDist.mkdirs()
    buildBuild.mkdirs()
    srcDist.mkdirs()
    new File(buildDist, 'file.txt').text = 'x'
    new File(buildBuild, 'file.txt').text = 'x'
    new File(srcDist, 'file.txt').text = 'x'

    def result = GradleRunner.create()
      .withProjectDir(projectDir)
      .withArguments('cleanDist')
      .withPluginClasspath()
      .build()

    assert result.task(':cleanDist').outcome == SUCCESS
    assert !buildDist.exists()
    assert !buildBuild.exists()
    assert !srcDist.exists()
  }

  /**
   * Verify pyGradleInit scaffolds core files.
   */
  @Test
  void shouldScaffoldProjectFiles() {
    def projectDir = temp.newFolder('init-project')
    def settingsFile = new File(projectDir, 'settings.gradle')
    settingsFile.text = "rootProject.name = 'init-project'\n"

    def buildFile = new File(projectDir, 'build.gradle')
    buildFile.text = """
plugins {
  id 'br.com.hhs.pygradle'
}
""".trim() + "\n"

    def result = GradleRunner.create()
      .withProjectDir(projectDir)
      .withArguments('pyGradleInit', '-PskipVenv=true')
      .withPluginClasspath()
      .build()

    assert result.task(':pyGradleInit').outcome == SUCCESS
    def pygradleProps = new File(projectDir, 'pygradle.properties')
    assert pygradleProps.exists()
    assert pygradleProps.text.contains('pythonVersion=')
    assert pygradleProps.text.contains('pythonExec=')
    assert new File(projectDir, 'pygradle.yaml').exists()
    assert !new File(projectDir, '.venv').exists()
    assert new File(projectDir, 'dependencies.hspd').exists()
    assert new File(projectDir, 'src/main/python/__main__.py').exists()
    assert new File(projectDir, 'src/test/test_main.py').exists()
  }

  /**
   * Verify pyGradleInit uses the requested pythonVersion.
   */
  @Test
  void shouldRespectPythonVersionOverride() {
    def projectDir = temp.newFolder('init-python-version')
    def settingsFile = new File(projectDir, 'settings.gradle')
    settingsFile.text = "rootProject.name = 'init-python-version'\n"

    def buildFile = new File(projectDir, 'build.gradle')
    buildFile.text = """
plugins {
  id 'br.com.hhs.pygradle'
}
""".trim() + "\n"

    def result = GradleRunner.create()
      .withProjectDir(projectDir)
      .withArguments('pyGradleInit', '-PskipVenv=true', '-PpythonVersion=3.12.1')
      .withPluginClasspath()
      .build()

    assert result.task(':pyGradleInit').outcome == SUCCESS
    def pygradleProps = new File(projectDir, 'pygradle.properties')
    assert pygradleProps.text.contains('pythonVersion=3.12.1')
  }

  /**
   * Verify pygradle.yaml overrides are applied.
   */
  @Test
  void shouldUsePyGradleYamlOverrides() {
    def projectDir = temp.newFolder('yaml-overrides')
    def settingsFile = new File(projectDir, 'settings.gradle')
    settingsFile.text = "rootProject.name = 'yaml-overrides'\n"

    def yamlFile = new File(projectDir, 'pygradle.yaml')
    yamlFile.text = "python:\n  executable: python3\n"

    def buildFile = new File(projectDir, 'build.gradle')
    buildFile.text = """
plugins {
  id 'br.com.hhs.pygradle'
}

task printPythonExec {
  doLast {
    println("PYTHON_EXEC=" + project.ext.python)
  }
}
""".trim() + "\n"

    def result = GradleRunner.create()
      .withProjectDir(projectDir)
      .withArguments('printPythonExec')
      .withPluginClasspath()
      .build()

    assert result.task(':printPythonExec').outcome == SUCCESS
    assert result.output.contains('PYTHON_EXEC=python3')
  }
}
