package org.cobalt.util.rotation

import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.WorldRenderEvent

/**
 * Global manager for the currently active rotation controller.
 *
 * The manager holds an [IRotation] implementation which performs rotation
 * logic. Use [setActiveRotation] to switch controllers, [getActiveRotation]
 * to query the current controller and [resetRotation] to return to the
 * default implementation. This object registers itself on the event bus and
 * forwards per-frame world render updates to the active rotation when
 * applicable.
 */
object RotationManager {

  private var rotation: IRotation = DefaultRotations

  init {
    EventBus.register(this)
  }

  /**
   * Make [newRotation] the active rotation controller and start a rotation
   * towards the provided [yaw] and [pitch]. If a rotation is already in
   * progress it will be stopped before switching controllers.
   */
  fun setActiveRotation(newRotation: IRotation, yaw: Double, pitch: Double) {
    if (rotation.isRotating()) {
      rotation.stopRotation()
    }

    rotation = newRotation
    rotation.onRotationStart(yaw, pitch)
  }

  /**
   * Returns the currently active [IRotation] controller.
   */
  fun getActiveRotation(): IRotation = rotation

  /**
   * Reset the active rotation controller to the default implementation and
   * stop any ongoing rotation.
   */
  fun resetRotation() {
    if (rotation.isRotating()) {
      rotation.stopRotation()
    }

    rotation = DefaultRotations
  }

  /**
   * Event handler invoked during world rendering. Forwards the render event
   * to the active rotation controller when a rotation is in progress.
   */
  @SubscribeEvent
  fun onWorldRender(event: WorldRenderEvent) {
    if (!rotation.isRotating()) return
    rotation.onRotationWorldRender()
  }

}
