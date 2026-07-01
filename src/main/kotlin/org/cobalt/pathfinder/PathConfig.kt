package org.cobalt.pathfinder

import org.cobalt.pathfinder.calculate.PathMode
import org.cobalt.pathfinder.goal.IGoal
import org.cobalt.pathfinder.movement.Movement

class PathConfig(
  val goal: IGoal,
  val movements: Array<out Movement> = PathMode.WALK.movements,
  val shouldSprint: Boolean = true,
  val preferShifting: Boolean = false,
  val returnBestNode: Boolean = false,
) {

  val useFlyMovement: Boolean
    get() = movements.any {
      it.type == Movement.Type.FLY
    }

}
