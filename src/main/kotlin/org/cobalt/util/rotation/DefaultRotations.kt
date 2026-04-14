package org.cobalt.util.rotation

import kotlin.math.abs
import org.cobalt.Cobalt.minecraft
import org.cobalt.util.ChatUtils
import org.cobalt.util.MessageType

object DefaultRotations : IRotation {

  private var rotating = false
  private var targetYaw = 0.0
  private var targetPitch = 0.0
  private var currentYaw = 0.0
  private var currentPitch = 0.0
  private var currentSpeed = 0.0

  override fun onRotationStart(yaw: Double, pitch: Double, speed: Double) {
    rotating = true
    targetYaw = yaw
    targetPitch = pitch
    currentYaw = getPlayerYaw()
    currentPitch = getPlayerPitch()
    currentSpeed = speed
    ChatUtils.sendSystemMessage("Rotation started to $yaw, $pitch", MessageType.DEBUG)
  }

  override fun onRotationEnd() {
    rotating = false
    ChatUtils.sendSystemMessage("Ended rotation.", MessageType.DEBUG)
  }

  override fun onRotationWorldRender() {
    if (!rotating) return

    val player = getPlayer() ?: return

    val currentYaw = player.yRot.toDouble()
    val currentPitch = player.xRot.toDouble()


    val newYaw = lerpAngle(currentYaw, targetYaw, currentSpeed)
    val newPitch = lerp(currentPitch, targetPitch, currentSpeed)

    applyRotation(newYaw, newPitch)

    if (
      angleDistance(newYaw, targetYaw) < 0.5 &&
      abs(newPitch - targetPitch) < 0.5
    ) {
      stopRotation()
    }
  }
 // lerp!
  private fun lerpAngle(current: Double, target: Double, alpha: Double): Double {
    val delta = ((target - current + 540) % 360) - 180
    return current + delta * alpha
  }

  private fun lerp(a: Double, b: Double, t: Double): Double {
    return a + (b - a) * t
  }

  private fun angleDistance(a: Double, b: Double): Double {
    val d = ((b - a + 540) % 360) - 180
    return abs(d)
  }
  private fun distance(a: Double, b: Double): Double {
    return abs(a - b)
  }

  override fun isRotating(): Boolean = rotating

  private fun applyRotation(yaw: Double, pitch: Double) {
    val player = getPlayer() ?: return

    val y = yaw.toFloat()
    val p = pitch.toFloat()

    player.yRot = y
    player.xRot = p
  }

  private fun getPlayerYaw(): Double {
    return getPlayer()?.yRot?.toDouble() ?: 0.0
  }

  private fun getPlayerPitch(): Double {
    return getPlayer()?.xRot?.toDouble() ?: 0.0
  }

  private fun getPlayer() = minecraft.player

}
