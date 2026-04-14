package org.cobalt.util.rotation

import kotlin.math.abs
import org.cobalt.Cobalt.minecraft
import org.cobalt.util.ChatUtils
import org.cobalt.util.MessageType

object DefaultRotations : IRotation {

  private const val FULL_CIRCLE = 360.0
  private const val HALF_CIRCLE = 180.0
  private const val NORMALIZE_OFFSET = FULL_CIRCLE + HALF_CIRCLE
  private const val ANGLE_TOLERANCE = 0.5
  private const val ZERO_ANGLE = 0.0

  private var rotating = false
  private var targetYaw = ZERO_ANGLE
  private var targetPitch = ZERO_ANGLE
  private var currentYaw = ZERO_ANGLE
  private var currentPitch = ZERO_ANGLE
  private var currentSpeed = ZERO_ANGLE

  private val player = minecraft.player

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

    val player = player?: return

    val currentYaw = player.yRot.toDouble()
    val currentPitch = player.xRot.toDouble()


    val newYaw = lerpAngle(currentYaw, targetYaw, currentSpeed)
    val newPitch = lerp(currentPitch, targetPitch, currentSpeed)

    applyRotation(newYaw, newPitch)

    if (
      angleDistance(newYaw, targetYaw) < ANGLE_TOLERANCE &&
      abs(newPitch - targetPitch) < ANGLE_TOLERANCE
    ) {
      stopRotation()
    }
  }

  private fun lerpAngle(current: Double, target: Double, alpha: Double): Double {
    val delta = ((target - current + NORMALIZE_OFFSET) % FULL_CIRCLE) - HALF_CIRCLE
    return current + delta * alpha
  }

  private fun lerp(a: Double, b: Double, t: Double): Double {
    return a + (b - a) * t
  }

  private fun angleDistance(a: Double, b: Double): Double {
    val d = ((b - a + NORMALIZE_OFFSET) % FULL_CIRCLE) - HALF_CIRCLE
    return abs(d)
  }

  private fun distance(a: Double, b: Double): Double {
    return abs(a - b)
  }

  override fun isRotating(): Boolean = rotating

  private fun applyRotation(yaw: Double, pitch: Double) {
    val player = player ?: return

    val y = yaw.toFloat()
    val p = pitch.toFloat()

    player.yRot = y
    player.xRot = p
  }

  private fun getPlayerYaw(): Double {
    return player?.yRot?.toDouble() ?: ZERO_ANGLE
  }

  private fun getPlayerPitch(): Double {
    return player?.xRot?.toDouble() ?: ZERO_ANGLE
  }

}
