package lol.koblizek.amber.platform.gradle

import lol.koblizek.amber.platform.gradle.extensions.AmberExtension
import lol.koblizek.amber.platform.gradle.extensions.MinecraftExtension
import lol.koblizek.amber.platform.gradle.tasks.CollectRequiredData
import lol.koblizek.amber.platform.gradle.tasks.GetAllVersionsTask
import lol.koblizek.amber.platform.gradle.tasks.GetCurrentVersionData
import lol.koblizek.amber.platform.gradle.tasks.RemapJar
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.util.*

class AmberToolchainPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val minecraftLibrary = project.configurations.create("minecraftLibrary") {
            it.isCanBeResolved = true
        }

        project.configurations.getByName("implementation") {
            it.extendsFrom(minecraftLibrary)
        }

        project.extensions.create("minecraft", MinecraftExtension::class.java, project)
        project.extensions.create("amber", AmberExtension::class.java, project)
        project.tasks.create("getAllVersions", GetAllVersionsTask::class.java)
        project.tasks.create("getCurrentVersionData", GetCurrentVersionData::class.java)
        project.tasks.create("collectRequiredData", CollectRequiredData::class.java)
        project.tasks.create("remapJar", RemapJar::class.java)
    }
}

fun Project.download(url: String, out: String): File {
    val u = URL(url)
    val fOut = File(out)
    if (fOut.exists()) {
        fOut.delete()
    }
    u.openStream().use { Files.copy(it, fOut.toPath()) }
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
        println(e.toString())
    }
}

fun <E> List<E>.println(prefix: String) {
    for (e in this) {
        kotlin.io.println(prefix + e.toString())
    }
}