package br.com.hhs.pygradle.tasks

import org.gradle.api.GradleException
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task to install the Oracle client and driver.
 */
class InstallOraClientTask extends PyGradleBaseTask {

  /**
   * Create a new Oracle client install task.
   */
  InstallOraClientTask() {
    group = 'Oracle'
    description = 'Install oracle driver and client into the system'
    dependsOn 'downloadOraClient'
    onlyIf { !installDir().exists() }
  }

  /**
   * Execute the task action.
   */
  @TaskAction
  void installClient() {
    def config = resolveOracleConfig()
    def libDir = config.libDir
    def outFile = config.outFile
    println('Extracting oracle client and driver')
    println("  From: ${outFile}")
    println("  Into: ${libDir}")

    if (isDryRun()) {
      println("DRY-RUN: unzip ${outFile} -d ${libDir}")
      return
    }

    project.copy {
      from project.zipTree(outFile)
      into libDir
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
   * Get the input zip file.
   *
   * @return Zip file.
   */
  @InputFile
  File getInputZip() {
    new File(resolveOracleConfig().outFile)
  }

  /**
   * Get the output install directory.
   *
   * @return Install directory.
   */
  @OutputDirectory
  File getInstallDir() {
    installDir()
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
