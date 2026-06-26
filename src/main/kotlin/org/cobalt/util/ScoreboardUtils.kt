package org.cobalt.util

import net.minecraft.network.chat.Component
import net.minecraft.world.scores.DisplaySlot
import org.cobalt.Cobalt.minecraft

object ScoreboardUtils {

  @JvmStatic
  fun getTitle(): Component? {
    val scoreboard = minecraft.level?.scoreboard ?: return null
    val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return null

    return objective.displayName
  }

  @JvmStatic
  fun getLines(): List<Component?> {
    val scoreboard = minecraft.level?.scoreboard ?: return emptyList()
    val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return emptyList()

    return scoreboard.listPlayerScores(objective)
      .toList()
      .takeLast(15)
      .reversed()
      .map { it.display }
  }

}
