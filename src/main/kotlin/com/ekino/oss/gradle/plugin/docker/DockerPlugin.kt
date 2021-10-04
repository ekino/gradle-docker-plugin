package com.ekino.oss.gradle.plugin.docker

import com.avast.gradle.dockercompose.ComposeExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.testing.Test
import java.io.File

class DockerPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    with(project) {
      if (!File("${projectDir}/docker-compose.yml").exists()) {
        return
      }

      // plugins
      pluginManager.apply("docker-compose")

      val dockerCompose = extensions.getByType(ComposeExtension::class.java)
      dockerCompose.forceRecreate.set(true)
      dockerCompose.removeOrphans.set(true)
      dockerCompose.upAdditionalArgs.set(listOf("--renew-anon-volumes"))

      afterEvaluate {
        // Run integration tests using docker compose
        applyConfigForTask("integrationTest") {
          dependsOn("composeUp")
          doFirst {
            dockerCompose.exposeAsEnvironment(it as Test)
          }
          finalizedBy("composeDown")
        }

        // Local launching with docker compose
        applyConfigForTask("bootRun") {
          dependsOn("composeUp")
          doFirst {
            dockerCompose.exposeAsEnvironment(it as JavaExec)
          }
        }
      }
    }
  }

  private inline fun Project.applyConfigForTask(taskName: String, config: Task.() -> Unit) {
    getTasksByName(taskName, false).firstOrNull()?.config()
  }
}
