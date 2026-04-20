package org.cobalt.command.annotation

/**
 * Marks a function as a sub-command.
 *
 * @param name optional label that defaults to the function name
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SubCommand(val name: String = "")
