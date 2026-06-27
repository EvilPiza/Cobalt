package org.cobalt.pathfinder.goal

interface IGoal {

  fun heuristic(x: Int, y: Int, z: Int): Double
  fun isAtGoal(x: Int, y: Int, z: Int): Boolean

}
