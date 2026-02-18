# TODO - Option 2: Standalone Gradle Plugin Project (Prioritized)

## P0 - Required to Build and Publish
- [x] Define plugin ID: `br.com.hhs.pygradle`.
- [x] Set Gradle/Java compatibility targets: Gradle 8.5, Java 21.
- [x] Create a standalone Gradle plugin project (new module or separate repo).
- [x] Configure `java-gradle-plugin` + `groovy` and set group/version coordinates.
- [x] Add Plugin Portal metadata and plugin marker publication.
- [x] Implement a typed extension that mirrors current `ext` properties.
- [x] Port core tasks with 1:1 names/behavior:
  - [x] Dependencies tasks (`dependencies.gradle`).
  - [x] Python tasks + wrappers (`python.gradle`).
  - [x] Versioning tasks (`versioning.gradle`).
  - [x] PyPI publish tasks (`pypi-publish.gradle`).
- [x] Update this repo to consume the plugin instead of `apply from` scripts.
- [x] Smoke test key tasks (`clean`, `build`, `check`, `publish` as applicable).

## P1 - Developer Experience and Correctness
- [x] Port remaining tasks:
  - [x] Docker (`docker.gradle`).
  - [x] Oracle client (`oracle.gradle`).
  - [x] Docgen (`docgen.gradle`).
  - [x] IDE run configs (`idea.gradle`).
  - [x] Poetry dev tooling (`devel/poetry.gradle`).
- [x] Centralize Python/OS/tool detection in shared utilities.
- [x] Add task inputs/outputs for caching and up-to-date checks.
- [x] Add validation for missing tools with clear error messages.
- [x] Add unit tests for dependency parser.
- [x] Add Gradle TestKit functional tests for core tasks (happy/unhappy paths).

## P2 - Documentation and Release Hardening
- [x] Write plugin README with usage examples and task catalog.
- [x] Add migration guide from `apply from` to plugin ID usage.
- [x] Document extension properties and defaults.
- [x] Add publishing/signing instructions (if required by Plugin Portal).
- [x] Add CI workflow or release checklist for publishing.

## Confirmed Decisions
- Language: Groovy
- Publish destination: Gradle Plugin Portal
- Backend: Java
- Task names/behavior: 1:1 compatibility
