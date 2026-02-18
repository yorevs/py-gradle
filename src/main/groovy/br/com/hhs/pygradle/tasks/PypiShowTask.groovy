package br.com.hhs.pygradle.tasks

import groovy.json.JsonSlurper
import org.gradle.api.tasks.TaskAction

/**
 * Task to show PyPI module details.
 */
class PypiShowTask extends PyGradleBaseTask {
  /**
   * Create a new PyPI show task.
   */
  PypiShowTask() {
    group = 'Publish'
    description = 'Show PyPi module details'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void pypiShow() {
    def extension = getExtension()
    def tempDir = System.getenv('TEMP') ?: '/tmp'
    def url = extension.pypiModuleUrl + "/${extension.appName}/json"
    def outFile = "${tempDir}/${extension.appName}-info.json"
    println("PyPi Instance: ${url}")
    project.exec {
      commandLine 'curl', '-s', '-o', outFile, url
    }
    def json = new JsonSlurper().parseText(new File(outFile).getText())
    println('\n--------------------------------------------------------------------------------')
    println("|-AppName: ${json.info.package_url}")
    println("|-Summary: ${json.info.summary}")
    println("|-Version: ${json.info.version}")
    println("|-License: ${json.info.license}")
    println("|-Python: ${json.info.requires_python}")
    println("|-Keywords: \n  ${json.info.keywords ? '#' + json.info.keywords.split(',').join(' #') : 'None'}")
    println("|-Classifiers: \n  ${json.info.classifiers ? '|-' + json.info.classifiers.join('\n  |-') : 'None'}")
    println("|-Dependencies: \n  ${json.info.requires_dist ? '|-' + json.info.requires_dist.join('\n  |-') : 'None'} ")
    println('--------------------------------------------------------------------------------')
  }
}
