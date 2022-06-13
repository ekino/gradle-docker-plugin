
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import se.bjurr.gitchangelog.plugin.gradle.GitChangelogTask

plugins {
  `java-gradle-plugin`
  jacoco
  id("net.researchgate.release") version "2.8.1"
  id("se.bjurr.gitchangelog.git-changelog-gradle-plugin") version "1.72.0"
  kotlin("jvm") version "1.5.31"
  id("org.sonarqube") version "3.4.0.2513"
  id("com.gradle.plugin-publish") version "0.21.0"
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation("com.avast.gradle:gradle-docker-compose-plugin:${property("dockerComposePluginVersion")}")

  testImplementation(gradleTestKit())
  testImplementation("org.junit.jupiter:junit-jupiter:${property("junitJupiterVersion")}")
}

tasks {
  withType<KotlinCompile> {
    kotlinOptions {
      freeCompilerArgs = listOf("-Xjsr305=strict")
      jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
  }

  withType<Test> {
    useJUnitPlatform()

    // Tests summary (displayed at the end)
    addTestListener(object : TestListener {
      override fun beforeSuite(suite: TestDescriptor) {}
      override fun beforeTest(testDescriptor: TestDescriptor) {}
      override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}

      override fun afterSuite(desc: TestDescriptor, result: TestResult) {
        if (desc.parent == null) {
          println("Tests result: ${result.resultType}")
          println("Tests summary: ${result.testCount} tests, "+
                  "${result.successfulTestCount} succeeded, " +
                  "${result.failedTestCount} failed, " +
                  "${result.skippedTestCount} skipped")
        }
      }
    })
  }

  register("printVersion") {
    doLast {
      println(project.version)
    }
  }

  register("gitChangelogTask", GitChangelogTask::class) {
    file("CHANGELOG.md")
    templateContent = file("changelog.mustache").readText(Charsets.UTF_8)
  }
}

val pluginName = "gradleDocker"
gradlePlugin {
  plugins {
    register(pluginName) {
      id = "com.ekino.oss.gradle.plugin.docker"
      implementationClass = "com.ekino.oss.gradle.plugin.docker.DockerPlugin"
    }
  }
}

pluginBundle {
  website = "https://github.com/ekino/gradle-docker-plugin"
  vcsUrl = "https://github.com/ekino/gradle-docker-plugin"
  description = "Docker plugin applying some configuration for your builds"

  (plugins) {
    named(pluginName) {
      displayName = "Gradle docker plugin"
      tags = listOf("ekino", "docker")
      version = version
    }
  }
}

tasks.jacocoTestReport {
  reports {
    xml.isEnabled = true
  }
}

sonarqube {
  properties {
    property("sonar.projectKey", "ekino_gradle-docker-plugin")
    property("sonar.java.coveragePlugin", "jacoco")
    property("sonar.junit.reportPaths", "${buildDir}/test-results/test")
    property("sonar.coverage.jacoco.xmlReportPaths", "${buildDir}/reports/jacoco/test/jacocoTestReport.xml")
  }
}
