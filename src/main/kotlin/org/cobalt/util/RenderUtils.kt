package org.cobalt.util

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.cobalt.Cobalt
import org.cobalt.util.helper.CustomRenderTypes
import org.joml.Matrix4f

object RenderUtils {

  private const val ALPHA = 100

  /**
   * Draw a unit cube wireframe and optional translucent fill at the given block position.
   *
   * @param context the level render context to draw with
   * @param pos the block position to draw
   * @param color the color to use for outline/fill
   * @param esp when true uses ESP render type variants
   * @param lineWidth line thickness for outlines
   */
  @JvmStatic
  fun drawBlockPos(
    context: LevelRenderContext,
    pos: BlockPos,
    color: Color,
    esp: Boolean = false,
    lineWidth: Float = 1f,
  ) {
    val box = AABB(
      pos.x.toDouble(),
      pos.y.toDouble(),
      pos.z.toDouble(),
      pos.x + 1.0,
      pos.y + 1.0,
      pos.z + 1.0
    )

    drawBox(context, box, color, esp, lineWidth)
  }

  /**
   * Draw an outline around the provided entity's bounding box, interpolated for rendering.
   *
   * @param context the level render context to draw with
   * @param entity the entity whose bounding box will be outlined
   * @param color the outline color
   * @param esp when true uses ESP render type variants
   * @param lineWidth outline thickness
   */
  fun drawEntityOutline(
    context: LevelRenderContext,
    entity: Entity,
    color: Color,
    esp: Boolean = false,
    lineWidth: Float = 1f,
  ) {
    val partialTicks = Cobalt.minecraft.deltaTracker.getGameTimeDeltaPartialTick(true)

    val interpolatedX = entity.xOld + (entity.x - entity.xOld) * partialTicks
    val interpolatedY = entity.yOld + (entity.y - entity.yOld) * partialTicks
    val interpolatedZ = entity.zOld + (entity.z - entity.zOld) * partialTicks

    val dx = interpolatedX - entity.x
    val dy = interpolatedY - entity.y
    val dz = interpolatedZ - entity.z

    drawBox(context, entity.boundingBox.move(dx, dy, dz), color, esp, lineWidth)
  }

  /** Draw a line from the camera position toward the supplied world-space target point.
   *
   * @param context the level render context to draw with
   * @param to world-space target coordinate for the tracer
   * @param color tracer color
   * @param esp when true uses ESP render type variants
   * @param lineWidth tracer thickness
   */
  @JvmStatic
  fun drawTracer(
    context: LevelRenderContext,
    to: Vec3,
    color: Color,
    esp: Boolean = true,
    lineWidth: Float = 1f,
  ) {
    val camera = Cobalt.minecraft.gameRenderer.mainCamera
    val cameraPos = camera.position()
    val from = cameraPos.add(Vec3.directionFromRotation(camera.xRot(), camera.yRot()))

    drawLine(context, from, to, color, esp, lineWidth)
  }

  /** Draw a colored axis-aligned bounding box (AABB) with optional translucent fill and outline.
   *
   * @param context the level render context to draw with
   * @param box the world-space axis-aligned bounding box
   * @param color color used for fill/outline
   * @param esp when true uses ESP render type variants
   * @param lineWidth outline thickness
   */
  @JvmStatic
  fun drawBox(
    context: LevelRenderContext,
    box: AABB,
    color: Color,
    esp: Boolean = false,
    lineWidth: Float = 1f,
  ) {
    if (color.alpha == 0) return

    val frustum = context.levelState().cameraRenderState.cullFrustum

    if (!FrustumUtils.isVisible(frustum, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)) {
      return
    }

    val cameraPos = Cobalt.minecraft.gameRenderer.mainCamera.position()

    val corners = arrayOf(
      Vec3(box.minX, box.minY, box.minZ), Vec3(box.maxX, box.minY, box.minZ),
      Vec3(box.maxX, box.minY, box.maxZ), Vec3(box.minX, box.minY, box.maxZ),
      Vec3(box.minX, box.maxY, box.minZ), Vec3(box.maxX, box.maxY, box.minZ),
      Vec3(box.maxX, box.maxY, box.maxZ), Vec3(box.minX, box.maxY, box.maxZ),
    )

    drawBoxQuads(context, corners, color, esp, cameraPos)
    drawBoxLines(context, corners, color, esp, lineWidth, cameraPos)
  }

  /** Draw a colored line between two world-space points.
   *
   * @param context the level render context to draw with
   * @param from start point in world coordinates
   * @param to end point in world coordinates
   * @param color the color to use for the line
   * @param esp whether ESP rendering is enabled
   * @param lineWidth thickness of the line
   */
  @JvmStatic
  fun drawLine(
    context: LevelRenderContext,
    from: Vec3,
    to: Vec3,
    color: Color,
    esp: Boolean = false,
    lineWidth: Float = 1f,
  ) {
    if (color.alpha == 0) return

    val frustum = context.levelState().cameraRenderState.cullFrustum

    if (!FrustumUtils.isVisible(
        frustum,
        min(from.x, to.x), min(from.y, to.y), min(from.z, to.z),
        max(from.x, to.x), max(from.y, to.y), max(from.z, to.z)
      )
    ) return

    drawVisibleLine(context, from, to, color, esp, lineWidth)
  }

