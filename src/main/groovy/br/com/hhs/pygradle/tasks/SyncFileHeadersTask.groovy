package br.com.hhs.pygradle.tasks

import br.com.hhs.pygradle.internal.PyGradleUtils
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory

/**
 * Task to synchronize Python file headers.
 */
class SyncFileHeadersTask extends PyGradleBaseTask {
  /**
   * Create a new sync file headers task.
   */
  SyncFileHeadersTask() {
    group = 'Documentation'
    description = 'Synchronize python file headers (doc strings)'
    finalizedBy 'optimizeImports'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void syncFileHeaders() {
    def extension = getExtension()
    def replaceHeaders = project.hasProperty('replaceHeaders')
    println("Synchronizing python headers replace: ${replaceHeaders}")
    def files = PyGradleUtils.filesByPattern(project, extension.sourceRoot, /.*(?<!__init__)\.py$/)
    def headerTemplate = buildPythonHeaderTemplate()

    files.each { File file ->
      println("Processing file: ${file}")
      def headers = processPythonHeader(file, headerTemplate)
      def parseState = 'headers'
      def imports = []
      def code = []
      def oldHeaders = []
      def hasOldHeaders = false
      file.eachLine { String line ->
        if ('headers' == parseState && !(line ==~ /^(import|from) .*/)) {
          oldHeaders << line
          if (!replaceHeaders && line.startsWith('"""')) {
            hasOldHeaders = true
          }
        } else if ('imports' == parseState || line ==~ /^(import|from .* import) .*/) {
          imports << line
          if (line.trim().endsWith('\\') || line.trim().endsWith(',') || line.trim().endsWith('(')) {
            parseState = 'imports'
          } else {
            parseState = parseState == 'headers' ? 'code' : parseState
          }
        } else if ('code' == parseState || !(line.isEmpty() && line ==~ /^(import|from .* import) .*/)) {
          code << line
        }
      }
      file.setText("${hasOldHeaders ? oldHeaders.join('\n') : headers}\n${imports.join('\n')}${code.join('\n')}\n")
    }
    println('Finished processing all python files')
  }

  /**
   * Build the python header template.
   *
   * @return Header template string.
   */
  private String buildPythonHeaderTemplate() {
    def extension = getExtension()
    def year = new SimpleDateFormat('yyyy').format(new Date())
    def curDate = new SimpleDateFormat('EEE, d MMM yyyy').format(new Date())

    return """#!/usr/bin/env python3
# -*- coding: utf-8 -*-

\"\"\"
   @project: ${extension.appName}
   @package: %package%
      @file: %filename%
   @created: ${curDate}
    @author: ${extension.author}"
      @site: ${extension.siteUrl}
   @license: MIT - Please refer to <https://opensource.org/licenses/MIT>

   Copyright ${year}, HSPyLib team
\"\"\"
"""
  }

  /**
   * Get the package name for a file.
   *
   * @param file The input file.
   * @return Package name.
   */
  private String getPackageName(File file) {
    def extension = getExtension()
    file.getParentFile()
      .getCanonicalPath()
      .replaceAll("${extension.sourceRoot}", extension.appName.toLowerCase())
      .replaceAll('\\/', '\\.')
  }

  /**
   * Build the python header for a file.
   *
   * @param file The input file.
   * @param headerTemplate Header template.
   * @return Header string.
   */
  private String processPythonHeader(File file, String headerTemplate) {
    def pkg = getPackageName(file)
    headerTemplate
      .replaceAll('%filename%', file.getName())
      .replaceAll('%package%', pkg)
  }

  /**
   * Get the source root directory.
   *
   * @return Source root directory.
   */
  @InputDirectory
  File getSourceRootDir() {
    project.file(getExtension().sourceRoot)
  }

  /**
   * Get the output directory for updated sources.
   *
   * @return Output directory.
   */
  @OutputDirectory
  File getOutputRootDir() {
    project.file(getExtension().sourceRoot)
  }
}
