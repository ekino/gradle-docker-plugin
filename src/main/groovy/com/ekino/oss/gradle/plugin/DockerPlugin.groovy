package com.ekino.oss.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class DockerPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {

    // plugins
    project.plugins.apply("docker-compose")

    if (new File("${project.projectDir}/docker-compose.yml").exists()) {

      // configuration
      project.configure(project) {

        dockerCompose {
          forceRecreate = true
          removeOrphans = true
          upAdditionalArgs = ['--renew-anon-volumes']
        }

        project.afterEvaluate {

          // Run integration tests using docker compose
          integrationTest {
            dependsOn(composeUp)
            doFirst {
              dockerCompose.exposeAsEnvironment(integrationTest)
            }
            finalizedBy(composeDown)
          }

          // Local launching with PostgreSQL database
          bootRun {
            dependsOn(composeUp)
            doFirst {
              dockerCompose.exposeAsEnvironment(bootRun)
            }
          }
        }
      }
    }
  }
}
