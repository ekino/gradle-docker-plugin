
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import se.bjurr.gitchangelog.plugin.gradle.GitChangelogTask

plugins {
  id("java-gradle-plugin")
  id("maven-publish")
  id("signing")
  id("jacoco")
  id("net.researchgate.release") version "2.8.1"
  id("se.bjurr.gitchangelog.git-changelog-gradle-plugin") version "1.64"
  kotlin("jvm") version "1.3.50"
  id("org.sonarqube") version "2.8"
}

gradlePlugin {
  plugins {
    register("gradleDocker") {
      id = "com.ekino.oss.gradle.plugin.docker"
      implementationClass = "com.ekino.oss.gradle.plugin.docker.DockerPlugin"
    }
  }
}

repositories {
  mavenLocal()
  jcenter()
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

  val sourcesJar by creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
  }

  val javadocJar by creating(Jar::class) {
    dependsOn.add(javadoc)
    archiveClassifier.set("javadoc")
    from(javadoc.get().destinationDir)
  }

  artifacts {
    archives(sourcesJar)
    archives(javadocJar)
  }
}

publishing {
  publications {
    register("mavenJava", MavenPublication::class) {
      from(components["java"])

      artifact(tasks.getByName("sourcesJar"))
      artifact(tasks.getByName("javadocJar"))

      pom {
        name.set("Gradle docker plugin")
        description.set("Docker plugin applying some configuration for your builds")
        url.set("https://github.com/ekino/gradle-docker-plugin")

        licenses {
          license {
            name.set("MIT License (MIT)")
            url.set("https://opensource.org/licenses/mit-license")
          }
        }

        developers {
          developer {
            organization.set("ekino")
            organizationUrl.set("https://www.ekino.com/")
          }
        }

        scm {
          connection.set("scm:git:git://github.com/ekino/gradle-docker-plugin.git")
          developerConnection.set("scm:git:ssh://github.com:ekino/gradle-docker-plugin.git")
          url.set("https://github.com/ekino/gradle-docker-plugin")
        }
      }
    }

    //needed by sonatype oss check in staging
    register("mavenPluginMarker", MavenPublication::class) {
      groupId = gradlePlugin.plugins["gradleDocker"].id
      artifactId = gradlePlugin.plugins["gradleDocker"].id + ".gradle.plugin"

      pom {
        name.set("Gradle docker plugin")
        description.set("Docker plugin applying some configuration for your builds")
        url.set("https://github.com/ekino/gradle-docker-plugin")

        licenses {
          license {
            name.set("MIT License (MIT)")
            url.set("https://opensource.org/licenses/mit-license")
          }
        }

        developers {
          developer {
            organization.set("ekino")
            organizationUrl.set("https://www.ekino.com/")
          }
        }

        scm {
          connection.set("scm:git:git://github.com/ekino/gradle-docker-plugin.git")
          developerConnection.set("scm:git:ssh://github.com:ekino/gradle-docker-plugin.git")
          url.set("https://github.com/ekino/gradle-docker-plugin")
        }
      }

      pom.withXml {
        val dependency = asNode().appendNode("dependencies").appendNode("dependency")
        dependency.appendNode("groupId", project.group)
        dependency.appendNode("artifactId", project.name)
        dependency.appendNode("version", project.version)
      }
    }
  }

  repositories {
    maven {
      val ossrhUrl: String? by project
      val ossrhUsername: String? by project
      val ossrhPassword: String? by project

      url = uri(ossrhUrl ?: "")
      credentials {
        username = ossrhUsername
        password = ossrhPassword
      }
    }
  }
}

signing {
  setRequired({
    project.hasProperty("signing.keyId") && project.hasProperty("signing.password")
  })
  sign(publishing.publications["mavenJava"])
  sign(publishing.publications["mavenPluginMarker"])
}

tasks.jacocoTestReport {
  reports {
    xml.setEnabled(true)
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