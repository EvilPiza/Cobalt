package org.cobalt.pathfinding.theta

import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.system.measureNanoTime
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB

class ThetaPathfinder(
  private val level: Level,
  private val entityWidth: Float = 0.6f,
  private val entityHeight: Float = 1.8f,
  private val maxIterations: Int = 250000,
) {

  private val openSet = PriorityQueue<ThetaNode>(compareBy { it.f })
  private val nodes = HashMap<Long, ThetaNode>()

  private var expandedNodes = 0
  private var lineOfSightChecks = 0

  fun findPath(start: BlockPos, goal: BlockPos): ThetaPath {

    expandedNodes = 0
    lineOfSightChecks = 0

    openSet.clear()
    nodes.clear()

    lateinit var result: ThetaPath

    val nanos = measureNanoTime {

      val startNode = getNode(start)
      val goalNode = getNode(goal)

      startNode.g = 0.0
      startNode.h = heuristic(start, goal)

      openSet.add(startNode)

      var iterations = 0

      while (openSet.isNotEmpty()) {

        if (++iterations > maxIterations) {
          break
        }

        val current = openSet.poll()

        if (current.closed) {
          continue
        }

        current.closed = true
        expandedNodes++

        if (current.pos == goal) {

          val path = smoothPath(reconstructPath(current))

          result = ThetaPath(
            path,
            ThetaPathMetrics(
              expandedNodes,
              lineOfSightChecks,
              path.size,
              nanosToMs(0),
            )
          )

          return@measureNanoTime
        }

        for (neighborPos in getNeighbors(current.pos)) {

          if (!isWalkable(neighborPos)) {
            continue
          }

          val neighbor = getNode(neighborPos)

          if (neighbor.closed) {
            continue
          }

          updateVertex(current, neighbor, goalNode)
        }
      }

      result = ThetaPath(
        emptyList(),
        ThetaPathMetrics(
          expandedNodes,
          lineOfSightChecks,
          0,
          nanosToMs(0),
        )
      )
    }

    result = ThetaPath(
      result.points,
      ThetaPathMetrics(
        result.metrics.expandedNodes,
        result.metrics.lineOfSightChecks,
        result.metrics.pathLength,
        nanosToMs(nanos),
      )
    )

    return result
  }

  private fun updateVertex(
    current: ThetaNode,
    neighbor: ThetaNode,
    goal: ThetaNode,
  ) {

    val parent = current.parent

    if (parent != null && hasLineOfSight(parent.pos, neighbor.pos)) {

      val newG = parent.g + distance(parent.pos, neighbor.pos)

      if (newG < neighbor.g) {
        neighbor.g = newG
        neighbor.parent = parent
        neighbor.h = heuristic(neighbor.pos, goal.pos)

        openSet.add(neighbor)
      }

    } else {

      val newG = current.g + distance(current.pos, neighbor.pos)

      if (newG < neighbor.g) {
        neighbor.g = newG
        neighbor.parent = current
        neighbor.h = heuristic(neighbor.pos, goal.pos)

        openSet.add(neighbor)
      }
    }
  }

  private fun reconstructPath(end: ThetaNode): List<BlockPos> {

    val path = mutableListOf<BlockPos>()

    var current: ThetaNode? = end

    while (current != null) {
      path += current.pos
      current = current.parent
    }

    path.reverse()

    return path
  }

  private fun smoothPath(path: List<BlockPos>): List<BlockPos> {

    if (path.size <= 2) {
      return path
    }

    val result = mutableListOf<BlockPos>()

    var anchor = path.first()

    result += anchor

    var i = 2

    while (i < path.size) {

      val candidate = path[i]

      if (!hasLineOfSight(anchor, candidate)) {

        val previous = path[i - 1]

        result += previous
        anchor = previous
      }

      i++
    }

    result += path.last()

    return result
  }

  private fun hasLineOfSight(a: BlockPos, b: BlockPos): Boolean {

    lineOfSightChecks++

    val ax = a.x + 0.5
    val ay = a.y + 0.1
    val az = a.z + 0.5

    val bx = b.x + 0.5
    val by = b.y + 0.1
    val bz = b.z + 0.5

    val dx = bx - ax
    val dy = by - ay
    val dz = bz - az

    val steps = (sqrt(dx * dx + dy * dy + dz * dz) * 4.0).toInt()

    for (i in 0..steps) {

      val t = i.toDouble() / steps.toDouble()

      val x = ax + dx * t
      val y = ay + dy * t
      val z = az + dz * t

      val pos = BlockPos(
        x.toInt(),
        y.toInt(),
        z.toInt(),
      )

      if (!isPassable(pos)) {
        return false
      }
    }

    return true
  }

  private fun getNeighbors(pos: BlockPos): List<BlockPos> {

    val neighbors = ArrayList<BlockPos>(26)

    for (x in -1..1) {
      for (y in -1..1) {
        for (z in -1..1) {

          if (x == 0 && y == 0 && z == 0) {
            continue
          }

          val next = pos.offset(x, y, z)

          if (abs(next.y - pos.y) > 1) {
            continue
          }

          neighbors += next
        }
      }
    }

    return neighbors
  }

  private fun isWalkable(pos: BlockPos): Boolean {

    val below = pos.below()

    if (level.getBlockState(below).isAir) {
      return false
    }

    return isPassable(pos)
  }

  private fun isPassable(pos: BlockPos): Boolean {

    val box = AABB(
      pos.x + 0.5 - entityWidth / 2.0,
      pos.y.toDouble(),
      pos.z + 0.5 - entityWidth / 2.0,
      pos.x + 0.5 + entityWidth / 2.0,
      pos.y + entityHeight.toDouble(),
      pos.z + 0.5 + entityWidth / 2.0,
    )

    return level.noCollision(box)
  }

  private fun heuristic(a: BlockPos, b: BlockPos): Double {
    return distance(a, b)
  }

  private fun distance(a: BlockPos, b: BlockPos): Double {

    val dx = (a.x - b.x).toDouble()
    val dy = (a.y - b.y).toDouble()
    val dz = (a.z - b.z).toDouble()

    return sqrt(dx * dx + dy * dy + dz * dz)
  }

  private fun getNode(pos: BlockPos): ThetaNode {
    return nodes.getOrPut(pos.asLong()) {
      ThetaNode(pos)
    }
  }

  private fun nanosToMs(nanos: Long): Double {
    return nanos / 1_000_000.0
  }
}
