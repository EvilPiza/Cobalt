package org.cobalt.util.rotation

import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.WorldRenderEvent

object RotationManager {

  private var rotation: IRotation = DefaultRotations

  init {
    EventBus.register(this)
  }

  fun setActiveRotation(newRotation: IRotation, yaw: Double, pitch: Double) {
    if (rotation.isRotating()) {
      rotation.stopRotation()
    }

    rotation = newRotation
    rotation.onRotationStart(yaw, pitch)
  }

  fun getActiveRotation(): IRotation = rotation

  fun resetRotation() {
    if (rotation.isRotating()) {
      rotation.stopRotation()
    }

    rotation = DefaultRotations
  }

  @SubscribeEvent
  fun onWorldRender(event: WorldRenderEvent) {
    if (!rotation.isRotating()) return
    rotation.onRotationWorldRender()
  }

}
