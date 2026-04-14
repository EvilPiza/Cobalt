package org.cobalt.command.annotation

/**
 * Marks a function as a named sub-command for a command handler.
 *
 * @param name optional sub-command label; when empty the function name is
 *             used by the dispatcher
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SubCommand(val name: String = "")
