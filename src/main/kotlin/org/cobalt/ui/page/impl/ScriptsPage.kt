package org.cobalt.ui.page.impl

import org.cobalt.module.ModuleManager
import org.cobalt.module.type.Script
import org.cobalt.ui.animation.EaseOutAnimation
import org.cobalt.ui.component.ScriptComponent
import org.cobalt.ui.helper.ScrollHelper
import org.cobalt.ui.page.Page
import org.cobalt.util.MouseUtils
import org.cobalt.util.skia.Skia

object ScriptsPage : Page() {

  override val title: String
    get() = "Scripts"

  private val scriptComponents = mutableListOf<ScriptComponent>()
  private val scrollHelper = ScrollHelper()
  private val openingOffset = EaseOutAnimation(200L)

  override fun initializePage() {
    openingOffset.start()
    scriptComponents.clear()
    scrollHelper.reset()

    removeAllChildren()

    ModuleManager.modules
      .filterIsInstance<Script>()
      .forEach { script ->
        val component = ScriptComponent(script)

        println(script.name)

        scriptComponents.add(component)
        addChild(component)
      }
  }

  override fun renderComponent() {
    super.renderComponent()

    val pageOffset = openingOffset.get(-30f, 0f)
    val baseY = yPos + PADDING + pageOffset - scrollHelper.scrollOffset
    val columnX = floatArrayOf(xPos + PADDING, xPos + PADDING + ScriptComponent.WIDTH + COLUMN_GAP)
    val columnY = floatArrayOf(baseY, baseY)

    Skia.pushScissor(xPos, yPos, width, height)

    scriptComponents.forEachIndexed { index, component ->
      val col = index % 2

      component
        .updateBounds(columnX[col], columnY[col])
        .renderComponent()

      columnY[col] += component.height + PADDING
    }

    Skia.popScissor()

    val contentHeight = columnY.max() + scrollHelper.scrollOffset - yPos
    scrollHelper.updateMaxScroll(contentHeight, height)
  }

  override fun mouseScrolled(horizontalAmount: Double, verticalAmount: Double): Boolean {
    if (MouseUtils.isHoveringOver(xPos, yPos, width, height)) {
      scrollHelper.scroll(verticalAmount)
      return true
    }

    return false
  }

  private const val PADDING = 20f
  private const val COLUMN_GAP = 20f

}
