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

  @JvmStatic
  fun selectHotbarSlot(slot: Int): Boolean {
    val player = minecraft.player ?: return false

    if (slot !in 0..8) {
      return false
    }

    player.inventory.selectedSlot = slot
    return true
  }

  @JvmStatic
  fun holdItem(name: String): Boolean {
    val slot = findItemInHotbar(name)

    if (slot == -1) {
      return false
    }

    return selectHotbarSlot(slot)
  }

  @JvmStatic
  fun findItemInHotbar(name: String): Int {
    val player = minecraft.player ?: return -1
    val inventory = player.inventory

    for (i in 0..8) {
      val stack = inventory.getItem(i)

      if (stack.isEmpty) {
        continue
      }

      val displayName = stack.hoverName.string

      if (displayName.contains(name, ignoreCase = true)) {
        return i
      }
    }

    return -1
  }

  @JvmStatic
  fun findItemInHotbarWithLore(lore: String): Int {
    val player = minecraft.player ?: return -1
    val inventory = player.inventory

    for (slot in 0..8) {
      val stack = inventory.getItem(slot)

      if (stack.isEmpty) {
        continue
      }

      for (line in ItemUtils.getLoreLines(stack)) {
        if (line.string.contains(lore, ignoreCase = true)) {
          return slot
        }
      }
    }

    return -1
  }

  @JvmStatic
  fun findItemInInventory(name: String): Int {
    val player = minecraft.player ?: return -1
    val inventory = player.inventory

    for (slot in 0 until inventory.containerSize) {
      val stack = inventory.getItem(slot)

      if (stack.isEmpty) {
        continue
      }

      if (stack.hoverName.string.contains(name, ignoreCase = true)) {
        return slot
      }
    }

    return -1
  }

  @JvmStatic
  fun findItemInInventoryWithLore(lore: String): Int {
    val player = minecraft.player ?: return -1
    val inventory = player.inventory

    for (slot in 0 until inventory.containerSize) {
      val stack = inventory.getItem(slot)

      if (stack.isEmpty) {
        continue
      }

      for (line in ItemUtils.getLoreLines(stack)) {
        if (line.string.contains(lore, ignoreCase = true)) {
          return slot
        }
      }
    }

    return -1
  }

}
