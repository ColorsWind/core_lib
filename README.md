# Core_lib [![Java CI with Gradle](https://github.com/divios/core_lib/actions/workflows/gradle.yml/badge.svg)](https://github.com/divios/core_lib/actions/workflows/gradle.yml)

A collection of libraries and methods to speed up the process of creating a spigot plugin.

# Installation for Development

Core_lib is not a standalone plugin, is a library and is designed to be a dependency to be attached to another plugin. To add it to your dependency, you can either [build it](ttps://github.com/divios/core_lib#build-it-locally) or access the artifact via Github Packages. For now the repository is `private` so only people with permissions can access it.

## With Github Packages

Gradle:
```Groovy
repositories {
     maven {
        url "https://maven.pkg.github.com/divios/core_lib"
        credentials {
            username = "divios"
            password = gitToken
        }
    }
}
```

```groovy
dependencies {
      implementation 'io.github.divios:core_lib_api:Tag'
}
```

Replace `Tag` with a valid package version [here](https://github.com/divios/core_lib/packages).

## Build it Locally

```
git clone https://github.com/divios/core_lib
cd core_lib
./gradlew build
```

After that you can find the jar in `builds/lib/Core_lib.jar`. Take that jar and add it to your proyect classpath.

# Usage

For more info of all the core_lib utilities, see [wiki]().
