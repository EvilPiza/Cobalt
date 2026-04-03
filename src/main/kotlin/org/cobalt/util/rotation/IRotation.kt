package org.cobalt.util.rotation

interface IRotation {

  fun onRotationWorldRender()
  fun onRotationEnd()
  fun onRotationStart(yaw: Double, pitch: Double, speed: Double = 0.15)
  fun isRotating(): Boolean

  fun stopRotation() {
    onRotationEnd()
  }

}
