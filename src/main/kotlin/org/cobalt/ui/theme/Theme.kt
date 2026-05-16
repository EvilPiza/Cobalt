package org.cobalt.ui.theme

import java.awt.Color

// TODO: load in values
class Theme(val name: String) {

  val backgroundPrimary = Color(10, 10, 11).rgb
  val backgroundSecondary = Color(15, 15, 16).rgb
  val border = Color(34, 34, 35).rgb

  val accentPrimary = Color(40, 90, 210).rgb
  val accentSecondary = Color(70, 130, 255).rgb

  val textPrimary = Color(230, 230, 230).rgb
  val textSecondary = Color(179, 179, 179).rgb
  val textMuted = Color(122, 122, 122).rgb
  val textDisabled = Color(95, 95, 95).rgb

  val success = Color(63, 191, 127).rgb
  val warning = Color(230, 181, 102).rgb
  val error = Color(224, 90, 90).rgb
  val info = Color(93, 169, 233).rgb

}
