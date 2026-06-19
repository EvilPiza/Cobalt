package org.cobalt.ui.helper

import org.cobalt.ui.component.TextInputComponent.Type
import org.cobalt.util.skia.Skia

class TextInputHelper(
  private val fontSize: Float,
  val textInputType: Type,
  startText: String = "",
) {

  var text = startText
    private set

  var focused = false
    private set

  val usePlaceholder: Boolean
    get() = text.isBlank() && !focused

  private fun textWidth(str: String) =
    Skia.textWidth(Skia.regularFont, str, fontSize)

}
