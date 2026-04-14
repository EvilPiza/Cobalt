package org.cobalt.ui

import java.awt.Color

// TODO: bring this over to a theme manager
object ColorPalette {

  /** Primary background color used for main surfaces (RGB int). */
  val BACKGROUND_PRIMARY = Color(18, 18, 18).rgb

  /** Secondary background color for panels and elevated surfaces (RGB int). */
  val BACKGROUND_SECONDARY = Color(24, 24, 24).rgb

  /** Panel background color for containers (RGB int). */
  val PANEL = Color(30, 30, 30).rgb

  /** Hover background color for interactive elements (RGB int). */
  val HOVER = Color(37, 37, 37).rgb

  /** Border color for separators and outlines (RGB int). */
  val BORDER = Color(42, 42, 42).rgb

  /** Primary accent color for highlights and buttons (RGB int). */
  val ACCENT_PRIMARY = Color(79, 140, 255).rgb

  /** Accent color used on hover states (RGB int). */
  val ACCENT_HOVER = Color(106, 162, 255).rgb

  /** Accent color used for active/pressed states (RGB int). */
  val ACCENT_ACTIVE = Color(58, 116, 230).rgb

  /** Accent glow color with alpha for soft glow effects (RGBA int). */
  val ACCENT_GLOW = Color(47, 95, 191, 64).rgb

  /** Primary text color for high-contrast text (RGB int). */
  val TEXT_PRIMARY = Color(230, 230, 230).rgb

  /** Secondary text color for less prominent text (RGB int). */
  val TEXT_SECONDARY = Color(179, 179, 179).rgb

  /** Muted text color for de-emphasized labels (RGB int). */
  val TEXT_MUTED = Color(122, 122, 122).rgb

  /** Disabled text color for inactive elements (RGB int). */
  val TEXT_DISABLED = Color(95, 95, 95).rgb

  /** Success semantic color for positive states (RGB int). */
  val SUCCESS = Color(63, 191, 127).rgb

  /** Warning semantic color for cautionary states (RGB int). */
  val WARNING = Color(230, 181, 102).rgb

  /** Error semantic color for negative states (RGB int). */
  val ERROR = Color(224, 90, 90).rgb

  /** Informational semantic color for neutral/utility states (RGB int). */
  val INFO = Color(93, 169, 233).rgb

}
