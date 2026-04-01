package org.cobalt.util.rotation

import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.WorldRenderEvent

internal object RotationHandler {
  @SubscribeEvent
  fun onWorldRender(event: WorldRenderEvent) {
    if (!RotationManager.getActiveRotation().isRotating()) return
    RotationManager.getActiveRotation().onRotationWorldRender()
  }
}
