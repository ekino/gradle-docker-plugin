# Ekino gradle docker plugin

Docker gradle plugin for Ekino projects

[![Build Status](https://travis-ci.org/ekino/gradle-docker-plugin.svg?branch=master)](https://travis-ci.org/ekino/gradle-docker-plugin)
[![GitHub (pre-)release](https://img.shields.io/github/release/ekino/gradle-docker-plugin.svg)](https://github.com/ekino/gradle-docker-plugin/releases)
[![GitHub license](https://img.shields.io/github/license/ekino/gradle-docker-plugin.svg)](https://github.com/ekino/gradle-docker-plugin/blob/master/LICENSE.md)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ekino_gradle-docker-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=ekino_gradle-docker-plugin)


## Overview

This plugin configures the following tasks for any Ekino projects :

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

## Requirements

- JDK 8
- Gradle 5.6.4

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
