package org.cobalt.util.render

import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderType.CompositeRenderType

internal object RenderLayers {

  val LINE_LIST: RenderType = RenderType.create(
    "line_list", RenderType.TRANSIENT_BUFFER_SIZE,
    RenderPipelines.LINE_LIST,
    RenderType.CompositeState.builder()
      .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
      .createCompositeState(false)
  )

  val LINE_LIST_ESP: RenderType = RenderType.create(
    "line_list_esp", RenderType.TRANSIENT_BUFFER_SIZE,
    RenderPipelines.LINE_LIST_ESP,
    RenderType.CompositeState.builder().createCompositeState(false)
  )

  val TRIANGLE_STRIP: CompositeRenderType = RenderType.create(
    "triangle_strip", RenderType.TRANSIENT_BUFFER_SIZE,
    false, true,
    RenderPipelines.TRIANGLE_STRIP,
    RenderType.CompositeState.builder()
      .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
      .createCompositeState(false)
  )

  val TRIANGLE_STRIP_ESP: CompositeRenderType = RenderType.create(
    "triangle_strip_esp", RenderType.TRANSIENT_BUFFER_SIZE,
    false, true,
    RenderPipelines.TRIANGLE_STRIP_ESP,
    RenderType.CompositeState.builder().createCompositeState(false)
  )

}
