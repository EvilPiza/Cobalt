package org.cobalt.math

/**
 * Simple 2D vector
 *
 * @property x x property
 * @property y y property
 */
data class Vec2f(
  val x: Float,
  val y: Float,
)

/**
 * Dimensions wrapper
 *
 * @property width width property
 * @property height height property
 */
data class Dimensions(
  val width: Float,
  val height: Float
)
