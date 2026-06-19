package org.cobalt.ui.page.impl

import org.cobalt.module.ModuleCategory
import org.cobalt.module.ModuleManager
import org.cobalt.ui.component.ModuleComponent
import org.cobalt.ui.helper.ScrollHelper
import org.cobalt.ui.page.Page
import org.cobalt.util.MouseUtils
import org.cobalt.util.skia.Skia

internal object ModulesPage : Page() {

  override val title: String
    get() = "Modules"

  private val moduleComponents = mutableListOf<ModuleComponent>()
  private val scrollHelper = ScrollHelper()

  var selectedCategory: ModuleCategory = ModuleCategory.COMBAT
    set(value) {
      if (field == value) {
        return
      }

      reloadModules(value)
      field = value
    }

  private fun reloadModules(category: ModuleCategory) {
    moduleComponents.clear()
    scrollHelper.reset()

    removeAllChildren()

    ModuleManager.modules
      .filter { module -> module.category == category }
      .forEach { module ->
        val component = ModuleComponent(module)

        moduleComponents.add(component)
        addChild(component)
      }
  }

  override fun renderComponent() {
    super.renderComponent()

    val leftX  = xPos + PADDING
    var leftY  = yPos + PADDING - scrollHelper.scrollOffset
    val rightX = xPos + PADDING + ModuleComponent.WIDTH + COLUMN_GAP
    var rightY = yPos + PADDING - scrollHelper.scrollOffset

    Skia.pushScissor(xPos, yPos, width, height)

    moduleComponents.forEachIndexed { index, component ->
      if (index % 2 == 0) {
        component
          .updateBounds(leftX, leftY)
          .renderComponent()

        leftY += component.height + PADDING
      } else {
        component
          .updateBounds(rightX, rightY)
          .renderComponent()

        rightY += component.height + PADDING
      }
    }

    Skia.popScissor()
  }


  override fun mouseScrolled(horizontalAmount: Double, verticalAmount: Double): Boolean {
    if (!MouseUtils.isHoveringOver(xPos, yPos, width, height)) {
      scrollHelper.scroll(verticalAmount)
      return true
    }

    return false
  }

  private const val PADDING = 20f
  private const val COLUMN_GAP = 20f

}
