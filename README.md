# Core_lib [![Java CI with Gradle](https://github.com/divios/core_lib/actions/workflows/gradle.yml/badge.svg)](https://github.com/divios/core_lib/actions/workflows/gradle.yml)

A collection of libraries and methods to speed up the process of creating a spigot plugin.

# Installation for Development

Core_lib is not a standalone plugin, is a library and is designed to be a dependency to be attached to another plugin. To add it to your dependency, you can either [build it]() or access the artifact via Github Packages. For now the repository is `private` so only people with permissions can access it.

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
      implementation 'io.github.divios:core_lib_api:5.0'
}
```
