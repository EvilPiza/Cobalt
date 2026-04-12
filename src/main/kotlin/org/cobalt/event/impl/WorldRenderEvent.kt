package org.cobalt.event.impl

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.client.Camera
import net.minecraft.client.DeltaTracker
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum
import org.cobalt.event.Event

/** Event emitted during the world render pass providing the rendering context. */
class WorldRenderEvent(
  /** The LevelRenderContext for the current render pass. */
  val context: LevelRenderContext
) : Event()
