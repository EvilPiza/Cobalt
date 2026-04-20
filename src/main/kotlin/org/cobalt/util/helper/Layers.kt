package org.cobalt.util.helper

import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.rendertype.LayeringTransform
import net.minecraft.client.renderer.rendertype.OutputTarget
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType

/**
 * Collection of predefined RenderType layers used for custom rendering.
 */
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

  /**
   * Returns the appropriate quad RenderType based on ESP mode.
   *
   * @param esp whether ESP rendering is enabled
   * @return the corresponding quad RenderType
   */
  @JvmStatic
  fun getQuads(esp: Boolean): RenderType {
    return if (esp) QUADS_ESP else QUADS
  }

  /**
   * Returns the appropriate line RenderType based on ESP mode.
   *
   * @param esp whether ESP rendering is enabled
   * @return the corresponding line RenderType
   */
  @JvmStatic
  fun getLines(esp: Boolean): RenderType {
    return if (esp) LINES_ESP else LINES
  }

}
