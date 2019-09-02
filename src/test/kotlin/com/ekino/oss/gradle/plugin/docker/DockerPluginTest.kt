package com.ekino.oss.gradle.plugin.docker

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DockerPluginTest {

  @Test
  fun `should contain docker-compose plugin`() {
    val project: Project = ProjectBuilder.builder().build()
    assertEquals(0, project.plugins.size)

    project.pluginManager.apply("com.ekino.oss.gradle.plugin.docker")
    assertTrue(project.plugins.hasPlugin("docker-compose"))
  }
}
