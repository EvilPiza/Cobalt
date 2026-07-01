package org.cobalt.pathfinder.calculate

import kotlin.time.Duration
import org.cobalt.pathfinder.goal.IGoal

data class Path(
  val nodes: List<PathNode>,
  val timeElapsed: Duration,
  val goal: IGoal
) {

  val keyNodes: List<PathNode>

  init {
    keyNodes = emptyList()
  }

}
