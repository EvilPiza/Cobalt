package org.cobalt.pathfinder.movement

import org.cobalt.pathfinder.calculate.PathMode

class MovementResult {

  private var x: Int = 0
  private var y: Int = 0
  private var z: Int = 0

  var type: PathMode = PathMode.WALK
  var cost: Double = 1e6

  fun set(x: Int, y: Int, z: Int) {
    this.x = x
    this.y = y
    this.z = z
  }

  fun reset() {
    x = 0
    y = 0
    z = 0
    cost = 1e6
  }

}
