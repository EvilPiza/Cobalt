package org.cobalt.pathfinding.theta

import java.util.concurrent.Executors
import java.util.concurrent.Future
import net.minecraft.core.BlockPos
import org.cobalt.Cobalt.minecraft

object ThetaAsyncWorker {

  private val executor = Executors.newSingleThreadExecutor()

  @Volatile
  private var currentTask: Future<*>? = null

  @Volatile
  var activeGoal: BlockPos? = null

  fun requestPath(goal: BlockPos, onDone: (ThetaPath?) -> Unit) {
    val level = minecraft.level ?: return
    val player = minecraft.player ?: return

    activeGoal = goal

    currentTask?.cancel(true)

    currentTask = executor.submit {
      val pathfinder = ThetaPathfinder(level)

      val result = pathfinder.findPath(
        player.blockPosition(),
        goal
      )

      minecraft.execute {
        onDone(result)
      }
    }
  }

  fun shutdown() {
    executor.shutdownNow()
  }

}
