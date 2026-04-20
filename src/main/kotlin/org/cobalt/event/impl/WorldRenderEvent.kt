package org.cobalt.event.impl

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import org.cobalt.event.Event

/**
 * Custom event fired at the end of world rendering.
 *
 * Intended for use with [org.cobalt.util.RenderUtils] to perform custom world rendering.
 *
 * @property context the Fabric [LevelRenderContext] for the current render frame
 */
class WorldRenderEvent(
  val context: LevelRenderContext,
) : Event()
