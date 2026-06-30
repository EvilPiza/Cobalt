package org.cobalt.pathfinder.precompute

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import org.cobalt.pathfinder.movement.CalculationContext
import org.cobalt.pathfinder.movement.MovementHelper

class PrecomputedData {

  private val data = ByteArray(Block.BLOCK_STATE_REGISTRY.size())

  companion object {
    private const val COMPLETED_MASK = 1 shl 0
    private const val CAN_PASS_THROUGH_MAYBE_MASK = 1 shl 1
    private const val CAN_PASS_THROUGH_MASK = 1 shl 2
    private const val CAN_STAND_ON_MAYBE_MASK = 1 shl 3
    private const val CAN_STAND_ON_MASK = 1 shl 4
  }

  private fun fillData(id: Int, state: BlockState): Byte {
    var blockData = 0

    when (MovementHelper.canPassThroughState(state)) {
      Ternary.YES -> blockData = 0 or CAN_PASS_THROUGH_MASK
      Ternary.MAYBE -> blockData = 0 or CAN_PASS_THROUGH_MAYBE_MASK
      Ternary.NO -> {}
    }

    when (MovementHelper.canStandOnState(state)) {
      Ternary.YES -> blockData = blockData or CAN_STAND_ON_MASK
      Ternary.MAYBE -> blockData = blockData or CAN_STAND_ON_MAYBE_MASK
      Ternary.NO -> {}
    }

    blockData = blockData or COMPLETED_MASK

    val byteResult = blockData.toByte()
    data[id] = byteResult

    return byteResult
  }

  fun canPassThrough(ctx: CalculationContext, x: Int, y: Int, z: Int, state: BlockState): Boolean {
    val id = Block.BLOCK_STATE_REGISTRY.getId(state)
    var blockData = data[id].toInt()

    if ((blockData and COMPLETED_MASK) == 0) {
      blockData = fillData(id, state).toInt()
    }

    return if ((blockData and CAN_PASS_THROUGH_MAYBE_MASK) != 0) {
      MovementHelper.canPassThrough(ctx.bsa, x, y, z, state)
    } else {
      (blockData and CAN_PASS_THROUGH_MASK) != 0
    }
  }

  fun canStandOn(ctx: CalculationContext, x: Int, y: Int, z: Int, state: BlockState): Boolean {
    val id = Block.BLOCK_STATE_REGISTRY.getId(state)
    var blockData = data[id].toInt()

    if ((blockData and COMPLETED_MASK) == 0) {
      blockData = fillData(id, state).toInt()
    }

    return if ((blockData and CAN_STAND_ON_MAYBE_MASK) != 0) {
      MovementHelper.canStandOn(ctx.bsa, x, y, z, state)
    } else {
      (blockData and CAN_STAND_ON_MASK) != 0
    }
  }

}
