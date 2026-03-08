package org.cobalt.event.impl

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Camera
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum
import org.cobalt.event.Event

abstract class WorldEvent : Event() {
  class RenderStart(val context: RenderContext) : WorldEvent()
  class RenderLast(val context: RenderContext) : WorldEvent()
}

class RenderContext {
  var matrixStack: PoseStack? = null
  lateinit var consumers: MultiBufferSource
  lateinit var camera: Camera
  lateinit var frustum: Frustum
}
