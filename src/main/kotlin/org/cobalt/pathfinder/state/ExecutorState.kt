package org.cobalt.pathfinder.state

interface ExecutorState {

  fun enter() {}
  fun onTick() {}
  fun onRender() {}
  fun exit() {}

}
