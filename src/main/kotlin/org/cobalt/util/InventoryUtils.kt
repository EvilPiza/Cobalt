package org.cobalt.util

import net.minecraft.world.inventory.ContainerInput
import org.cobalt.Cobalt.minecraft

object InventoryUtils {

  @JvmStatic
  fun clickSlot(
    slot: Int,
    click: MouseButton = MouseButton.LEFT,
    input: ContainerInput = ContainerInput.PICKUP,
  ) {
    val player = minecraft.player ?: return
    val containerId = player.containerMenu.containerId

    minecraft.gameMode?.handleContainerInput(containerId, slot, click.ordinal, input, player)
  }

}
