# py-gradle
Gradle plugin for Python applications.

Default task: `build`.

## Overview
py-gradle provides a Gradle plugin that automates common Python project tasks such as
requirements syncing, builds, linting, publishing, docker management, and docs generation.

## Requirements
- Gradle 8.5+
- Java 21
- Python 3.x on PATH (or `PYTHON_HOME` set)

Optional tools (required by specific tasks):
- docker
- poetry
- bumpver
- twine
- pdoc
- isort
- black
- mypy
- pylint

## Apply The Plugin
```gradle
plugins {
  id 'br.com.hhs.pygradle'
}
```

## Sample settings.gradle
```gradle
rootProject.name = 'pygradle-sample'
```

## Sample build.gradle
```gradle
plugins {
  id 'br.com.hhs.pygradle'
}

pyGradle {
  sourceRoot = "${projectDir}/src"
  pythonPath = "${projectDir}/src/main:${projectDir}/src/test"
  pypiModuleUrl = "https://pypi.org/pypi"
  dryRun = false
}
```

## Configuration
The plugin exposes a `pyGradle` extension with defaults. Common overrides:

```gradle
pyGradle {
  sourceRoot = "${projectDir}/src"
  pythonPath = "${projectDir}/src/main:${projectDir}/src/test"
  pypiModuleUrl = "https://pypi.org/pypi"
  dryRun = false
  oracleClientUrl = null
  oracleClientAppFile = null
}
```

You can also set properties in `pygradle.properties` or `pygradle.yaml` at the project root.
These override defaults and can be used to set `pythonExec`, `pythonHome`, and `pythonVersion`.

Example `pygradle.properties`:

```text
app_name=rpg-game
app_version=0.0.1
pythonVersion=3.11.14
pythonExec=.venv/bin/python
```

Example `pygradle.yaml`:

```yaml
application:
  name: rpg-game
  version: 0.0.1

python:
  version: 3.11.14
  executable: .venv/bin/python

pip:
  extraIndexUrl: https://your.private.index/simple
```

Gradle properties that are used as defaults when present:
- `app_name`, `app_version`, `author`, `siteUrl`
- `pythonPath`, `failScore`, `versionFile`, `buildTools`
- `oracleClientUrl`, `oracleClientAppFile`, `oracleClientLibDir`

## Validation And Tooling
Validate toolchain availability:

```bash
./gradlew validateEnvironment
```

Install tooling helpers (prints commands, run with `-PexecuteInstall=true` to execute):

```bash
./gradlew installPyGradleTools
./gradlew installPyGradleTools -PexecuteInstall=true
```

Note: Docker and Poetry are not installed by this helper. Install them manually if you use Docker or Poetry tasks.

## Project Scaffold
Create a starter project or update an existing one:

```bash
./gradlew pyGradleInit
```

Overwrite existing files:

```bash
./gradlew pyGradleInit -PforceInit=true
```

Select a Python version (default: 3.11.14):

```bash
./gradlew pyGradleInit -PpythonVersion=3.11.14
```

Skip virtual environment creation:

```bash
./gradlew pyGradleInit -PskipVenv=true
```

## Dry-Run Mode
For destructive tasks (installs, docker start/stop, publish), enable dry-run:

```bash
./gradlew publish -PdryRun=true
```

## Task Catalog
Below is a high-level task catalog grouped by purpose. Use `./gradlew tasks --all` for the full list.

### Build
- `cleanPython`: remove compiled Python artifacts and caches
- `compilePython`: run `py_compile` on sources
- `compileQrc`: compile Qt `.qrc` resources
- `syncBuildTools`: generate build tools file
- `installBuildTools`: install build tools
- `syncPythonPackages`: update `__init__.py` packages
- `cleanDist`: remove distribution artifacts
- `buildOnly`, `build`, `clean`, `compile`

### Application
- `run`: run the configured Python entrypoint (uses venv when configured)

### Dependencies
- `listDependencies`: list parsed dependencies
- `syncRequirements`: generate `requirements.txt`
- `installPackages`: install Python requirements
- `removePackages`: uninstall requirements
- `freezeRequirements`: show installed versions
- `installBinaries`: install required system apps (brew/apt/yum; uses `sudo` for apt/yum)

`dependencies.hspd` supports entries like:

```text
package: <pkg_name>, version: <latest|versionNum>, mode: <lt|le|eq|compat|ne|gt|ge|none>
binary: <app_name>, version: <latest|versionNum>
```

You can also include raw `requirements.txt` lines (they are copied as-is), for example:

```text
--index-url https://pypi.org/simple
-r extra-requirements.txt
git+https://github.com/user/repo.git@main#egg=demo
requests[socks]>=2.31.0; python_version < "3.12"
```

### Verification
- `check`: run Python unittests
- `pylint`: run pylint
- `mypy`: run mypy
- `validateEnvironment`: validate external tools and config

### Versioning
- `patchVersion`, `updateMinor`, `updateMajor`
- `version`, `listProperties`, `changelog`

### Publish
- `sdist`: build source/wheel distributions
- `checkDist`: run twine checks
- `publish`: tag and push git revisions
- `pypiShow`: show PyPI module info
- `copyLicenseAndReadme`

### Docker
- `isDockerUp`
- `findContainers`, `listContainers`
- `startAllContainers`, `stopAllContainers`

### Documentation
- `autoDocApi`: generate docs with pdoc
- `optimizeImports`: isort
- `reformatCode`: black
- `syncFileHeaders`: update file headers

### IDE
- `exportRunConfigurations`, `importRunConfigurations`, `clearRunConfigurations`

### Poetry
- `syncDevDependencies`, `syncBuildSystemRequirements`, `syncSetupPy`, `syncPyProject`
- `poetryInstall`, `poetryBuild`, `poetryPublish`

### Oracle
- `downloadOraClient`, `installOraClient`

## Examples
Generate requirements and install dependencies:

```bash
./gradlew syncRequirements installPackages
```

Build and run tests:

```bash
./gradlew build
```

Generate documentation:

```bash
./gradlew autoDocApi
```

Publish (dry-run):

```bash
./gradlew publish -PdryRun=true
```

Initialize a new project scaffold:

```bash
./gradlew pyGradleInit
```

## Publishing The Plugin
Set Plugin Portal credentials and publish:

```bash
export GRADLE_PUBLISH_KEY=your_key
export GRADLE_PUBLISH_SECRET=your_secret
./gradlew publishPlugins
```

## Notes
- `defaultTasks` is set to `build`.
- Some tasks require network or external tools. Run `validateEnvironment` first.
