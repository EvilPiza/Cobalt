package org.cobalt.util.rotation

interface IRotation {
  fun onRotationWorldRender()
  fun onRotationEnd()
  fun onRotationStart(yaw: Double, pitch: Double)
  fun isRotating(): Boolean
  fun stopRotation() { onRotationEnd() }
}
