package org.cobalt.dsl

import java.awt.Color
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.util.RenderUtils

/**
 * Draw a unit cube wireframe and optional translucent fill at the given block position.
 *
 * @param pos the block position to draw
 * @param color the color to use for outline/fill
 * @param esp when true uses ESP render type variants
 * @param lineWidth line thickness for outlines
 */
fun LevelRenderContext.drawBlockPos(pos: BlockPos, color: Color, esp: Boolean = false, lineWidth: Float = 1f) =
  RenderUtils.drawBlockPos(this, pos, color, esp, lineWidth)

/**
 * Draw an outline around the provided entity's bounding box, interpolated for rendering.
 *
 * @param entity the entity whose bounding box will be outlined
 * @param color the outline color
 * @param esp when true uses ESP render type variants
 * @param lineWidth outline thickness
 */
fun LevelRenderContext.drawEntityOutline(entity: Entity, color: Color, esp: Boolean = false, lineWidth: Float = 1f) =
  RenderUtils.drawEntityOutline(this, entity, color, esp, lineWidth)

/**
 * Draw a line from the camera position toward the supplied world-space target point.
 *
 * @param to world-space target coordinate for the tracer
 * @param color tracer color
 * @param esp when true uses ESP render type variants
 * @param lineWidth tracer thickness
 */
fun LevelRenderContext.drawTracer(to: Vec3, color: Color, esp: Boolean = false, lineWidth: Float = 1f) =
  RenderUtils.drawTracer(this, to, color, esp, lineWidth)

/**
 * Draw a colored axis-aligned bounding box (AABB) with optional translucent fill and outline.
 *
 * @param box the world-space axis-aligned bounding box
 * @param color color used for fill/outline
 * @param esp when true uses ESP render type variants
 * @param lineWidth outline thickness
 */
fun LevelRenderContext.drawBox(box: AABB, color: Color, esp: Boolean = false, lineWidth: Float = 1f) =
  RenderUtils.drawBox(this, box, color, esp, lineWidth)

/**
 * Draw a colored line between two world-space points.
 *
 * @param from start point in world coordinates
 * @param to end point in world coordinates
 * @param color the color to use for the line
 * @param esp whether ESP rendering is enabled
 * @param lineWidth thickness of the line
 */
fun LevelRenderContext.drawLine(from: Vec3, to: Vec3, color: Color, esp: Boolean = false, lineWidth: Float = 1f) =
  RenderUtils.drawLine(this, from, to, color, esp, lineWidth)
