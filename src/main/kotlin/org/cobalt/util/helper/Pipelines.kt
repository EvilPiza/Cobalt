package org.cobalt.util.helper

import com.mojang.blaze3d.pipeline.DepthStencilState
import com.mojang.blaze3d.pipeline.RenderPipeline
import java.util.*
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier

object Pipelines {

  val LINES_ESP: RenderPipeline = RenderPipelines.register(
    RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
      .withLocation(Identifier.fromNamespaceAndPath("cobalt", "pipeline/lines_esp"))
      .withDepthStencilState(Optional.empty())
      .build()
  )

  val QUADS: RenderPipeline = RenderPipelines.register(
    RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
      .withLocation(Identifier.fromNamespaceAndPath("cobalt", "pipeline/quads"))
      .withDepthStencilState(DepthStencilState.DEFAULT)
      .build()
  )

  val QUADS_ESP: RenderPipeline = RenderPipelines.register(
    RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
      .withLocation(Identifier.fromNamespaceAndPath("cobalt", "pipeline/quads_esp"))
      .withDepthStencilState(Optional.empty())
      .build()
  )

}
