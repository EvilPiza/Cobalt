package org.cobalt.module.impl.combat

import org.cobalt.module.Module
import org.cobalt.module.ModuleCategory
import org.cobalt.ui.component.setting.impl.ButtonSetting
import org.cobalt.ui.component.setting.impl.CheckboxSetting
import org.cobalt.ui.component.setting.impl.InfoSetting
import org.cobalt.ui.component.setting.impl.KeyBindSetting
import org.cobalt.ui.component.setting.impl.ModeSetting
import org.cobalt.ui.component.setting.impl.RangeSetting
import org.cobalt.ui.component.setting.impl.SliderSetting
import org.cobalt.ui.component.setting.impl.TextSetting

object TestModule : Module(
  name = "Test Module",
  category = ModuleCategory.COMBAT
) {

  var testText by TextSetting(
    name = "Test Text Setting",
    description = "Test Text Setting Description",
    defaultValue = "Cobalt"
  )

  var testCheckbox by CheckboxSetting(
    name = "Test Checkbox Setting",
    description = "Test Checkbox Setting Description",
    defaultValue = true
  )

  var testSlider by SliderSetting(
    name = "Test Slider Setting",
    description = "Test Slider Setting Description",
    defaultValue = 5,
    min = 1,
    max = 10
  )

  var testRange by RangeSetting(
    name = "Test Range Setting",
    description = "Test Range Setting Description",
    default = Pair(1, 3),
    min = 0,
    max = 10
  )

  var testMode by ModeSetting(
    name = "Test Mode Setting",
    description = "Test Mode Setting Description",
    defaultValue = 0,
    options = arrayOf("Mode 1", "Mode 2", "Mode 3")
  )

  var testKeyBind by KeyBindSetting(
    name = "Test KeyBind Setting",
    description = "Test KeyBind Setting Description",
    defaultKeyCode = 32
  )

  var testButton by ButtonSetting(
    name = "Test Button Setting",
    description = "Test Button Setting Description",
    buttonLabel = "Reset",
    onClick = {
      println("Test Button Clicked")
    }
  )

  val testInfo by InfoSetting(
    text = "Test Info Setting Text",
    type = InfoSetting.Type.INFO
  )

  val testSuccess by InfoSetting(
    text = "Test Info Setting Text",
    type = InfoSetting.Type.SUCCESS
  )

  val testWarning by InfoSetting(
    text = "Test Info Setting Text",
    type = InfoSetting.Type.WARNING
  )

  val testError by InfoSetting(
    text = "Test Info Setting Text",
    type = InfoSetting.Type.ERROR
  )

}

