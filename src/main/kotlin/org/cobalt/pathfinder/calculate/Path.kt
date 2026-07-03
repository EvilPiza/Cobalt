package org.cobalt.pathfinder.calculate

import kotlin.time.Duration
import org.cobalt.pathfinder.goal.IGoal

data class Path(
  val nodes: List<PathNode>,
  val timeElapsed: Duration,
  val goal: IGoal
) {

  val keyNodes: List<PathNode> = buildKeyNodes()

  // TODO: Replace this simple key nodes extractor with a better implementation (or use Theta*)
  private fun buildKeyNodes(): List<PathNode> {
    if (nodes.size <= 2) {
      return nodes
    }

    val keyNodes = mutableListOf(nodes.first())
    var lastDirection = direction(nodes[0], nodes[1])

    for (i in 2 until nodes.size) {
      val currentDirection = direction(nodes[i - 1], nodes[i])

      if (currentDirection != lastDirection) {
        keyNodes += nodes[i - 1]
        lastDirection = currentDirection
      }
    }

    keyNodes += nodes.last()
    return keyNodes
  }

  private fun direction(a: PathNode, b: PathNode) = Direction(
    (b.x - a.x).coerceIn(-1, 1),
    (b.y - a.y).coerceIn(-1, 1),
    (b.z - a.z).coerceIn(-1, 1)
  )

  private data class Direction(val dx: Int, val dy: Int, val dz: Int)

}
