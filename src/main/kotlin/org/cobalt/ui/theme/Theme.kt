package org.cobalt.ui.theme

import java.awt.Color

// TODO: load in values
class Theme(val name: String) {

  val backgroundPrimary = Color(18, 18, 18).rgb
  val backgroundSecondary = Color(24, 24, 24).rgb
  val panel = Color(30, 30, 30).rgb
  val hover = Color(37, 37, 37).rgb
  val border = Color(42, 42, 42).rgb

  val accentPrimary = Color(58, 96, 192).rgb
  val accentHover = Color(78, 118, 212).rgb
  val accentActive = Color(44, 78, 168).rgb
  val accentGlow = Color(40, 72, 150, 64).rgb

  val textPrimary = Color(230, 230, 230).rgb
  val textSecondary = Color(179, 179, 179).rgb
  val textMuted = Color(122, 122, 122).rgb
  val textDisabled = Color(95, 95, 95).rgb

  val success = Color(63, 191, 127).rgb
  val warning = Color(230, 181, 102).rgb
  val error = Color(224, 90, 90).rgb
  val info = Color(93, 169, 233).rgb

}
