package org.cobalt.event

import java.lang.invoke.MethodHandles
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import org.cobalt.event.annotation.SubscribeEvent
import org.slf4j.LoggerFactory

object EventBus {

  private val handlers = CopyOnWriteArrayList<Handler>()
  private val cache = ConcurrentHashMap<Class<*>, Array<Handler>>()
  private val logger = LoggerFactory.getLogger(this::class.java)

  @JvmStatic
  fun register(listener: Any) {
    if (handlers.any { it.listener === listener }) {
      return
    }

    val toAdd = listener.javaClass.declaredMethods.mapNotNull { method ->
      val annotation = method.getAnnotation(SubscribeEvent::class.java)
      val params = method.parameterTypes

      if (annotation == null || params.size != 1 || !Event::class.java.isAssignableFrom(params[0])) {
        return@mapNotNull null
      }

      if (!method.trySetAccessible()) {
        logger.error("EventBus: could not access method ${listener.javaClass.name}#${method.name}, skipping")
        return@mapNotNull null
      }

      val eventType = params.first()
      val handle = MethodHandles
        .privateLookupIn(listener.javaClass, MethodHandles.lookup())
        .unreflect(method)
        .bindTo(listener)

      Handler(
        listener = listener,
        eventType = eventType,
        priority = annotation.priority,
        ignoreCancelled = annotation.ignoreCancelled,
        once = annotation.once,
        invoker = { event -> handle.invoke(event) },
      )
    }

    if (toAdd.isNotEmpty()) {
      handlers.addAll(toAdd)
    }

    cache.clear()
  }

  @JvmStatic
  fun unregister(listener: Any) {
    handlers.removeIf { it.listener === listener }
    cache.clear()
  }

  @JvmStatic
  fun post(event: Event): Event {
    val matched = cache.computeIfAbsent(event.javaClass) { cls ->
      handlers
        .filter { it.eventType.isAssignableFrom(cls) }
        .sortedBy { it.priority.ordinal }
        .toTypedArray()
    }

    val toRemove = mutableListOf<Handler>()

    for (handler in matched) {
      if (event is Event.Cancellable && event.isCancelled() && !handler.ignoreCancelled) {
        continue
      }

      handler.invoker(event)

      if (handler.once) {
        toRemove.add(handler)
      }
    }

    if (toRemove.isNotEmpty()) {
      handlers.removeAll(toRemove.toSet())
      cache.clear()
    }

    return event
  }

  private data class Handler(
    val listener: Any,
    val eventType: Class<*>,
    val priority: Event.Priority,
    val ignoreCancelled: Boolean,
    val once: Boolean,
    val invoker: (Event) -> Unit,
  )

}
