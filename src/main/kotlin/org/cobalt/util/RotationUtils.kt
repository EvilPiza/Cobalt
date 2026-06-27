package org.cobalt.util

import kotlin.math.abs
import net.minecraft.util.Mth
import org.cobalt.Cobalt.minecraft
import org.cobalt.util.rotation.Rotation

object RotationUtils {

  @JvmStatic
  val gcd: Double
    get() = (mouseSensitivityFactor.toFloat() * MOUSE_TURN_SCALE).toDouble()

  @JvmStatic
  val mouseSensitivityFactor: Double
    get() {
      val sensitivity = minecraft.options.sensitivity().get()
      val f = sensitivity * 0.6f + 0.2f
      return f * f * f * 8.0
    }

  @JvmStatic
  fun angleDifference(a: Float, b: Float): Float {
    return Mth.wrapDegrees(a - b)
  }

  @JvmStatic
  fun approximatelyEquals(current: Rotation, other: Rotation, tolerance: Float = 2f): Boolean {
    val deltaYaw = angleDifference(other.yaw, current.yaw)
    val deltaPitch = angleDifference(other.pitch, current.pitch)
    return abs(deltaYaw) <= tolerance && abs(deltaPitch) <= tolerance
  }

  private const val MOUSE_TURN_SCALE = 0.15f

}
