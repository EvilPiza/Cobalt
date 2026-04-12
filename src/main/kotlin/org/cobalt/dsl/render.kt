package org.cobalt.dsl

import java.awt.Color
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.util.RenderUtils

/** Draw a wireframe outline of a block at the given world block position. */
fun LevelRenderContext.drawBlockPos(pos: BlockPos, color: Color, esp: Boolean = false, lineWidth: Float = 1f) =
  RenderUtils.drawBlockPos(this, pos, color, esp, lineWidth)

/** Draw an outline around an entity using the provided color and line width. */
fun LevelRenderContext.drawEntityOutline(entity: Entity, color: Color, esp: Boolean = false, lineWidth: Float = 1f) =
  RenderUtils.drawEntityOutline(this, entity, color, esp, lineWidth)

/** Draw a tracer (line) from the camera/renderer position to the given world vector. */
fun LevelRenderContext.drawTracer(to: Vec3, color: Color, esp: Boolean = false, lineWidth: Float = 1f) =
  RenderUtils.drawTracer(this, to, color, esp, lineWidth)

/** Draw a colored axis-aligned bounding box (AABB) in world space. */
fun LevelRenderContext.drawBox(box: AABB, color: Color, esp: Boolean = false, lineWidth: Float = 1f) =
  RenderUtils.drawBox(this, box, color, esp, lineWidth)

/** Draw a colored line between two world-space points using the renderer context. */
fun LevelRenderContext.drawLine(from: Vec3, to: Vec3, color: Color, esp: Boolean = false, lineWidth: Float = 1f) =
  RenderUtils.drawLine(this, from, to, color, esp, lineWidth)
