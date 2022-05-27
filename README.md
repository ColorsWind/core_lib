# Core_lib

A collection of libraries and methods to speed up the process of creating a spigot plugin.

# Installation for Development

Core_lib is not a standalone plugin, is a library and is designed to be a dependency to be attached to another plugin. To add it to your project, you can either take it from github packages via maven or [build it](ttps://github.com/divios/core_lib#build-it-locally) yourself.

## With Github Packages

You can use the artifacts hosted on Github Packages to access the api.

```groovy
repositories {
    maven { url = "https://maven.pkg.github.com/divios/core_lib" }
}
```

``` groovy
dependencies {
        compileOnly 'io.github.divios:core_lib:Tag'
}
```

Replace `Tag` with a valid Core_lib version. Example `6.0.1`. Check the latest tag on [packages](https://github.com/divios?tab=packages&repo_name=core_lib)

## Build it Locally

```
git clone https://github.com/divios/core_lib
cd core_lib
./gradlew build
```

Note: you need to run [buildtools](https://www.spigotmc.org/wiki/buildtools/) since the api uses mojand's Authlib library internally. If you are not familiar with this i strongly suggest to get the jar via [releases](https://github.com/divios/core_lib/releases) and add to your project path.

After that you can find the jar in `builds/lib/Core_lib.jar`. Take that jar and add it to your proyect classpath or instead publish it to your maven local.

