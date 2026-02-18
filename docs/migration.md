# Migration Guide

This guide migrates a project that uses `apply from: ...` Gradle scripts to the plugin ID.

## Before
Your project likely has something like this in `build.gradle`:

```gradle
apply from: "${sourceRoot}/main/gradle/dependencies.gradle"
apply from: "${sourceRoot}/main/gradle/python.gradle"
apply from: "${sourceRoot}/main/gradle/versioning.gradle"
apply from: "${sourceRoot}/main/gradle/pypi-publish.gradle"
apply from: "${sourceRoot}/main/gradle/docker.gradle"
apply from: "${sourceRoot}/main/gradle/oracle.gradle"
apply from: "${sourceRoot}/main/gradle/docgen.gradle"
apply from: "${sourceRoot}/main/gradle/idea.gradle"
apply from: "${sourceRoot}/main/gradle/devel/poetry.gradle"
```

## After
Replace those `apply from` lines with the plugin ID:

```gradle
plugins {
  id 'br.com.hhs.pygradle'
}
```

## Configuration Mapping
Most previous `ext` properties are now part of the `pyGradle` extension and/or standard Gradle properties.
You can keep properties in `gradle.properties` as before. Common overrides:

```gradle
pyGradle {
  sourceRoot = "${projectDir}/src"
  pythonPath = "${projectDir}/src/main:${projectDir}/src/test"
  pypiModuleUrl = "https://pypi.org/pypi"
  dryRun = false
}
```

## Validate
Run a quick check after migration:

```bash
./gradlew validateEnvironment
./gradlew tasks --all
```

## Notes
- Default task is now `build`.
- Use `-PdryRun=true` for safe previews of destructive tasks.
