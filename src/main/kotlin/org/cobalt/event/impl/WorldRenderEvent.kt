package org.cobalt.event.impl

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.client.Camera
import net.minecraft.client.DeltaTracker
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.culling.Frustum
import org.cobalt.event.Event

class WorldRenderEvent(
  val context: LevelRenderContext
) : Event()
