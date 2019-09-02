package com.ekino.oss.gradle.plugin.docker

import com.avast.gradle.dockercompose.ComposeSettings
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.testing.Test
import java.io.File

class DockerPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    // plugins
    project.pluginManager.apply("docker-compose")

    if (File("${project.projectDir}/docker-compose.yml").exists()) {
      val dockerCompose = ComposeSettings(project)
      dockerCompose.forceRecreate = true
      dockerCompose.removeOrphans = true
      dockerCompose.upAdditionalArgs = listOf("--renew-anon-volumes")

      with(project) {
        afterEvaluate {
          // Run integration tests using docker compose
          with(task("integrationTest")) {
            dependsOn("composeUp")

            doFirst {
              dockerCompose.exposeAsEnvironment(it as Test)
            }

            finalizedBy("composeDown")
          }

          // Local launching with PostgreSQL database
          with(task("bootRun")) {
            dependsOn("composeUp")

            doFirst {
              dockerCompose.exposeAsEnvironment(it as JavaExec)
            }
          }
        }
      }
    }
  }
}
