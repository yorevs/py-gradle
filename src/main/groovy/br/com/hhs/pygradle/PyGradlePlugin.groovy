package br.com.hhs.pygradle

import br.com.hhs.pygradle.internal.PyGradleUtils
import br.com.hhs.pygradle.tasks.AutoDocApiTask
import br.com.hhs.pygradle.tasks.ChangelogTask
import br.com.hhs.pygradle.tasks.CheckDistTask
import br.com.hhs.pygradle.tasks.CheckTask
import br.com.hhs.pygradle.tasks.CleanDistTask
import br.com.hhs.pygradle.tasks.CleanPythonTask
import br.com.hhs.pygradle.tasks.CompilePythonTask
import br.com.hhs.pygradle.tasks.CompileQrcTask
import br.com.hhs.pygradle.tasks.CopyLicenseAndReadmeTask
import br.com.hhs.pygradle.tasks.DownloadOraClientTask
import br.com.hhs.pygradle.tasks.FindContainersTask
import br.com.hhs.pygradle.tasks.FreezeRequirementsTask
import br.com.hhs.pygradle.tasks.InstallBinariesTask
import br.com.hhs.pygradle.tasks.InstallBuildToolsTask
import br.com.hhs.pygradle.tasks.InstallModuleTask
import br.com.hhs.pygradle.tasks.InstallPackagesTask
import br.com.hhs.pygradle.tasks.InstallTask
import br.com.hhs.pygradle.tasks.InstallPyGradleToolsTask
import br.com.hhs.pygradle.tasks.InstallOraClientTask
import br.com.hhs.pygradle.tasks.IsDockerUpTask
import br.com.hhs.pygradle.tasks.ListContainersTask
import br.com.hhs.pygradle.tasks.ListDependenciesTask
import br.com.hhs.pygradle.tasks.ListPropertiesTask
import br.com.hhs.pygradle.tasks.MypyTask
import br.com.hhs.pygradle.tasks.OptimizeImportsTask
import br.com.hhs.pygradle.tasks.PatchVersionTask
import br.com.hhs.pygradle.tasks.PoetryBuildTask
import br.com.hhs.pygradle.tasks.PoetryInstallTask
import br.com.hhs.pygradle.tasks.PoetryPublishTask
import br.com.hhs.pygradle.tasks.PylintTask
import br.com.hhs.pygradle.tasks.PypiShowTask
import br.com.hhs.pygradle.tasks.PublishTask
import br.com.hhs.pygradle.tasks.ReformatCodeTask
import br.com.hhs.pygradle.tasks.RemovePackagesTask
import br.com.hhs.pygradle.tasks.SdistTask
import br.com.hhs.pygradle.tasks.StartAllContainersTask
import br.com.hhs.pygradle.tasks.SyncBuildSystemRequirementsTask
import br.com.hhs.pygradle.tasks.SyncBuildToolsTask
import br.com.hhs.pygradle.tasks.SyncDevDependenciesTask
import br.com.hhs.pygradle.tasks.SyncFileHeadersTask
import br.com.hhs.pygradle.tasks.SyncPythonPackagesTask
import br.com.hhs.pygradle.tasks.SyncPyProjectTask
import br.com.hhs.pygradle.tasks.SyncRequirementsTask
import br.com.hhs.pygradle.tasks.SyncSetupPyTask
import br.com.hhs.pygradle.tasks.StopAllContainersTask
import br.com.hhs.pygradle.tasks.UninstallTask
import br.com.hhs.pygradle.tasks.UpdateMajorTask
import br.com.hhs.pygradle.tasks.UpdateMinorTask
import br.com.hhs.pygradle.tasks.ValidateEnvironmentTask
import br.com.hhs.pygradle.tasks.VersionTask
import br.com.hhs.pygradle.tasks.PyGradleInitTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete

/**
 * Gradle plugin entry point for PyGradle.
 */
