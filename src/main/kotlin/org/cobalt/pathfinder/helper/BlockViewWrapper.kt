package org.cobalt.pathfinder.helper

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState

class BlockViewWrapper(
  val bsa: BlockStateAccessor,
) : BlockGetter {

  override fun getBlockEntity(pos: BlockPos): BlockEntity? {
    return null
  }

  override fun getBlockState(pos: BlockPos): BlockState {
    return bsa.get(pos.x, pos.y, pos.z)
  }

  override fun getFluidState(pos: BlockPos): FluidState {
    return getBlockState(pos).fluidState
  }

  override fun getHeight(): Int {
    return bsa.level.height
  }

  override fun getMinY(): Int {
    return bsa.level.minY
  }

}
