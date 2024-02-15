package lol.koblizek.amber.platform.gradle.tasks

import lol.koblizek.amber.platform.gradle.download
import lol.koblizek.amber.platform.gradle.util.Described
import org.gradle.api.DefaultTask
import java.io.File
import java.net.URL
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

abstract class AmberTask : DefaultTask() {
    init {
        group = "amber-toolchain"
        if (this::class.hasAnnotation<Described>())
            description = this::class.findAnnotation<Described>()?.value
    }

    fun download(file: String): File {
        val fileName = file.substring( file.lastIndexOf('/')+1, file.length )
        return project.download(file, this.temporaryDir.resolve(fileName).path)
    }
}