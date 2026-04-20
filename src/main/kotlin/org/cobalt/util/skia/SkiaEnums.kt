package org.cobalt.util.skia

/**
 * Direction for gradient interpolation in Skia rendering helpers
 */
enum class SkiaGradient {

  /** Gradient flows from top to bottom (vertical interpolation). */
  TOP_TO_BOTTOM,

  /** Gradient flows from left to right (horizontal interpolation). */
  LEFT_TO_RIGHT

}

/**
 * A side of a rectangle, used for edge-based drawing operations.
 */
enum class SkiaSide {

  /** The top edge of a rectangle. */
  TOP,

  /** The bottom edge of a rectangle. */
  BOTTOM,

  /** The left edge of a rectangle. */
  LEFT,

  /** The right edge of a rectangle. */
  RIGHT

}
