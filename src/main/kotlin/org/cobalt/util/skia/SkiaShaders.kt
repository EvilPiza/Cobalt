package org.cobalt.util.skia

import io.github.humbleui.skija.Data
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.RuntimeEffect
import io.github.humbleui.types.Rect
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaContext.canvas

object SkiaShaders {

  private const val UNIFORM_BUFFER_SIZE = 20
  private const val NANOSECONDS_PER_SECOND = 1_000_000_000f

  @JvmStatic
  fun renderShader(
    pos: Vec2f,
    dim: Dimensions,
    shaderCode: String,
  ) {
    val canvas = canvas ?: return

    if (!isValid(dim)) {
      return
    }

    RuntimeEffect.makeForShader(shaderCode).use { effect ->
      val shader = effect.makeShader(buildUniformData(dim), emptyArray())

      Paint().apply {
        this.shader = shader
        isAntiAlias = true
      }.use { paint ->
        canvas.drawRect(Rect.makeXYWH(pos.x, pos.y, dim.width, dim.height), paint)
      }
    }
  }

  private fun buildUniformData(dim: Dimensions): Data {
    val buffer = ByteBuffer.allocateDirect(UNIFORM_BUFFER_SIZE)
      .order(ByteOrder.nativeOrder())

    buffer.putFloat(dim.width)
    buffer.putFloat(dim.height)
    buffer.putFloat(System.nanoTime() / NANOSECONDS_PER_SECOND)
    buffer.flip()

    return Data.makeFromBytes(ByteArray(buffer.remaining()).also { buffer.get(it) })
  }

  private fun isValid(dim: Dimensions) = dim.width > 0f && dim.height > 0f

}
