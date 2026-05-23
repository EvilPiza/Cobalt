package org.cobalt.ui.notification

import kotlin.time.Duration
import org.cobalt.ui.UIComponent
import org.cobalt.ui.animation.BounceAnimation
import org.cobalt.ui.animation.EaseOutAnimation
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.WindowUtils.windowWidth
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaSide
import org.cobalt.util.skia.SkiaText
import org.cobalt.util.skia.TextStyle

internal class Notification(
  private val title: String,
  private val description: String,
  private val duration: Duration,
) : UIComponent(
  width = DEFAULT_WIDTH,
  height = calculateHeight(title, description)
) {

  private val slideInAnim = BounceAnimation(duration = 300L)
  private val slideDownAnim = EaseOutAnimation(duration = 200L)
  private val slideOutAnim = EaseOutAnimation(duration = 400L)

  private var isExpired: Boolean = false
  private var startTime: Long = 0L
  private var expiryTime: Long = 0L

  var targetY: Float = 0f
  var previousY: Float = 0f

  fun start(currentTime: Long) {
    slideInAnim.start()
    startTime = System.currentTimeMillis()
    expiryTime = currentTime + slideInAnim.duration + duration.inWholeMilliseconds
    isExpired = false
  }

  fun checkExpiry(currentTime: Long) {
    if (!isExpired && currentTime >= expiryTime) {
      isExpired = true
      slideOutAnim.start()
    }
  }

  fun moveTo(newTargetY: Float) {
    if (newTargetY != targetY) {
      previousY = targetY + slideDownAnim.get(previousY - targetY, 0f, false)
      targetY = newTargetY
      slideDownAnim.start()
    }
  }

  override fun renderComponent() {
    val resolvedX = if (isExpired) {
      slideOutAnim.get(windowWidth - width - SCREEN_MARGIN - CONTENT_PADDING, windowWidth, false)
    } else {
      slideInAnim.get(windowWidth, windowWidth - width - SCREEN_MARGIN - CONTENT_PADDING, false)
    }

    val resolvedY = targetY + slideDownAnim.get(previousY - targetY, 0f, false)
    updateBounds(resolvedX, resolvedY)

    SkiaShapes.drawRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      CORNER_RADIUS,
      theme.backgroundPrimary.rgb
    )

    drawText()
    drawProgressBar()
  }

  private fun drawText() {
    val contentWidth = width - CONTENT_PADDING * 2

    SkiaText.drawWrappedText(
      SkiaText.boldFont,
      title,
      Vec2f(xPos + CONTENT_PADDING, yPos + CONTENT_PADDING),
      contentWidth,
      TextStyle(TITLE_FONT_SIZE, theme.textPrimary.rgb)
    )

    val titleHeight = SkiaText.getWrappedTextHeight(
      SkiaText.boldFont, title, contentWidth, TITLE_FONT_SIZE
    )

    SkiaText.drawWrappedText(
      SkiaText.boldFont,
      description,
      Vec2f(xPos + CONTENT_PADDING, yPos + CONTENT_PADDING + titleHeight + TITLE_DESCRIPTION_GAP),
      contentWidth,
      TextStyle(DESCRIPTION_FONT_SIZE, theme.textSecondary.rgb)
    )
  }

  private fun drawProgressBar() {
    val progress = calculateProgress(System.currentTimeMillis())
    val fillWidth = width * progress

    SkiaShapes.drawHalfRoundedRect(
      Vec2f(xPos, yPos + height - PROGRESS_BAR_HEIGHT),
      Dimensions(width, PROGRESS_BAR_HEIGHT),
      CORNER_RADIUS,
      theme.backgroundSecondary.rgb,
      SkiaSide.BOTTOM
    )

    if (fillWidth > 0f) {
      SkiaShapes.drawHalfRoundedRect(
        Vec2f(xPos, yPos + height - PROGRESS_BAR_HEIGHT),
        Dimensions(fillWidth, PROGRESS_BAR_HEIGHT),
        CORNER_RADIUS,
        theme.accentPrimary.rgb,
        SkiaSide.BOTTOM
      )
    }
  }

  private fun calculateProgress(currentTime: Long): Float {
    if (isExpired) return 0f
    val totalDuration = duration.inWholeMilliseconds.toFloat()
    val elapsed = (currentTime - startTime - slideInAnim.duration).toFloat()
    return (1f - (elapsed / totalDuration)).coerceIn(0f, 1f)
  }

  fun isDone(): Boolean {
    return isExpired && !slideOutAnim.isAnimating()
  }

  companion object {
    private const val DEFAULT_WIDTH: Float = 350f
    private const val MIN_HEIGHT: Float = 100f

    private const val SCREEN_MARGIN: Float = 10f
    private const val CONTENT_PADDING: Float = 15f
    private const val TITLE_DESCRIPTION_GAP: Float = 10f
    private const val CORNER_RADIUS: Float = 5f

    private const val TITLE_FONT_SIZE: Float = 16f
    private const val DESCRIPTION_FONT_SIZE: Float = 14f

    private const val PROGRESS_BAR_HEIGHT: Float = 5f

    private fun calculateHeight(title: String, description: String): Float {
      val titleHeight = SkiaText.getWrappedTextHeight(
        SkiaText.boldFont,
        title,
        DEFAULT_WIDTH - CONTENT_PADDING * 2,
        TITLE_FONT_SIZE
      )

      val descHeight = SkiaText.getWrappedTextHeight(
        SkiaText.boldFont,
        description,
        DEFAULT_WIDTH - CONTENT_PADDING * 2,
        DESCRIPTION_FONT_SIZE
      )

      val verticalOverhead =
        CONTENT_PADDING * 2 + TITLE_DESCRIPTION_GAP + PROGRESS_BAR_HEIGHT

      return maxOf(MIN_HEIGHT, titleHeight + descHeight + verticalOverhead)
    }
  }

}
