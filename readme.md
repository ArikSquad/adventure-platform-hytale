# adventure-platform-hytale

[![MIT License](https://img.shields.io/badge/license-MIT-blue)](license.txt)

Adventure platform implementation for [Hytale](https://hytale.com/).

You may be able to refer to [documentation](https://docs.papermc.io/adventure/platform/) for usage and dependency information for this project and the main `adventure` library.

### Usage
To use `adventure-platform-hytale`, include it as a dependency in your project. You need to add Jitpack as a repository to your build system. For example, in Gradle:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.ArikSquad:adventure-platform-hytale:1.0.2'
}
```

### Contributing

We appreciate contributions of any type. For any new features or typo-fix/style changes, please open an issue or come talk to us in our [Discord] first so we make sure you're going in the right direction for the project.

All the adventure projects are built with Gradle, require at least JDK 8, and use a common checkstyle configuration. Please make sure all tests pass, license headers are updated, and checkstyle passes to help us review your contribution.

`adventure-platform-hytale` is released under the terms of the [MIT License](license.txt).
