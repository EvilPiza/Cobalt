package org.cobalt.command

import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.ChatSendEvent

object CommandManager {

  private val commands = ObjectRBTreeSet<Command>()

  init {
    EventBus.register(this)
  }

  @JvmStatic
  fun register(command: Command) {
    if (!commands.add(command)) {
      error("'${command.name}' is already registered")
    }
  }

  @JvmStatic
  fun unregister(command: Command) {
    commands.remove(command)
  }

  @SubscribeEvent
  fun handleCommandExecution(event: ChatSendEvent) {
    // TODO: handle command execution
  }

  @JvmStatic
  fun getPrefix(): String = "."

}
