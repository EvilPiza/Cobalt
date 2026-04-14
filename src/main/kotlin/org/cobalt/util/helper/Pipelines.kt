package org.cobalt.util.helper

import com.mojang.blaze3d.pipeline.DepthStencilState
import com.mojang.blaze3d.pipeline.RenderPipeline
import java.util.*
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier

/** Central registry for custom render pipelines used by the client. */
object Pipelines {

  private const val NAMESPACE = "cobalt"
  private const val PIPELINE_DIR = "pipeline/"
  private const val LINES_ESP_PATH = PIPELINE_DIR + "lines_esp"
  private const val QUADS_PATH = PIPELINE_DIR + "quads"
  private const val QUADS_ESP_PATH = PIPELINE_DIR + "quads_esp"

  private val NO_DEPTH_STENCIL: Optional<DepthStencilState> = Optional.empty()

  /** Pipeline for rendering ESP-style lines (no depth/stencil). */
  val LINES_ESP: RenderPipeline = RenderPipelines.register(
    RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
      .withLocation(Identifier.fromNamespaceAndPath(NAMESPACE, LINES_ESP_PATH))
      .withDepthStencilState(NO_DEPTH_STENCIL)
      .build()
  )

  /** Pipeline for rendering filled debug quads (default depth/stencil). */
  val QUADS: RenderPipeline = RenderPipelines.register(
    RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
      .withLocation(Identifier.fromNamespaceAndPath(NAMESPACE, QUADS_PATH))
      .withDepthStencilState(DepthStencilState.DEFAULT)
      .build()
  )

  /** Pipeline for rendering filled quads used in ESP (no depth/stencil). */
  val QUADS_ESP: RenderPipeline = RenderPipelines.register(
    RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
      .withLocation(Identifier.fromNamespaceAndPath(NAMESPACE, QUADS_ESP_PATH))
      .withDepthStencilState(NO_DEPTH_STENCIL)
      .build()
  )

}
