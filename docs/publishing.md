# Publishing The Plugin

## Prerequisites
- Gradle 8.5+
- Java 21
- Plugin Portal account
- API key/secret

## Credentials
You can provide credentials via environment variables:

```bash
export GRADLE_PUBLISH_KEY=your_key
export GRADLE_PUBLISH_SECRET=your_secret
```

Or set them in `~/.gradle/gradle.properties`:

```text
gradle.publish.key=your_key
gradle.publish.secret=your_secret
```

## Publish
From the plugin project root:

```bash
./gradlew publishPlugins
```

## Signing (Optional)
If you need signing, apply the Gradle `signing` plugin and configure signing keys
for Maven publications used by the Plugin Portal. See Gradle signing docs for
GPG setup and key material.

## Approval
Initial publication may require manual approval by Gradle engineers.
