package org.cobalt.util.helper

import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.rendertype.LayeringTransform
import net.minecraft.client.renderer.rendertype.OutputTarget
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType

/** Pre-configured RenderType providers used by RenderUtils for lines and quads (ESP variants). */
object Layers {

  private val LINES: RenderType = RenderType.create(
    "cobalt:lines",
    RenderSetup
      .builder(RenderPipelines.LINES)
      .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
      .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
      .createRenderSetup()
  )

  private val LINES_ESP: RenderType = RenderType.create(
    "cobalt:lines_esp",
    RenderSetup
      .builder(Pipelines.LINES_ESP)
      .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
      .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
      .createRenderSetup()
  )

  private val QUADS: RenderType = RenderType.create(
    "cobalt:quads",
    RenderSetup
      .builder(Pipelines.QUADS)
      .createRenderSetup()
  )

  private val QUADS_ESP: RenderType = RenderType.create(
    "cobalt:quads_esp",
    RenderSetup
      .builder(Pipelines.QUADS_ESP)
      .createRenderSetup()
  )

  /** Return a RenderType for drawing quads; pass true to use the ESP variant. */
  fun getQuads(esp: Boolean): RenderType {
    return if (esp) QUADS_ESP else QUADS
  }

  /** Return a RenderType for drawing lines; pass true to use the ESP variant. */
  fun getLines(esp: Boolean): RenderType {
    return if (esp) LINES_ESP else LINES
  }

}
