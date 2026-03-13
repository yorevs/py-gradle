package io.github.hs_teams.pygradle

import io.github.hs_teams.pygradle.internal.PyGradleUtils
import io.github.hs_teams.pygradle.tasks.AutoDocApiTask
import io.github.hs_teams.pygradle.tasks.ChangelogTask
import io.github.hs_teams.pygradle.tasks.CheckDistTask
import io.github.hs_teams.pygradle.tasks.CheckTask
import io.github.hs_teams.pygradle.tasks.CleanDistTask
import io.github.hs_teams.pygradle.tasks.CleanPythonTask
import io.github.hs_teams.pygradle.tasks.CompilePythonTask
import io.github.hs_teams.pygradle.tasks.CompileQrcTask
import io.github.hs_teams.pygradle.tasks.CopyLicenseAndReadmeTask
import io.github.hs_teams.pygradle.tasks.DownloadOraClientTask
import io.github.hs_teams.pygradle.tasks.FindContainersTask
import io.github.hs_teams.pygradle.tasks.FreezeRequirementsTask
import io.github.hs_teams.pygradle.tasks.InstallBinariesTask
import io.github.hs_teams.pygradle.tasks.InstallBuildToolsTask
import io.github.hs_teams.pygradle.tasks.InstallModuleTask
import io.github.hs_teams.pygradle.tasks.InstallPackagesTask
import io.github.hs_teams.pygradle.tasks.InstallTask
import io.github.hs_teams.pygradle.tasks.InstallPyGradleToolsTask
import io.github.hs_teams.pygradle.tasks.InstallOraClientTask
import io.github.hs_teams.pygradle.tasks.IsDockerUpTask
import io.github.hs_teams.pygradle.tasks.ListContainersTask
import io.github.hs_teams.pygradle.tasks.ListDependenciesTask
import io.github.hs_teams.pygradle.tasks.ListPropertiesTask
import io.github.hs_teams.pygradle.tasks.MypyTask
import io.github.hs_teams.pygradle.tasks.OptimizeImportsTask
import io.github.hs_teams.pygradle.tasks.PatchVersionTask
import io.github.hs_teams.pygradle.tasks.PoetryBuildTask
import io.github.hs_teams.pygradle.tasks.PoetryInstallTask
import io.github.hs_teams.pygradle.tasks.PoetryPublishTask
import io.github.hs_teams.pygradle.tasks.PylintTask
import io.github.hs_teams.pygradle.tasks.PypiShowTask
import io.github.hs_teams.pygradle.tasks.PublishTask
import io.github.hs_teams.pygradle.tasks.ReformatCodeTask
import io.github.hs_teams.pygradle.tasks.RemovePackagesTask
import io.github.hs_teams.pygradle.tasks.RunTask
import io.github.hs_teams.pygradle.tasks.SdistTask
import io.github.hs_teams.pygradle.tasks.StartAllContainersTask
import io.github.hs_teams.pygradle.tasks.SyncBuildSystemRequirementsTask
import io.github.hs_teams.pygradle.tasks.SyncBuildToolsTask
import io.github.hs_teams.pygradle.tasks.SyncDevDependenciesTask
import io.github.hs_teams.pygradle.tasks.SyncFileHeadersTask
import io.github.hs_teams.pygradle.tasks.SyncPythonPackagesTask
import io.github.hs_teams.pygradle.tasks.SyncPyProjectTask
import io.github.hs_teams.pygradle.tasks.SyncRequirementsTask
import io.github.hs_teams.pygradle.tasks.SyncSetupPyTask
import io.github.hs_teams.pygradle.tasks.StopAllContainersTask
import io.github.hs_teams.pygradle.tasks.UninstallTask
import io.github.hs_teams.pygradle.tasks.UpdateMajorTask
import io.github.hs_teams.pygradle.tasks.UpdateMinorTask
import io.github.hs_teams.pygradle.tasks.ValidateEnvironmentTask
import io.github.hs_teams.pygradle.tasks.VersionTask
import io.github.hs_teams.pygradle.tasks.PyGradleInitTask
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
    project.tasks.register('run', RunTask)
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
