package org.cobalt.event.impl

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Camera
import net.minecraft.client.DeltaTracker
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.state.level.CameraRenderState
import org.cobalt.event.Event

abstract class WorldEvent : Event() {
  class RenderStart(val context: RenderContext) : WorldEvent()
  class RenderLast(val context: RenderContext) : WorldEvent()
}

class RenderContext {
  var poseStack: PoseStack? = null
  lateinit var deltaTracker: DeltaTracker
  lateinit var bufferSource: MultiBufferSource
  lateinit var frustum: Frustum
}
