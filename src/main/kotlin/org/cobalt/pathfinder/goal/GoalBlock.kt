package org.cobalt.pathfinder.goal

class GoalBlock(
  val goalX: Int,
  val goalY: Int,
  val goalZ: Int
) : IGoal {

  override fun heuristic(x: Int, y: Int, z: Int): Double {
    return 1.0 // todo: implement good heuristic
  }

  override fun isAtGoal(x: Int, y: Int, z: Int): Boolean {
    return goalX == x && goalY == y && goalZ == z
  }

}
