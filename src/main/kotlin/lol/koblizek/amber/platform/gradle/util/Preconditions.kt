package lol.koblizek.amber.platform.gradle.util

import lol.koblizek.amber.platform.Environment
import lol.koblizek.amber.platform.gradle.extensions.AmberExtension.Companion.amberExtension
import lol.koblizek.amber.platform.gradle.extensions.AmberExtension.SourcesExtension.Companion.sources
import lol.koblizek.amber.platform.gradle.extensions.MinecraftExtension.Companion.minecraftExtension
import org.gradle.api.Project

class Preconditions(val project: Project) {

    fun isDevelopmentEnabled(): Boolean {
        return project.amberExtension.getEnableDevelopment().isPresent
                && project.amberExtension.getEnableDevelopment().get() == true
    }

    fun areDevPathsSet(): Boolean {
        val sources = project.amberExtension.sources
        return sources.getMinecraft().isPresent
                && sources.getPatches().isPresent
                && sources.getPatchSupport().isPresent
    }

    fun isVersionSet(): Boolean {
        return project.minecraftExtension.getVersionData().isPresent
    }

    fun envNotNull(): Boolean {
        return project.amberExtension.getEnvironment().orNull != null
    }

    companion object {

        private lateinit var instance: Preconditions

        val Project.preconditions: Preconditions
            get() = if (::instance.isInitialized) instance else Preconditions(this)
    }
}