package org.cobalt.pathfinder

import org.cobalt.pathfinder.calculate.PathMode
import org.cobalt.pathfinder.goal.IGoal

data class PathConfig(
  val goal: IGoal,
  val mode: PathMode = PathMode.WALK,
  val shouldSprint: Boolean = false,
  val preferShifting: Boolean = false
)
