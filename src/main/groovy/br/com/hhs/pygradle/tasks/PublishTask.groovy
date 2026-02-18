package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.TaskAction

/**
 * Task to publish a new version via git tagging and pushing.
 */
class PublishTask extends PyGradleBaseTask {
  /**
   * Create a new publish task.
   */
  PublishTask() {
    group = 'Publish'
    description = 'Publish the module to PyPi repository'
    if (!project.hasProperty('no-patch') || !Boolean.valueOf(project.getProperty('no-patch'))) {
      dependsOn 'patchVersion'
    }
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void publish() {
    def extension = getExtension()
    def out = new ByteArrayOutputStream()
    def version = new File(extension.versionFile).text
    println("Publishing HomeSetup v${version} ...")
    runCommand(['git', 'log', '--pretty=format:%s', 'origin/master..HEAD'], out)
    def tag = "v${extension.appVersion}"
    def commitLogs = out.toString()
    def prefix = commitLogs?.trim() ? commitLogs?.split('\n').size() + ' commits' : ' No commits'
    def commitMsg = "New ${extension.appName} revision: '${tag}' >> ${prefix}"
    println("Publishing ${extension.appName}...")
    println("Revision commits: ${commitMsg}  Tag: ${tag}")
    runCommand(['git', 'tag', '-a', tag, '-m', "New ${extension.appName} revision ${tag}"])
    runCommand(['git', 'push', 'origin', tag])
    runCommand(['git', 'add', '-A', ':/'])
    runCommand(['git', 'commit', '-m', commitMsg])
    runCommand(['git', 'push', 'origin', 'HEAD'])
  }

  /**
   * Run or print a command based on dry-run settings.
   *
   * @param args Command arguments.
   * @param out Optional output stream.
   */
  private void runCommand(List<String> args, OutputStream out = null) {
    if (isDryRun()) {
      println("DRY-RUN: ${args.join(' ')}")
      return
    }
    project.exec {
      commandLine args
      if (out != null) {
        standardOutput = out
      }
    }
  }
}
