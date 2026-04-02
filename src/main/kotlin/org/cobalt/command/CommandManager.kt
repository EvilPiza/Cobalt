package org.cobalt.command

import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet
import org.cobalt.command.annotation.DefaultHandler
import org.cobalt.command.annotation.SubCommand
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.ChatSendEvent
import org.cobalt.util.ChatUtils

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
    val prefix = getPrefix()
    if (!event.message.startsWith(prefix)) return

    val parts = event.message.removePrefix(prefix).split(" ")
    val inputName = parts[0]
    val args = parts.drop(1)

    val command = commands.find { it.name.equals(inputName, ignoreCase = true) }

    if (command != null) {
      executeCommand(command, args)
      event.setCancelled(true)
    }
  }

  private fun executeCommand(command: Command, args: List<String>) {
    val methods = command::class.java.declaredMethods

    if (args.isEmpty()) {
      val defaultMethod = methods.find { it.isAnnotationPresent(DefaultHandler::class.java) }
      defaultMethod?.invoke(command)
      return
    }

    val subCommandName = args[0]
    val subArgs = args.drop(1)

    val subMethod = methods.find {
      it.isAnnotationPresent(SubCommand::class.java) && it.name.equals(subCommandName, ignoreCase = true)
    }

    if (subMethod != null) {
      val parameters = subMethod.parameters
      val convertedArgs = mutableListOf<Any?>()

      for (i in parameters.indices) {
        val type = parameters[i].type
        val value = subArgs.getOrNull(i)

        convertedArgs.add(when (type) {
          Double::class.java -> value?.toDoubleOrNull() ?: 0.0
          Int::class.java -> value?.toIntOrNull() ?: 0
          String::class.java -> value ?: ""
          else -> null
        })
      }

      subMethod.invoke(command, *convertedArgs.toTypedArray())
    } else {
      ChatUtils.sendMessage("Unknown subcommand: $subCommandName")
    }
  }


  @JvmStatic
  fun getPrefix(): String = "."

}
