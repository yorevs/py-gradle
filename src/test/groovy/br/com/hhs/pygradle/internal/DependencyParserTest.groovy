package br.com.hhs.pygradle.internal

import br.com.hhs.pygradle.PyGradleExtension
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Unit tests for dependency parsing logic.
 */
class DependencyParserTest {
  /**
   * Temporary folder for test files.
   */
  @Rule
  public final TemporaryFolder temp = new TemporaryFolder()

  private PyGradleExtension extension

  /**
   * Set up a minimal Gradle project and extension.
   */
  @Before
  void setUp() {
    def project = ProjectBuilder.builder().build()
    extension = new PyGradleExtension(project)
  }

  /**
   * Parse all supported dependency formats.
   */
  @Test
  void shouldParseDependenciesAndBinaries() {
    def depsFile = temp.newFile('dependencies.hspd')
    depsFile.text = '''
package: hspylib, version: 1.2.3, mode: ge
package: hspylib-clitt, version: 2.3.4
package: simplepkg
binary: curl, version: 8.0.1
binary: jq
'''.trim()

    extension.depsFile = depsFile
    extension.deps = []
    extension.apps = []

    PyGradleUtils.readDependencies(extension)

    assert extension.deps.size() == 3
    assert extension.deps[0] == [package: 'hspylib', version: '1.2.3', mode: 'ge']
    assert extension.deps[1] == [package: 'hspylib-clitt', version: '2.3.4', mode: 'compat']
    assert extension.deps[2] == [package: 'simplepkg', version: 'latest', mode: 'ge']

    assert extension.apps.size() == 2
    assert extension.apps[0] == [binary: 'curl', version: '8.0.1']
    assert extension.apps[1] == [binary: 'jq', version: 'latest']
  }
}
