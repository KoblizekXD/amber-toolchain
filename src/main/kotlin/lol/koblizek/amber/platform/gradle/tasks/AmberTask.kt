package lol.koblizek.amber.platform.gradle.tasks

import lol.koblizek.amber.platform.gradle.util.Described
import org.gradle.api.DefaultTask
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

abstract class AmberTask : DefaultTask() {
    init {
        group = "amber-toolchain"
        if (this::class.hasAnnotation<Described>())
            description = this::class.findAnnotation<Described>()?.value
    }
}