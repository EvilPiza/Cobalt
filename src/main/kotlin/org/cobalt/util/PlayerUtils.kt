package org.cobalt.util

import org.cobalt.Cobalt.minecraft

object PlayerUtils {

  @JvmStatic
  fun getIgn(): String {
    return minecraft.player?.gameProfile?.name ?: "Undefined"
  }

}
