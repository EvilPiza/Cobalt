package org.cobalt.event

import org.cobalt.event.annotation.SubscribeEvent
import java.lang.invoke.MethodHandles
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

object EventBus {

  private data class Handler(
    val listener: Any,
    val eventType: Class<*>,
    val priority: Event.Priority,
    val ignoreCancelled: Boolean,
    val once: Boolean,
    val invoker: (Event) -> Unit
  )

  private val handlers = CopyOnWriteArrayList<Handler>()
  private val cache = ConcurrentHashMap<Class<*>, Array<Handler>>()

  @JvmStatic
  fun register(listener: Any) {
    if (handlers.any { it.listener === listener }) {
      return
    }

    listener.javaClass.declaredMethods.forEach { method ->
      val annotation = method.getAnnotation(SubscribeEvent::class.java)
        ?: return@forEach

      val params = method.parameterTypes

      if (params.size != 1 || !Event::class.java.isAssignableFrom(params[0])) {
        return@forEach
      }

      if (!method.trySetAccessible()) {
        System.err.println(
          "EventBus: could not access method ${listener.javaClass.name}#${method.name}, skipping"
        )
        return@forEach
      }

      val eventType = params[0]
      val lookup = MethodHandles.privateLookupIn(listener.javaClass, MethodHandles.lookup())
      val handle = lookup.unreflect(method).bindTo(listener)

      val invoker: (Event) -> Unit = { event ->
        handle.invoke(event)
      }

      handlers.add(
        Handler(
          listener = listener,
          eventType = eventType,
          priority = annotation.priority,
          ignoreCancelled = annotation.ignoreCancelled,
          once = annotation.once,
          invoker = invoker
        )
      )
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
    val eventClass = event.javaClass
    val matched = cache.computeIfAbsent(eventClass) {
      handlers
        .filter { it.eventType.isAssignableFrom(eventClass) }
        .sortedBy { it.priority.ordinal }
        .toTypedArray()
    }

    var toRemove: MutableList<Handler>? = null

    for (handler in matched) {
      if (
        event is Event.Cancellable &&
        event.isCancelled() &&
        !handler.ignoreCancelled
      ) {
        continue
      }

      handler.invoker(event)

      if (handler.once) {
        if (toRemove == null) toRemove = mutableListOf()
        toRemove.add(handler)
      }
    }

    if (toRemove != null) {
      handlers.removeAll(toRemove.toSet())
      cache.clear()
    }

    return event
  }

}
