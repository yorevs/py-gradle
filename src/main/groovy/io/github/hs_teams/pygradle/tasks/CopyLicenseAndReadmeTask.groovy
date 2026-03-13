package io.github.hs_teams.pygradle.tasks

import org.gradle.api.tasks.Copy

/**
 * Task to copy LICENSE and README files into the main folder.
 */
class CopyLicenseAndReadmeTask extends Copy {
  /**
   * Create a new copy task.
   */
  CopyLicenseAndReadmeTask() {
    group = 'Publish'
    description = 'Copy LICENSE file into main folder'
    from(project.rootDir) {
      include 'LICENSE*'
      include 'README*'
    }
    into "${project.extensions.getByType(io.github.hs_teams.pygradle.PyGradleExtension).sourceRoot}/main"
  }
}
