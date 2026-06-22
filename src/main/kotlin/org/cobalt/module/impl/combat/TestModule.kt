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
    description = "Your new nickname",
    defaultValue = "CobaltUser"
  )

  var testCheckbox by CheckboxSetting(
    name = "Test Checkbox Setting",
    description = "Enable a feature",
    defaultValue = true
  )

  var testSlider by SliderSetting(
    name = "Test Slider Setting",
    description = "Movement speed",
    defaultValue = 5,
    min = 1,
    max = 10
  )

  var testRange by RangeSetting(
    name = "Test Range Setting",
    description = "Attack range",
    default = Pair(1, 3),
    min = 0,
    max = 10
  )

  var testMode by ModeSetting(
    name = "Test Mode Setting",
    description = "Operating mode",
    defaultValue = 0,
    options = arrayOf("Aggressive", "Passive", "Balanced")
  )

  var testKeyBind by KeyBindSetting(
    name = "Test KeyBind Setting",
    description = "Activate module",
    defaultKeyCode = 32
  )

  var testButton by ButtonSetting(
    name = "Test Button Setting",
    description = "Reset settings to defaults",
    buttonLabel = "Reset"
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

