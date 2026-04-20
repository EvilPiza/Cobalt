package org.cobalt.command.annotation

/**
 * Marks a function as the default command handler.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class DefaultHandler
