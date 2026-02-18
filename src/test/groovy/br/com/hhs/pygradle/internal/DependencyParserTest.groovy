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
--index-url https://pypi.org/simple
git+https://github.com/user/repo.git@main#egg=demo
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
    assert extension.requirementLines.contains('--index-url https://pypi.org/simple')
    assert extension.requirementLines.contains('git+https://github.com/user/repo.git@main#egg=demo')
  }

  /**
   * Ignore block comments while still parsing valid entries.
   */
  @Test
  void shouldIgnoreBlockComments() {
    def depsFile = temp.newFile('dependencies.hspd')
    depsFile.text = '''
/*
package: badpkg, version: 0.0.1
* still comment
*/
package: hspylib, version: 1.2.3 /* inline comment */
binary: curl /* inline comment */
/* one-line comment */ package: simplepkg
'''.trim()

    extension.depsFile = depsFile
    extension.deps = []
    extension.apps = []

    PyGradleUtils.readDependencies(extension)

    assert extension.deps.size() == 2
    assert extension.deps[0] == [package: 'hspylib', version: '1.2.3', mode: 'compat']
    assert extension.deps[1] == [package: 'simplepkg', version: 'latest', mode: 'ge']
    assert extension.apps.size() == 1
    assert extension.apps[0] == [binary: 'curl', version: 'latest']
  }

  /**
   * Handle multiple block comments on a single line.
   */
  @Test
  void shouldIgnoreMultipleBlockCommentsOnLine() {
    def depsFile = temp.newFile('dependencies.hspd')
    depsFile.text = '''
package: alpha/* c1 */, version: 1.0.0/* c2 */, mode: ge
/* c3 */ package: beta/* c4 */
'''.trim()

    extension.depsFile = depsFile
    extension.deps = []
    extension.apps = []

    PyGradleUtils.readDependencies(extension)

    assert extension.deps.size() == 2
    assert extension.deps[0] == [package: 'alpha', version: '1.0.0', mode: 'ge']
    assert extension.deps[1] == [package: 'beta', version: 'latest', mode: 'ge']
  }

  /**
   * Verify pip help detection of --break-system-packages.
   */
  @Test
  void shouldDetectBreakSystemPackagesFlag() {
    def helpText = '  --break-system-packages  Allow pip to modify system packages'
    assert PyGradleUtils.supportsBreakSystemPackages(helpText)
  }

  /**
   * Verify pip help detection when --break-system-packages is absent.
   */
  @Test
  void shouldHandleMissingBreakSystemPackagesFlag() {
    def helpText = 'Options:\n  --user  Install to the Python user install directory.'
    assert !PyGradleUtils.supportsBreakSystemPackages(helpText)
  }

  /**
   * Verify venv directory detection from python executable.
   */
  @Test
  void shouldDetectVenvDir() {
    def unixExec = '/tmp/project/.venv/bin/python'
    def winExec = 'C:\\project\\.venv\\Scripts\\python.exe'
    def unixVenv = PyGradleUtils.detectVenvDir(unixExec)
    def winVenv = PyGradleUtils.detectVenvDir(winExec)

    assert unixVenv.path.replace('\\', '/') == '/tmp/project/.venv'
    assert winVenv.path.replace('\\', '/') == 'C:/project/.venv'
  }
}
