package org.cobalt.util.skia

/**
 * Lightweight enums used by the Skia-based renderer helpers.
 *
 * These enums provide small, strongly-typed descriptors that make drawing code
 * easier to read and less error-prone than using raw booleans or integers.
 */
enum class SkiaGradient {
  /**
   * Indicates a gradient that interpolates colors from the top edge toward the
   * bottom edge of the target rectangle (y increases).
   */
  TOP_TO_BOTTOM,

  /**
   * Indicates a gradient that interpolates colors from the left edge toward the
   * right edge of the target rectangle (x increases).
   */
  LEFT_TO_RIGHT
}

/**
 * Represents a side of a rectangle or box and is useful for APIs that need to
 * specify a particular edge (for example, where to draw a border or place a
 * badge).
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
