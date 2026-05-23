package org.cobalt.ui.theme

import java.awt.Color

data class Theme(
  val name: String,
  val backgroundPrimary: Color,
  val backgroundSecondary: Color,
  val border: Color,
  val accentPrimary: Color,
  val accentSecondary: Color,
  val textPrimary: Color,
  val textSecondary: Color,
  val textMuted: Color,
  val textDisabled: Color,
  val success: Color,
  val warning: Color,
  val error: Color,
  val info: Color
)
