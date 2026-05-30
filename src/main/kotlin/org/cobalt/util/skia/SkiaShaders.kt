package org.cobalt.util.skia

import io.github.humbleui.skija.Data
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.RuntimeEffect
import io.github.humbleui.types.Rect
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaContext.canvas
import org.cobalt.util.skia.SkiaContext.surface

object SkiaShaders {

  private val startTime: Long = System.currentTimeMillis()

  fun loadShader(resourcePath: String): RuntimeEffect {
    try {
      javaClass.getResourceAsStream(resourcePath).use { inputStream ->
        checkNotNull(inputStream) { "Missing shader resource" }

        val sksl = String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
        val shaderEffect = RuntimeEffect.makeForShader(sksl)

        return shaderEffect
      }
    } catch (exception: IOException) {
      throw IllegalStateException("Failed to read shader resource", exception)
    }
  }

  fun renderShader(pos: Vec2f, dim: Dimensions, runtimeEffect: RuntimeEffect) {
    val canvas = canvas ?: return
    val surface = surface ?: return

    val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000f
    val uniformData = createUniformData(elapsedSeconds, dim)

    Data.makeFromBytes(uniformData).use { data ->
      surface.makeImageSnapshot().use { image ->
        image.makeShader().use { imageShader ->
          renderRuntimeShader(canvas, pos, dim, runtimeEffect, data, imageShader)
        }
      }
    }
  }

  private fun createUniformData(
    elapsedSeconds: Float,
    dim: Dimensions
  ): ByteArray {
    return ByteBuffer.allocate(12)
      .order(ByteOrder.LITTLE_ENDIAN)
      .putFloat(elapsedSeconds)
      .putFloat(dim.width)
      .putFloat(dim.height)
      .array()
  }

  private fun renderRuntimeShader(
    canvas: io.github.humbleui.skija.Canvas,
    pos: Vec2f,
    dim: Dimensions,
    runtimeEffect: RuntimeEffect,
    data: Data,
    imageShader: io.github.humbleui.skija.Shader
  ) {
    runtimeEffect.makeShader(data, arrayOf(imageShader), null).use { shader ->
      Paint().setShader(shader).use { paint ->
        canvas.drawRect(
          Rect.makeXYWH(pos.x, pos.y, dim.width, dim.height),
          paint
        )
      }
    }
  }

}
