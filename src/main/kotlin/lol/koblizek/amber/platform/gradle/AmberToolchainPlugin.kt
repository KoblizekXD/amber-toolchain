package lol.koblizek.amber.platform.gradle

import lol.koblizek.amber.platform.gradle.extensions.AmberExtension
import lol.koblizek.amber.platform.gradle.extensions.MinecraftExtension
import lol.koblizek.amber.platform.gradle.tasks.GetAllVersionsTask
import lol.koblizek.amber.platform.gradle.tasks.GetCurrentVersionData
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.collections.List

class AmberToolchainPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("minecraft", MinecraftExtension::class.java, project)
        project.extensions.create("amber", AmberExtension::class.java, project)
        project.tasks.create("getAllVersions", GetAllVersionsTask::class.java)
        project.tasks.create("getCurrentVersionData", GetCurrentVersionData::class.java)
    }
}

fun Project.download(url: String, out: String): File {
    val u = URL(url)
    u.openStream().use { Files.copy(it, Paths.get(out)) }
    return File(out)
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

fun <E> List<E>.println() {
    for (e in this) {
        kotlin.io.println(e.toString())
    }
}