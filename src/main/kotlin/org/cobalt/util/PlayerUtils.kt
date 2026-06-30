package org.cobalt.util

import kotlin.math.ceil
import kotlin.math.floor
import net.minecraft.client.player.LocalPlayer
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
  val player: LocalPlayer
    get() = minecraft.player!!

  @JvmStatic
  val position: BlockPos
    get() = BlockPos(
      floor(player.x).toInt(),
      ceil(player.y - 0.25).toInt(),
      floor(player.z).toInt()
    )

  @JvmStatic
  val isSuffocating: Boolean
    get() {
      val player = minecraft.player ?: return false
      val level = minecraft.level ?: return false

      return !level.noCollision(
        player, player.boundingBox.inflate(-0.15)
      )
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
