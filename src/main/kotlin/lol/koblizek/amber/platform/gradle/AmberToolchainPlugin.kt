package lol.koblizek.amber.platform.gradle

import lol.koblizek.amber.platform.gradle.extensions.MinecraftExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import java.util.*

class AmberToolchainPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("minecraft", MinecraftExtension::class.java, project)
    }
}

fun <R : Any> Any.safe(action: () -> R): Optional<R> {
    try {
        return Optional.of(action())
    } catch (e: Exception) {
        println("An error occurred!Reason: \n${e.message}")
        println("Stacktrace:")
        e.printStackTrace()
    }
    return Optional.empty()
}