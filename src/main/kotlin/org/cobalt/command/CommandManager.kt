package org.cobalt.command

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback

object CommandManager {

  private val commandsList = mutableListOf<Command>()

  init {
    ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
      commandsList.forEach { it.dispatch(dispatcher) }
    }
  }

  @JvmStatic
  fun register(command: Command) {
    commandsList.add(command)
  }

  @JvmStatic
  fun unregister(command: Command) {
    commandsList.remove(command)
  }

}
