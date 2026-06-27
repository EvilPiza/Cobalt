package org.cobalt.util

import net.minecraft.core.BlockPos
import org.cobalt.Cobalt.minecraft
import org.cobalt.util.rotation.Rotation

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
  val rotation: Rotation
    get() {
      val player = minecraft.player!!
      return Rotation(player.yRot, player.xRot, true)
    }

  @JvmStatic
  val position: BlockPos
    get() = minecraft.player!!.blockPosition()

  @JvmStatic
  val isSuffocating: Boolean
    get() {
      val player = minecraft.player ?: return false
      val level = minecraft.level ?: return false

      val playerBox = player.boundingBox.inflate(-0.15)
      return level.noCollision(player, playerBox).not()
    }

  @JvmStatic
  fun setRotation(rotation: Rotation) {
    val player = minecraft.player ?: return

    rotation.normalize().let { rot ->
      player.xRotO = player.xRot
      player.yRotO = player.yRot
      player.yBob = player.yRot
      player.yBobO = player.yRot

      player.yRot = rot.yaw
      player.xRot = rot.pitch
    }
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
