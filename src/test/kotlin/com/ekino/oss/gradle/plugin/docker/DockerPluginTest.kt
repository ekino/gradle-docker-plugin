package com.ekino.oss.gradle.plugin.docker

import com.avast.gradle.dockercompose.ComposeExtension
import org.gradle.api.internal.tasks.DefaultTaskDependency
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class DockerPluginTest {

  @TempDir
  lateinit var tempDir: Path

  @Test
  fun `should contain docker-compose plugin and proper defined tasks`() {
    // Given
    Files.write(tempDir.resolve("docker-compose.yml"), "".toByteArray())

    val project = ProjectBuilder.builder()
        .withProjectDir(tempDir.toFile())
        .build()
    project.task("integrationTest")
    project.task("bootRun")

    // When
    project.pluginManager.apply(DockerPlugin::class.java)

    // Then
    assertTrue(project.plugins.hasPlugin("docker-compose"))

    val dockerComposeExtension = project.extensions.getByType(ComposeExtension::class.java)
    assertTrue(dockerComposeExtension.forceRecreate)
    assertTrue(dockerComposeExtension.removeOrphans)
    assertTrue(dockerComposeExtension.upAdditionalArgs.contains("--renew-anon-volumes"))

    // Force the project to be evaluated
    val bootRunTask = project.getTasksByName("bootRun", false).first()
    assertTrue(bootRunTask.dependsOn.contains("composeUp"))

    val integrationTestTask = project.getTasksByName("integrationTest", false).first()
    assertTrue(integrationTestTask.dependsOn.contains("composeUp"))
    assertTrue((integrationTestTask.finalizedBy as DefaultTaskDependency).mutableValues.contains("composeDown"))
  }

  @Test
  fun `should contain docker-compose plugin without specific tasks`() {
    // Given
    Files.write(tempDir.resolve("docker-compose.yml"), "".toByteArray())

    val project = ProjectBuilder.builder()
        .withProjectDir(tempDir.toFile())
        .build()

    // When
    project.pluginManager.apply(DockerPlugin::class.java)

    // Then
    assertTrue(project.plugins.hasPlugin("docker-compose"))

    val dockerComposeExtension = project.extensions.getByType(ComposeExtension::class.java)
    assertTrue(dockerComposeExtension.forceRecreate)
    assertTrue(dockerComposeExtension.removeOrphans)
    assertTrue(dockerComposeExtension.upAdditionalArgs.contains("--renew-anon-volumes"))

    val tasksNames = project.getAllTasks(false).getValue(project).map { it.name }
    assertFalse(tasksNames.contains("bootRun"))
    assertFalse(tasksNames.contains("integrationTest"))
  }

  @Test
  fun `should not contain docker-compose plugin without docker-compose file`() {
    // Given
    val project = ProjectBuilder.builder()
        .withProjectDir(tempDir.toFile())
        .build()

    // When
    project.pluginManager.apply(DockerPlugin::class.java)

    // Then
    assertFalse(project.plugins.hasPlugin("docker-compose"))
  }
}
