package lol.koblizek.amber.platform.gradle.tasks

import lol.koblizek.amber.platform.gradle.download
import lol.koblizek.amber.platform.gradle.util.Described
import org.gradle.api.DefaultTask
import org.gradle.workers.WorkerExecutor
import java.io.File
import javax.inject.Inject
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation


abstract class AmberTask : DefaultTask() {
    init {
        group = "amber-toolchain"
        if (this::class.hasAnnotation<Described>())
            description = this::class.findAnnotation<Described>()?.value
    }

    @Inject
    abstract fun getWorkerExecutor(): WorkerExecutor


    fun download(file: String): File {
        val fileName = file.substring( file.lastIndexOf('/')+1, file.length )
        return project.download(file, this.temporaryDir.resolve(fileName).path)
    }
}