class PyGradlePlugin implements Plugin<Project> {
  /**
   * Apply the plugin to the given project.
   *
   * @param project The target Gradle project.
   */
  @Override
  void apply(Project project) {
    def extension = project.extensions.create('pyGradle', PyGradleExtension, project)
    extension.python = PyGradleUtils.detectPython(project, extension.pythonExec, extension.pythonHome)
    extension.space = PyGradleUtils.detectSpace(project, extension.python)

    project.ext.set('sourceRoot', extension.sourceRoot)
    project.ext.set('application', extension.application)
    project.ext.set('pythonPath', extension.pythonPath)
    project.ext.set('buildToolsFile', extension.buildToolsFile)
    project.ext.set('startTime', System.currentTimeMillis())
    project.ext.set('verbose', extension.verbose)
    project.ext.set('depsFile', extension.depsFile.toString())
    project.ext.set('reqsFile', extension.reqsFile.toString())
    project.ext.set('os', extension.os)
    project.ext.set('python', extension.python)
    project.ext.set('space', extension.space)
    project.ext.set('pyrcc', extension.pyrcc)
    project.ext.set('deps', extension.deps)
    project.ext.set('apps', extension.apps)
    project.ext.set('failScore', extension.failScore)
    project.ext.set('versionFile', extension.versionFile)
    project.ext.set('pylint_disabled_checks', extension.pylintDisabledChecks)
    project.ext.set('app_name', extension.appName)
    project.ext.set('app_version', extension.appVersion)
    project.ext.set('pypiModuleUrl', extension.pypiModuleUrl)
    project.ext.set('pipExtraIndexUrl', extension.pipExtraIndexUrl)
    project.ext.set('apiDocsDir', extension.apiDocsDir)
    project.ext.set('projTomlFile', extension.projTomlFile.toString())
    project.ext.set('setupFile', extension.setupFile.toString())
    project.ext.set('containers', extension.containers)
    project.ext.set('author', extension.author)
    project.ext.set('siteUrl', extension.siteUrl)
    project.ext.set('oracleClientUrl', extension.oracleClientUrl)
    project.ext.set('oracleClientAppFile', extension.oracleClientAppFile)
    project.ext.set('oracleClientLibDir', extension.oracleClientLibDir)

    project.tasks.register('listDependencies', ListDependenciesTask)
    project.tasks.register('syncRequirements', SyncRequirementsTask)
    project.tasks.register('installPackages', InstallPackagesTask)
    project.tasks.register('removePackages', RemovePackagesTask)
    project.tasks.register('freezeRequirements', FreezeRequirementsTask)
    project.tasks.register('installBinaries', InstallBinariesTask)
    project.tasks.register('downloadOraClient', DownloadOraClientTask)
    project.tasks.register('installOraClient', InstallOraClientTask)

    project.tasks.register('cleanPython', CleanPythonTask)
    project.tasks.register('compilePython', CompilePythonTask)
    project.tasks.register('compileQrc', CompileQrcTask)
    project.tasks.register('syncBuildTools', SyncBuildToolsTask)
    project.tasks.register('installBuildTools', InstallBuildToolsTask)
    project.tasks.register('syncPythonPackages', SyncPythonPackagesTask)
    project.tasks.register('installModule', InstallModuleTask)
    project.tasks.register('install', InstallTask)
    project.tasks.register('uninstall', UninstallTask)
    project.tasks.register('check', CheckTask)
    project.tasks.register('pylint', PylintTask)
    project.tasks.register('mypy', MypyTask)
    project.tasks.register('validateEnvironment', ValidateEnvironmentTask)
    project.tasks.register('installPyGradleTools', InstallPyGradleToolsTask)
    project.tasks.register('pyGradleInit', PyGradleInitTask)
    project.tasks.register('pyGradle', PyGradleInitTask)

    project.tasks.register('patchVersion', PatchVersionTask)
    project.tasks.register('updateMinor', UpdateMinorTask)
    project.tasks.register('updateMajor', UpdateMajorTask)
    project.tasks.register('version', VersionTask)
    project.tasks.register('listProperties', ListPropertiesTask)
    project.tasks.register('changelog', ChangelogTask)

    project.tasks.register('cleanDist', CleanDistTask)
    project.tasks.register('copyLicenseAndReadme', CopyLicenseAndReadmeTask)
    project.tasks.register('checkDist', CheckDistTask)
    project.tasks.register('sdist', SdistTask)
    project.tasks.register('publish', PublishTask)
    project.tasks.register('pypiShow', PypiShowTask)

    project.tasks.register('isDockerUp', IsDockerUpTask)
    project.tasks.register('findContainers', FindContainersTask)
    project.tasks.register('listContainers', ListContainersTask)
    project.tasks.register('startAllContainers', StartAllContainersTask)
    project.tasks.register('stopAllContainers', StopAllContainersTask)

    project.tasks.register('autoDocApi', AutoDocApiTask)
    project.tasks.register('optimizeImports', OptimizeImportsTask)
    project.tasks.register('reformatCode', ReformatCodeTask)
    project.tasks.register('syncFileHeaders', SyncFileHeadersTask)

    project.tasks.register('exportRunConfigurations', Copy) { task ->
      task.group = 'Idea'
      task.description = 'Export run configurations'
      task.from("${project.rootDir}/.idea/runConfigurations") {
        include '*.xml'
      }
      task.into "${project.rootDir}/run-configs/idea"
    }
    project.tasks.register('importRunConfigurations', Copy) { task ->
      task.group = 'Idea'
      task.description = 'Import run configurations'
      task.from("${project.rootDir}/run-configs/idea") {
        include '*.xml'
      }
      task.into "${project.rootDir}/.idea/runConfigurations"
    }
    project.tasks.register('clearRunConfigurations', Delete) { task ->
      task.group = 'Idea'
      task.description = 'Delete all run configurations'
      task.delete project.fileTree("${project.rootDir}/.idea/runConfigurations") {
        include '**/*.xml'
      }
      task.followSymlinks = true
    }

    project.tasks.register('syncDevDependencies', SyncDevDependenciesTask)
    project.tasks.register('syncBuildSystemRequirements', SyncBuildSystemRequirementsTask)
    project.tasks.register('syncSetupPy', SyncSetupPyTask)
    project.tasks.register('syncPyProject', SyncPyProjectTask)
    project.tasks.register('poetryInstall', PoetryInstallTask)
    project.tasks.register('poetryBuild', PoetryBuildTask)
    project.tasks.register('poetryPublish', PoetryPublishTask)

    project.tasks.register('clean', org.gradle.api.Task) { task ->
      task.group = 'Build'
      task.dependsOn 'cleanPython'
      task.dependsOn 'cleanDist'
    }
    project.tasks.register('compile', org.gradle.api.Task) { task ->
      task.group = 'Build'
      task.dependsOn 'compilePython'
      task.dependsOn 'compileQrc'
    }
    project.tasks.register('buildOnly', org.gradle.api.Task) { task ->
      task.group = 'Build'
      task.dependsOn 'clean'
      task.dependsOn 'compile'
    }
    project.tasks.register('build', org.gradle.api.Task) { task ->
      task.group = 'Build'
      task.dependsOn 'clean'
      task.dependsOn 'compile'
      task.dependsOn 'check'
    }

    project.defaultTasks = ['build']
  }
}
