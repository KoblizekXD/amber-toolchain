package lol.koblizek.amber.platform.gradle.util

import lol.koblizek.amber.platform.gradle.extensions.AmberExtension.Companion.amberExtension
import lol.koblizek.amber.platform.gradle.extensions.AmberExtension.SourcesExtension.Companion.sources
import lol.koblizek.amber.platform.gradle.extensions.MinecraftExtension.Companion.minecraftExtension
import org.gradle.api.Project

class Preconditions(val project: Project) {

    /**
     * Checks if development mode is enabled
     */
    fun isDevelopmentEnabled(): Boolean {
        return project.amberExtension.getEnableDevelopment().isPresent
                && project.amberExtension.getEnableDevelopment().get() == true
    }

    /**
     * Checks if development paths are set
     */
    fun areDevPathsSet(): Boolean {
        val sources = project.amberExtension.sources
        return sources.getMinecraft().isPresent
                && sources.getPatches().isPresent
                && sources.getPatchSupport().isPresent
    }

    /**
     * Checks if the version & mappings are set
     */
    fun isVersionSet(): Boolean {
        return project.minecraftExtension.getVersionData().isPresent
    }

    /**
     * Checks if the environment is set and is not null
     */
    fun envNotNull(): Boolean {
        return project.amberExtension.getEnvironment().orNull != null
    }

    companion object {

        private lateinit var instance: Preconditions

        val Project.preconditions: Preconditions
            get() = if (::instance.isInitialized) instance else Preconditions(this)
    }
}