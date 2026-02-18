# Usage Guide

## Overview
- This repo provides Gradle tasks and scripts to build, lint, test, and package Python projects.
- The Gradle wrapper (`./gradlew`) is the primary entry point for tasks.

## Project Layout
- `build.gradle`: plugin build configuration and publishing metadata.
- `dependencies.hspd`: declarative dependency list used to generate `src/main/requirements.txt`.
- `src/main/groovy/`: plugin implementation and task classes.
- `src/main/python/`: sample Python app entrypoint and resources.
- `src/test/`: plugin unit and functional tests.

## Common Tasks
Use `./gradlew <task>` from the repo root.

Default task: `build`.

## Plugin Usage
Apply the plugin in a consuming project:

```gradle
plugins {
  id 'br.com.hhs.pygradle'
}
```

See `docs/migration.md` for migrating from `apply from` scripts.

### Configuration
The plugin exposes a `pyGradle` extension with defaults. Common overrides:

```gradle
pyGradle {
  sourceRoot = "${projectDir}/src"
  pythonPath = "${projectDir}/src/main:${projectDir}/src/test"
  pypiModuleUrl = "https://pypi.org/pypi"
}
```

### Validation
Run `validateEnvironment` to check required tools and config values are available.
If missing tools are detected, it will print installation hints and suggest running:
`./gradlew installPyGradleTools -PexecuteInstall=true`.

### Tool Installation
`installPyGradleTools` prints the commands to install required tools. To execute them:

```bash
./gradlew installPyGradleTools -PexecuteInstall=true
```

### Project Scaffold
Create or update a project scaffold:

```bash
./gradlew pyGradleInit
```

Use `-PforceInit=true` to overwrite existing files.

### Dry-Run Mode
For destructive tasks (installs, docker start/stop, publish), add `-PdryRun=true`
to print the commands without executing them.

### Dependencies
- `listDependencies`: print dependencies parsed from `dependencies.hspd`.
- `syncRequirements`: generate `src/main/requirements.txt` from `dependencies.hspd`.
- `installPackages`: install Python dependencies from generated requirements.
- `removePackages`: uninstall project dependencies.
- `freezeRequirements`: write a pinned requirements snapshot.
- `installBinaries`: install required system apps listed in `dependencies.hspd` (brew/apt/yum; uses `sudo` for apt/yum).

### Build and Package
- `cleanPython`: remove compiled Python artifacts and cache dirs.
- `compilePython`: run `py_compile` on project sources.
- `compileQrc`: compile Qt `.qrc` resources (if present).
- `buildOnly`: clean + compile.
- `build`: clean + compile + tests.
- `install`: install the project into the current Python environment.
- `installModule`: editable install (pip -e).
- `uninstall`: remove the project from the environment.

### Verification
- `check`: run Python unittests under `src/test`.
- `pylint`: run pylint against `src/main/<project.name>`.
- `mypy`: run mypy against `src/main/<project.name>`.

### Versioning and Release
- `patchVersion`, `updateMinor`, `updateMajor`: bump version components.
- `version`: show version info.
- `listProperties`: list versioning-related properties.
- `changelog`: show version changelog entries.
- `cleanDist`, `sdist`, `publish`, `pypiShow`: packaging and PyPI publish flow.

### Docs and Formatting
- `autoDocApi`: generate API docs.
- `optimizeImports`: sort/optimize imports.
- `reformatCode`: apply code formatting.
- `syncFileHeaders`: apply standardized file headers.

### Docker
- `isDockerUp`: check Docker status.
- `listContainers`, `startAllContainers`, `stopAllContainers`: manage containers.

### IDE Run Configs
- `exportRunConfigurations`, `importRunConfigurations`, `clearRunConfigurations`: manage IDE run configs in `run-configs/`.

### Oracle
- `downloadOraClient`, `installOraClient` (OS/arch-aware; set `oracleClientUrl` and `oracleClientAppFile` when needed)

## Dependency File Format
`dependencies.hspd` supports entries like:

```text
package: <pkg_name>, version: <latest|versionNum>, mode: <lt|le|eq|compat|ne|gt|ge|none>
binary: <app_name>, version: <latest|versionNum>
```

## Notes
- Python is detected automatically via `PYTHON_HOME` or `python3` on `PATH`.
- Package install space is selected based on venv detection (`--global` if venv, `--user` otherwise).
- Publishing details are in `docs/publishing.md`.
