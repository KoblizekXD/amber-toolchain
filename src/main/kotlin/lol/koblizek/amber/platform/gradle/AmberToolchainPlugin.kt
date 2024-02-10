package lol.koblizek.amber.platform.gradle

import lol.koblizek.amber.platform.gradle.extensions.MinecraftExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.Optional

class AmberToolchainPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        minecraftExtension = project.extensions.create("minecraft", MinecraftExtension::class.java)
        // Later, we will also add the `amber` extension, to support custom developer environments
    }

    companion object {
        lateinit var minecraftExtension: MinecraftExtension
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