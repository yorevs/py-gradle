package br.com.hhs.pygradle.tasks

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
    into "${project.extensions.getByType(br.com.hhs.pygradle.PyGradleExtension).sourceRoot}/main"
  }
}
