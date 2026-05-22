package org.cobalt.util.skia

import io.github.humbleui.skija.Data
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.RuntimeEffect
import io.github.humbleui.skija.RuntimeEffectChild
import io.github.humbleui.types.Rect
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaContext.canvas

object SkiaShaders {

  private val START_TIME: Long = System.currentTimeMillis()

  fun loadShader(resourcePath: String): RuntimeEffect {
    try {
      SkiaShaders.javaClass.getResourceAsStream(resourcePath).use { inputStream ->
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

    val elapsedSeconds = (System.currentTimeMillis() - START_TIME) / 1000f
    val uniformBytes = ByteBuffer.allocate(12)
      .order(ByteOrder.LITTLE_ENDIAN)
      .putFloat(elapsedSeconds)
      .putFloat(dim.width)
      .putFloat(dim.height)
      .array()

    Data.makeFromBytes(uniformBytes).use { data ->
      runtimeEffect.makeShader<RuntimeEffectChild>(data, null, null).use { shader ->
        Paint().setShader(shader).use { paint ->
          canvas.drawRect(
            Rect.makeXYWH(pos.x, pos.y, dim.width, dim.height), paint
          )
        }
      }
    }
  }

}
