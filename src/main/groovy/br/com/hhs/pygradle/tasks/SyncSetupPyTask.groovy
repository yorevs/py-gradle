package br.com.hhs.pygradle.tasks

import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Task to synchronize pyproject.toml with setup.py metadata.
 */
class SyncSetupPyTask extends PyGradleBaseTask {
  /**
   * Create a new sync setup.py task.
   */
  SyncSetupPyTask() {
    group = 'Poetry'
    description = 'Gather project project information from setup.py file'
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void syncSetupPy() {
    def extension = getExtension()
    def setupFile = extension.setupFile
    def pyprojectFile = extension.projTomlFile

    def setupContent = setupFile.text

    def nameMatch = (setupContent =~ /name=['"](.+?)['"]/)
    def versionMatch = (setupContent =~ /version=['"](.+?)['"]/)
    def descriptionMatch = (setupContent =~ /description=['"](.+?)['"]/)
    def authorMatch = (setupContent =~ /author=['"](.+?)['"]/)
    def authorEmailMatch = (setupContent =~ /author_email=['"](.+?)['"]/)
    def licenseMatch = (setupContent =~ /license=['"](.+?)['"]/)
    def readmeMatch = (setupContent =~ /README\s*=\s*\(HERE\s*\/\s*["'](.+?)["']\)\.read_text\(\)/)
    def urlMatch = (setupContent =~ /url=['"](.+?)['"]/)
    def classifiersMatch = (setupContent =~ /classifiers=\[\s*(.*?)\s*\]/)
    def projectUrlsMatch = (setupContent =~ /project_urls\s*=\s*\{([^}]+)\}/)

    def setupData = [:]
    if (nameMatch) setupData['name'] = nameMatch[0][1]
    if (versionMatch) setupData['version'] = versionMatch[0][1]
    if (descriptionMatch) setupData['description'] = descriptionMatch[0][1]
    if (authorMatch) setupData['authors'] = [authorMatch[0][1]]
    if (authorEmailMatch) setupData['authors'][0] += " <${authorEmailMatch[0][1]}>"
    if (licenseMatch) setupData['license'] = licenseMatch[0][1]
    if (readmeMatch) setupData['readme'] = readmeMatch[0][1]
    if (urlMatch) setupData['repository'] = urlMatch[0][1]

    if (classifiersMatch) {
      def classifiersRaw = classifiersMatch[0][1]
      def classifiers = classifiersRaw.split(/['"]?\s*,\s*['"]?/).collect { it.trim().replaceAll(/['"]/, '') }
      setupData['classifiers'] = classifiers.findAll { it }
    }

    def projectUrls = [:]
    if (projectUrlsMatch) {
      def urlsRaw = projectUrlsMatch[0][1].trim()
      def urlLines = urlsRaw.split(/,\s*/)
      urlLines.each { line ->
        def parts = line.split(/:\s+/, 2)
        if (parts.length == 2) {
          def key = parts[0].trim().replaceAll(/['"]/, '')
          def value = parts[1].trim().replaceAll(/['"]/, '')
          projectUrls[key] = value
        }
      }
    }

    def pyproject = pyprojectFile.text

    def poetrySectionRegex = /(?s)\[tool\.poetry\](.*?)(?=\n\[|\z)/
    pyproject = pyproject.replaceAll(poetrySectionRegex) { match, content ->
      def newPoetrySection = "[tool.poetry]\n"
      setupData.each { key, value ->
        if (key == 'authors') {
          newPoetrySection += "  ${key} = [\"${value[0]}\"]\n"
        } else if (key == 'classifiers') {
          newPoetrySection += "  ${key} = [\n"
          value.each { classifier ->
            newPoetrySection += "    \"${classifier}\",\n"
          }
          newPoetrySection += "  ]\n"
        } else {
          newPoetrySection += "  ${key} = \"${value}\"\n"
        }
      }
      return newPoetrySection
    }

    def urlsSectionRegex = /(?s)\[tool\.poetry\.urls\](.*?)(?=\n\[|\z)/
    def newUrlsSection = "[tool.poetry.urls]\n\n"
    projectUrls.each { key, value ->
      newUrlsSection += "${key} = \"${value}\"\n"
    }

    pyproject = pyproject.replaceAll(urlsSectionRegex, newUrlsSection)

    pyprojectFile.text = pyproject

    println('The [tool.poetry] section has been synchronized with setup.py.')
  }

  /**
   * Get the setup.py file.
   *
   * @return Setup file.
   */
  @InputFile
  File getSetupFile() {
    getExtension().setupFile
  }

  /**
   * Get the pyproject.toml file.
   *
   * @return Pyproject file.
   */
  @OutputFile
  File getPyprojectFile() {
    getExtension().projTomlFile
  }
}
