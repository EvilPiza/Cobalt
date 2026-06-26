package org.cobalt.util

import org.cobalt.Cobalt.minecraft

object PlayerUtils {

  @JvmStatic
  val ign: String
    get() = minecraft.player?.gameProfile?.name ?: "Undefined"

  @JvmStatic
  val isInventoryEmpty: Boolean
    get() = minecraft.player?.inventory?.nonEquipmentItems?.all { it.isEmpty } ?: true

  @JvmStatic
  val isInventoryFull: Boolean
    get() = minecraft.player?.inventory?.nonEquipmentItems?.none { it.isEmpty } ?: false

  @JvmStatic
  fun isSuffocating(): Boolean {
    val player = minecraft.player ?: return false
    val level = minecraft.level ?: return false

    val playerBox = player.boundingBox.inflate(-0.15)
    return level.noCollision(player, playerBox).not()
  }

  @JvmStatic
  fun closeScreen() {
    val gui = minecraft.gui

    if (gui.screen() == null) {
      return
    }

    minecraft.execute {
      gui.setScreen(null)
    }
  }

}
