package lol.koblizek.amber.platform.gradle.util

/**
 * An annotation used to add description to a task
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Described(val value: String)
