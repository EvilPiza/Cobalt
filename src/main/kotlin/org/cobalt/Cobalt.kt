package org.cobalt

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import org.cobalt.command.CommandManager
import org.cobalt.command.impl.MainCommand

object Cobalt : ClientModInitializer {

  @JvmField
  val MOD_CONTAINER: ModContainer = FabricLoader.getInstance().getModContainer("cobalt").orElseThrow()

  @JvmField
  val MOD_NAME: String = MOD_CONTAINER.metadata.name

  @JvmField
  val MOD_VERSION: String = MOD_CONTAINER.metadata.version.friendlyString

  override fun onInitializeClient() {
    CommandManager.register(MainCommand)
    CommandManager.hookCommandRegistration()
  }

}
