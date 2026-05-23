package org.cobalt.event

import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import org.cobalt.event.annotation.SubscribeEvent
import org.slf4j.LoggerFactory

object EventBus {

  private data class Handler(
    val listener: Any,
    val eventType: Class<*>,
    val priority: Event.Priority,
    val ignoreCancelled: Boolean,
    val once: Boolean,
    val invoker: (Event) -> Unit,
  )

  private val handlers = CopyOnWriteArrayList<Handler>()
  private val cache = ConcurrentHashMap<Class<*>, Array<Handler>>()
  private val logger = LoggerFactory.getLogger(this::class.java)

  @JvmStatic
  fun register(listener: Any) {
    if (handlers.any { it.listener === listener }) {
      return
    }

    val toAdd = createHandlersForListener(listener)

    if (toAdd.isNotEmpty()) handlers.addAll(toAdd)

    cache.clear()
  }

  @JvmStatic
  fun unregister(listener: Any) {
    handlers.removeIf { it.listener === listener }
    cache.clear()
  }

  @JvmStatic
  fun post(event: Event): Event {
    val eventClass = event.javaClass
    val matched = cache.computeIfAbsent(eventClass) { computeMatchedHandlers(eventClass) }

    val toRemove = processMatchedHandlers(matched, event)

    if (toRemove.isNotEmpty()) {
      handlers.removeAll(toRemove.toSet())
      cache.clear()
    }

    return event
  }

  private fun createHandlersForListener(listener: Any): List<Handler> {
    val result = mutableListOf<Handler>()
    listener.javaClass.declaredMethods.forEach { method ->
      createHandlerFromMethod(listener, method)?.let { result.add(it) }
    }
    return result
  }

  private fun createHandlerFromMethod(listener: Any, method: Method): Handler? {
    val annotation = method.getAnnotation(SubscribeEvent::class.java)
    val params = method.parameterTypes

    if (annotation == null || params.size != 1 || !Event::class.java.isAssignableFrom(params[0])) {
      return null
    }

    if (!method.trySetAccessible()) {
      logger.error("EventBus: could not access method ${listener.javaClass.name}#${method.name}, skipping")
      return null
    }

    val eventType = params.first()
    return buildHandler(listener, method, annotation, eventType)
  }

  private fun buildHandler(listener: Any, method: Method, annotation: SubscribeEvent, eventType: Class<*>): Handler {
    val lookup = MethodHandles.privateLookupIn(listener.javaClass, MethodHandles.lookup())
    val handle = lookup.unreflect(method).bindTo(listener)

    val invoker: (Event) -> Unit = { event -> handle.invoke(event) }

    return Handler(
      listener = listener,
      eventType = eventType,
      priority = annotation.priority,
      ignoreCancelled = annotation.ignoreCancelled,
      once = annotation.once,
      invoker = invoker
    )
  }

  private fun processMatchedHandlers(matched: Array<Handler>, event: Event): MutableList<Handler> {
    val toRemove = mutableListOf<Handler>()

    for (handler in matched) {
      if (shouldSkipHandler(handler, event)) continue
      invokeHandler(handler, event, toRemove)
    }

    return toRemove
  }

  private fun shouldSkipHandler(handler: Handler, event: Event): Boolean {
    return (event is Event.Cancellable && event.isCancelled() && !handler.ignoreCancelled)
  }

  private fun invokeHandler(handler: Handler, event: Event, toRemove: MutableList<Handler>) {
    handler.invoker(event)
    if (handler.once) toRemove.add(handler)
  }

  private fun computeMatchedHandlers(eventClass: Class<*>): Array<Handler> {
    return handlers
      .filter { it.eventType.isAssignableFrom(eventClass) }
      .sortedBy { it.priority.ordinal }
      .toTypedArray()
  }

}
