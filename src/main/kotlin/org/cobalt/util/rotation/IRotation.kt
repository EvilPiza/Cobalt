package org.cobalt.util.rotation

/**
 * Contract for components that perform or manage rotation over time.
 *
 * Implementations should handle starting, updating and stopping rotations
 * and expose whether a rotation is currently in progress.
 */
interface IRotation {

  /**
   * Called during world rendering when rotation-related rendering or updates
   * should be performed. Typically used to update camera orientation visuals
   * or perform per-frame interpolation while a rotation is active.
   */
  fun onRotationWorldRender()

  /**
   * Called when a rotation finishes or is explicitly stopped. Implementations
   * should perform any cleanup required when rotation ends.
   */
  fun onRotationEnd()

  /**
   * Start a rotation towards the given yaw and pitch.
   *
   * @param yaw target yaw in degrees
   * @param pitch target pitch in degrees
   * @param speed interpolation speed (higher = faster). Defaults to 0.15.
   */
  fun onRotationStart(yaw: Double, pitch: Double, speed: Double = 0.15)

  /**
   * Returns true when a rotation is currently in progress.
   */
  fun isRotating(): Boolean

  /**
   * Convenience helper to stop rotation. Default implementation delegates to
   * [onRotationEnd]. Implementations may override to perform additional work.
   */
  fun stopRotation() {
    onRotationEnd()
  }

}
