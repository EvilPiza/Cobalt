package org.cobalt.module.impl.combat

import org.cobalt.module.ModuleCategory
import org.cobalt.module.type.Script

object TestScript : Script(
  name = "Test Script",
  category = ModuleCategory.COMBAT,
  backgroundResourcePath = "/assets/cobalt/ui/scripts/testscript.png"
)
