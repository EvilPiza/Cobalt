package org.cobalt.module.impl.misc

import org.cobalt.module.Module
import org.cobalt.module.ModuleCategory
import org.cobalt.util.setting.impl.TextSetting

internal object NickHider : Module(
  name = "NickHider",
  category = ModuleCategory.MISC,
) {

  var nickname by TextSetting(
    name = "Nickname",
    description = "Your new nickname",
    defaultValue = "CobaltUser"
  )

}
