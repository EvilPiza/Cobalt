package org.cobalt.util

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

object ItemUtils {

  fun getLoreLines(stack: ItemStack): List<Component> {
    return stack.get(DataComponents.LORE)?.lines ?: emptyList()
  }

}
