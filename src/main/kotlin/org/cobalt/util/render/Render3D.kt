package org.cobalt.util.render

import java.awt.Color
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.event.impl.RenderContext

interface Render3D {

  fun drawBlockPos(context: RenderContext, pos: BlockPos, color: Color, esp: Boolean = false)
  fun drawBox(context: RenderContext, box: AABB, color: Color, esp: Boolean = false)

  fun drawTracer(context: RenderContext, to: Vec3, color: Color, esp: Boolean = true, thickness: Float = 1f)
  fun drawLine(context: RenderContext, start: Vec3, end: Vec3, color: Color, esp: Boolean = false, thickness: Float = 1f)

}
