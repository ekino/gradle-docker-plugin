# Ekino gradle docker plugin

Docker gradle plugin for Ekino projects

## Overview

This plugin configures the following tasks for any Frida Java project :

* Manage docker-compose

## Build

This will create the JAR and run the tests

    ./gradlew build

## Publish locally

This will publish the JAR in your local Maven repository

    ./gradlew publishToMavenLocal

## Publish

This will upload the plugin to Nexus repository

    ./gradlew build publish


## Usage

To use this plugin add the maven repository on settings.gradle (must be the first block of the file)

```groovy
pluginManagement {
  repositories {
    mavenCentral()
  }
}    
```

Or for SNAPSHOT versions :

```groovy
pluginManagement {
  repositories {
    maven {
      url 'https://oss.sonatype.org/content/repositories/snapshots/'
    }
  }
}
```

then add the plugin on build.gradle

```groovy
plugins {
    id "com.ekino.oss.gradle.plugin.docker" version "0.0.1"
}
```

## Docker compose

If you have a `docker-compose.yml` at the root of your project, this plugin will start your docker compose before :
* The bootRun task
* The integrationTest

And it will stop the docker compose after.

It can be very useful to start servers in order to perform good integration tests
or start a microservice with all its requirements offline.
