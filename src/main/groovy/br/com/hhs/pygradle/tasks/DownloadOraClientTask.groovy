package br.com.hhs.pygradle.tasks

import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Task to download the Oracle client and driver.
 */
class DownloadOraClientTask extends PyGradleBaseTask {

  /**
   * Create a new Oracle client download task.
   */
  DownloadOraClientTask() {
    group = 'Oracle'
    description = 'Download oracle driver and client from the oracle website'
    onlyIf { !installDir().exists() }
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void downloadClient() {
    def config = resolveOracleConfig()
    def libDir = config.libDir
    def outFile = config.outFile
    def url = config.url
    println('Downloading oracle client and driver')
    println("  From: ${url}")
    println("  Into: ${outFile}")

    if (isDryRun()) {
      println("DRY-RUN: mkdir -p ${libDir}")
      println("DRY-RUN: curl -L -o ${outFile} ${url}")
      return
    }

    project.mkdir(libDir)
    project.exec {
      commandLine 'curl', '-L', '-o', outFile, url
    }
  }

  /**
   * Get the lib directory path.
   *
   * @return Lib directory path.
   */
  private String libDirPath() {
    resolveOracleConfig().libDir
  }

  /**
   * Get the Oracle download URL.
   *
   * @return Download URL.
   */
  @Input
  String getOracleUrl() {
    resolveOracleConfig().url
  }

  /**
   * Get the output zip file.
   *
   * @return Output zip file.
   */
  @OutputFile
  File getOutputZip() {
    new File(resolveOracleConfig().outFile)
  }

  /**
   * Resolve Oracle client configuration based on OS/arch or overrides.
   *
   * @return Config map.
   */
  private Map<String, String> resolveOracleConfig() {
    def extension = getExtension()
    def os = extension.os?.toLowerCase()
    def arch = System.getProperty('os.arch')?.toLowerCase()

    def libDir = extension.oracleClientLibDir ?: "${project.rootDir}/src/main/resources/lib"
    def appFile = extension.oracleClientAppFile
    def url = extension.oracleClientUrl

    if (!appFile || !url) {
      def isMac = os?.contains('mac')
      def isX64 = arch?.contains('x86_64') || arch?.contains('amd64')
      if (isMac && isX64) {
        appFile = appFile ?: 'instantclient_19_3'
        url = url ?: 'https://download.oracle.com/otn_software/mac/instantclient/193000/instantclient-basiclite-macos.x64-19.3.0.0.0dbru.zip'
      } else {
        throw new GradleException("Unsupported OS/arch for Oracle client (${os}/${arch}). Set oracleClientUrl and oracleClientAppFile.")
      }
    }

    return [
      libDir: libDir,
      appFile: appFile,
      outFile: "${libDir}/${appFile}.zip",
      url: url
    ]
  }

  /**
   * Get the install directory.
   *
   * @return Install directory.
   */
  private File installDir() {
    def config = resolveOracleConfig()
    new File("${config.libDir}/${config.appFile}")
  }
}
