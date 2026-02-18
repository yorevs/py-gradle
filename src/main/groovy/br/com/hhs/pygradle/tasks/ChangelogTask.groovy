package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.StopActionException
import org.gradle.api.tasks.TaskAction

/**
 * Task to generate a changelog from git tags.
 */
class ChangelogTask extends PyGradleBaseTask {
  /**
   * Create a new changelog task.
   */
  ChangelogTask() {
    group = 'Versioning'
    description = 'Generate a changelog with the commits from last git tag'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void changelog() {
    def out = new ByteArrayOutputStream()
    project.exec {
      commandLine 'git', 'describe', '--tags', '--abbrev=0', 'HEAD^'
      standardOutput = out
    }
    def lastTag = out.toString().trim()
    if (!lastTag) {
      throw new StopActionException('Unable to fetch latest tag')
    }
    out.reset()
    project.exec {
      commandLine 'git', 'log', '--oneline', "--pretty='%h %ad %s'", '--date=short', "${lastTag}..HEAD"
      standardOutput = out
      errorOutput = standardOutput
    }
    println("\nShowing changelog commits from: ${lastTag}")
    println('--------------------------------------------------------------------------------')
    out.toString().readLines().each { line ->
      println(line)
    }
  }
}
