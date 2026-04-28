package org.cobalt.dsl

import java.awt.Color
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.util.RenderUtils

fun LevelRenderContext.drawBlockPos(pos: BlockPos, color: Color, esp: Boolean = false, lineWidth: Float = 1f) =
  RenderUtils.drawBlockPos(this, pos, color, esp, lineWidth)

fun LevelRenderContext.drawEntityOutline(entity: Entity, color: Color, esp: Boolean = false, lineWidth: Float = 1f) =
  RenderUtils.drawEntityOutline(this, entity, color, esp, lineWidth)

fun LevelRenderContext.drawTracer(to: Vec3, color: Color, esp: Boolean = false, lineWidth: Float = 1f) =
  RenderUtils.drawTracer(this, to, color, esp, lineWidth)

fun LevelRenderContext.drawBox(box: AABB, color: Color, esp: Boolean = false, lineWidth: Float = 1f) =
  RenderUtils.drawBox(this, box, color, esp, lineWidth)

fun LevelRenderContext.drawLine(from: Vec3, to: Vec3, color: Color, esp: Boolean = false, lineWidth: Float = 1f) =
  RenderUtils.drawLine(this, from, to, color, esp, lineWidth)
