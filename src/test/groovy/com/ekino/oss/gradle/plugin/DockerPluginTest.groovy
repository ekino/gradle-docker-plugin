package com.ekino.oss.gradle.plugin

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class DockerPluginTest {

  @Test
  void shouldContainsDefaultPlugins() {
    Project project = ProjectBuilder.builder().build()

    assertEquals(0, project.getExtensions().getPlugins().size())
    project.apply plugin: 'com.ekino.frida.docker'

    assertTrue(project.pluginManager.hasPlugin('docker-compose'))
  }
}
