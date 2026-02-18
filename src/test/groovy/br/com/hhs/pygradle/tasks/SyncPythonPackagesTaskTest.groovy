package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.PyGradleExtension
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Tests for SyncPythonPackagesTask.
 */
class SyncPythonPackagesTaskTest {
  /**
   * Temporary folder for test data.
   */
  @Rule
  public final TemporaryFolder temp = new TemporaryFolder()

  /**
   * Verify __init__.py includes import helpers for class exports.
   */
  @Test
  void shouldGenerateImportHelpers() {
    def projectDir = temp.newFolder('pkg-project')
    def srcRoot = new File(projectDir, 'src')
    def pkgDir = new File(srcRoot, 'main/demo_pkg')
    pkgDir.mkdirs()

    def versionFile = new File(pkgDir, '.version')
    versionFile.text = '0.0.1'

    def initFile = new File(pkgDir, '__init__.py')
    initFile.text = '# stub'

    def moduleFile = new File(pkgDir, 'inventory_instance.py')
    moduleFile.text = 'class InventoryInstance:\n  pass\n'

    def subPkgDir = new File(pkgDir, 'screens')
    subPkgDir.mkdirs()
    def subInitFile = new File(subPkgDir, '__init__.py')
    subInitFile.text = '# stub'
    def subModuleFile = new File(subPkgDir, 'chat_screen.py')
    subModuleFile.text = 'class ChatScreen:\n  pass\n\nVERSION_ID = "v1"\n'

    def project = ProjectBuilder.builder().withProjectDir(projectDir).build()
    def extension = project.extensions.create('pyGradle', PyGradleExtension, project)
    extension.sourceRoot = srcRoot.path
    extension.versionFile = versionFile.path

    def task = project.tasks.create('syncPythonPackages', SyncPythonPackagesTask)
    task.syncPythonPackages()

    def contents = initFile.text
    assert contents.contains('from .inventory_instance import InventoryInstance')
    assert contents.contains('from .screens.chat_screen import ChatScreen')
    assert contents.contains("_EXPORTS")
    assert contents.contains("inventory_instance")
    assert contents.contains("InventoryInstance")
    assert contents.contains("screens.chat_screen")
    assert contents.contains("ChatScreen")
    assert contents.contains("VERSION_ID")
  }
}
