package com.ekino.oss.gradle.plugin.docker

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

class GradleVersionsCompatibilityTest {

    @TempDir
    lateinit var tempDir: File

    @ValueSource(strings = ["5.6.4", "6.2.2"])
    @ParameterizedTest(name = "{0}")
    @DisplayName("Should work with Gradle version")
    fun shouldWorkInGradleVersion(gradleVersion: String) {
        val buildScript =
            """
            plugins {
                id 'com.ekino.oss.gradle.plugin.docker'
            }
            """

        File("$tempDir/build.gradle").writeText(buildScript)

        val result = GradleRunner.create()
            .withProjectDir(tempDir)
            .withGradleVersion(gradleVersion)
            .withPluginClasspath()
            .withArguments("build", "--stacktrace")
            .forwardOutput()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":buildEnvironment")?.outcome)
    }
}
