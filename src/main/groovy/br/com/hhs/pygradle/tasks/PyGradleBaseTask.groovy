package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.PyGradleExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal

/**
 * Base class for PyGradle tasks.
 */
abstract class PyGradleBaseTask extends DefaultTask {
  /**
   * Get the configured PyGradle extension.
   *
   * @return Extension instance.
   */
  @Internal
  protected PyGradleExtension getExtension() {
    project.extensions.getByType(PyGradleExtension)
  }

  /**
   * Check if dry-run is enabled.
   *
   * @return True when dry-run is enabled.
   */
  @Internal
  protected boolean isDryRun() {
    getExtension().dryRun
  }
}
