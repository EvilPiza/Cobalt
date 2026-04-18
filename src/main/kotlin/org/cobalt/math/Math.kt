package org.cobalt.math

/** Simple 3D vector */
data class SimpleVec3(
  /** X property */
  val x: Float,
  /** Y property */
  val y: Float,
  /** Z property */
  val z: Float = 0f
)

/** Dimensions wrapper */
data class Dimensions(
  /** Width property */
  val width: Float,
  /** Height property */
  val height: Float
)
