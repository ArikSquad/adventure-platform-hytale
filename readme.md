# adventure-platform-hytale

[![MIT License](https://img.shields.io/badge/license-MIT-blue)](license.txt)

Adventure platform implementation for [Hytale](https://hytale.com/).

You may be able to refer to Adventure's platform [documentation](https://docs.papermc.io/adventure/platform/) (not affiliated) for usage and dependency information for this project and the main `adventure` library.

### Usage
To use `adventure-platform-hytale`, include it as a dependency in your project. You need to add Jitpack as a repository to your build system. For example, in Gradle:

```gradle
repositories {
    maven { url 'https://repo.codemc.io/repository/ArikSquad/' }
}

dependencies {
    implementation 'eu.mikart.adventure:adventure-platform-hytale:1.0.3'
}
```

You should first obtain a `HytaleAudiences` object by using `HytaleAudiences.create(plugin)`. This object is thread-safe and can be reused from different threads if needed. From here, CommandSenders and Players may be converted into Audiences using the appropriate methods on HytaleAudiences.

```java
public class MyPlugin extends JavaPlugin {

    private HytaleAudiences adventure;

    @NonNull
    public HytaleAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    public void start() {
        // Initialize an audiences instance for the plugin
        this.adventure = HytaleAudiences.create(this);
        // then do any other initialization
    }

    @Override
    public void shutdown() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }
}
```

This audience provider should be used over the serializers directly, since it handles player-specific context.

You are able to use the `HytaleComponentSerializer` to convert between Adventure [Components](https://docs.papermc.io/adventure/text) and Hytale's Message objects. For uses that can’t integrate directly within the Adventure Components by default, you can make use of `HytaleComponentSerializer`.

### Contributing

We appreciate contributions of any type, and you're welcome to open a pull request. `adventure-platform-hytale` is built with Gradle, requires at least JDK 25, and use a common checkstyle configuration. Please make sure all tests pass, license headers are updated, and checkstyle passes to help us review your contribution.

`adventure-platform-hytale` is released under the terms of the [MIT License](license.txt).
