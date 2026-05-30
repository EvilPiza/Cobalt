package org.cobalt.util.helper

import com.mojang.blaze3d.pipeline.DepthStencilState
import com.mojang.blaze3d.pipeline.RenderPipeline
import java.util.Optional
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier

object CustomPipelines {

  private val NO_DEPTH_STENCIL: Optional<DepthStencilState> = Optional.empty()

  @JvmStatic
  val LINES_ESP: RenderPipeline = RenderPipelines.register(
    RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
      .withLocation(Identifier.fromNamespaceAndPath("cobalt", "pipeline/lines_esp"))
      .withDepthStencilState(NO_DEPTH_STENCIL)
      .build()
  )

  @JvmStatic
  val QUADS: RenderPipeline = RenderPipelines.register(
    RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
      .withLocation(Identifier.fromNamespaceAndPath("cobalt", "pipeline/quads"))
      .withDepthStencilState(DepthStencilState.DEFAULT)
      .build()
  )

  @JvmStatic
  val QUADS_ESP: RenderPipeline = RenderPipelines.register(
    RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
      .withLocation(Identifier.fromNamespaceAndPath("cobalt", "pipeline/quads_esp"))
      .withDepthStencilState(NO_DEPTH_STENCIL)
      .build()
  )

}