  private fun drawBoxQuads(
    context: LevelRenderContext,
    corners: Array<Vec3>,
    color: Color,
    esp: Boolean,
    cameraPos: Vec3,
  ) {
    val poseStack = context.poseStack()
    val bufferSource = context.bufferSource()
    val matrix = poseStack.last().pose()

    val fillColor = Color(color.red, color.green, color.blue, ALPHA)

    val quadBuffer = bufferSource.getBuffer(CustomRenderTypes.getQuads(esp))

    for (index in BOX_QUADS) {
      val corner = corners[index]
      quadBuffer.addVertex(
        matrix,
        (corner.x - cameraPos.x).toFloat(),
        (corner.y - cameraPos.y).toFloat(),
        (corner.z - cameraPos.z).toFloat(),
      ).setColor(fillColor.red, fillColor.green, fillColor.blue, fillColor.alpha)
    }

    bufferSource.endBatch(CustomRenderTypes.getQuads(esp))
  }

  private fun drawBoxLines(
    context: LevelRenderContext,
    corners: Array<Vec3>,
    color: Color,
    esp: Boolean,
    lineWidth: Float,
    cameraPos: Vec3,
  ) {
    val poseStack = context.poseStack()
    val bufferSource = context.bufferSource()
    val matrix = poseStack.last().pose()
    val poseEntry = poseStack.last()

    val lineBuffer = bufferSource.getBuffer(CustomRenderTypes.getLines(esp))

    for (i in BOX_LINES.indices step 2) {
      val lineStart = corners[BOX_LINES[i]]
      val lineEnd = corners[BOX_LINES[i + 1]]
      val lineNormal = lineEnd.subtract(lineStart).normalize()

      addBlockLineVertices(lineBuffer, matrix, poseEntry, lineStart, lineEnd, lineNormal, color, lineWidth, cameraPos)
    }

    bufferSource.endBatch(CustomRenderTypes.getLines(esp))
  }

  private fun addBlockLineVertices(
    lineBuffer: VertexConsumer,
    matrix: Matrix4f,
    poseEntry: PoseStack.Pose,
    lineStart: Vec3,
    lineEnd: Vec3,
    lineNormal: Vec3,
    color: Color,
    lineWidth: Float,
    cameraPos: Vec3,
  ) {
    for (vertex in listOf(lineStart, lineEnd)) {
      lineBuffer.addVertex(
        matrix,
        (vertex.x - cameraPos.x).toFloat(),
        (vertex.y - cameraPos.y).toFloat(),
        (vertex.z - cameraPos.z).toFloat(),
      )
        .setLineWidth(lineWidth)
        .setColor(color.red, color.green, color.blue, color.alpha)
        .setNormal(poseEntry, lineNormal.x.toFloat(), lineNormal.y.toFloat(), lineNormal.z.toFloat())
    }
  }

  private fun drawVisibleLine(
    context: LevelRenderContext,
    from: Vec3,
    to: Vec3,
    color: Color,
    esp: Boolean,
    lineWidth: Float,
  ) {
    val bufferSource = context.bufferSource()
    val lineBuffer = bufferSource.getBuffer(CustomRenderTypes.getLines(esp))

    addLineVertices(context, lineBuffer, from, to, color, lineWidth)

    bufferSource.endBatch(CustomRenderTypes.getLines(esp))
  }

  private fun addLineVertices(
    context: LevelRenderContext,
    lineBuffer: VertexConsumer,
    from: Vec3,
    to: Vec3,
    color: Color,
    lineWidth: Float,
  ) {
    val poseStack = context.poseStack()
    val cameraPos = Cobalt.minecraft.gameRenderer.mainCamera.position()
    val poseEntry = poseStack.last()
    val lineNormal = to.subtract(from).normalize()

    for (vertex in listOf(from, to)) {
      lineBuffer.addVertex(
        poseEntry.pose(),
        (vertex.x - cameraPos.x).toFloat(),
        (vertex.y - cameraPos.y).toFloat(),
        (vertex.z - cameraPos.z).toFloat()
      )
        .setLineWidth(lineWidth)
        .setColor(color.red, color.green, color.blue, color.alpha)
        .setNormal(poseEntry, lineNormal.x.toFloat(), lineNormal.y.toFloat(), lineNormal.z.toFloat())
    }
  }

  private val BOX_QUADS = intArrayOf(
    0, 1, 2, 3,
    4, 7, 6, 5,
    0, 4, 5, 1,
    1, 5, 6, 2,
    2, 6, 7, 3,
    3, 7, 4, 0,
  )

  private val BOX_LINES = intArrayOf(
    0, 1, 1, 2, 2, 3, 3, 0,
    4, 5, 5, 6, 6, 7, 7, 4,
    0, 4, 1, 5, 2, 6, 3, 7,
  )

}
