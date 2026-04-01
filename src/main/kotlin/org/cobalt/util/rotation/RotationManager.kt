package org.cobalt.util.rotation

object RotationManager {
  private var rotation: IRotation = DefaultRotations

  fun setActiveRotation(newRotation: IRotation, yaw: Double, pitch: Double) {
    if (rotation.isRotating()) {
      rotation.stopRotation()
    }

    rotation = newRotation
    rotation.onRotationStart(yaw, pitch)
  }

  fun getActiveRotation(): IRotation = rotation

  fun resetRotation() {
    if (rotation.isRotating()) {
      rotation.stopRotation()
    }
    rotation = DefaultRotations
  }

  fun onWorldRender() {
    if (rotation.isRotating()) {
      rotation.onRotationWorldRender()
    }
  }
}
