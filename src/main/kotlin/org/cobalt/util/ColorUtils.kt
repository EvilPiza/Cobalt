package org.cobalt.util

import kotlin.math.roundToInt
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor

object ColorUtils {

  @JvmStatic
  fun buildTextGradient(text: String, startRgb: Int, endRgb: Int): MutableComponent {
    val result = Component.empty()
    val length = text.length

    if (length <= 1) {
      return Component.literal(text).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(startRgb)))
    }

    val sr = (startRgb shr 16) and 0xFF
    val sg = (startRgb shr 8) and 0xFF
    val sb = startRgb and 0xFF

    val er = (endRgb shr 16) and 0xFF
    val eg = (endRgb shr 8) and 0xFF
    val eb = endRgb and 0xFF

    for (i in text.indices) {
      val ratio = i.toDouble() / (length - 1)

      val r = (sr + ratio * (er - sr)).roundToInt()
      val g = (sg + ratio * (eg - sg)).roundToInt()
      val b = (sb + ratio * (eb - sb)).roundToInt()

      val rgb = (r shl 16) or (g shl 8) or b

      val charText = Component.literal(text[i].toString())
        .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb)))

      result.append(charText)
    }

    return result
  }

  internal inline val Int.red get() = this shr 16 and 0xFF
  internal inline val Int.green get() = this shr 8 and 0xFF
  internal inline val Int.blue get() = this and 0xFF
  internal inline val Int.alpha get() = this shr 24 and 0xFF

}
