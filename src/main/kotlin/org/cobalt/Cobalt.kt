package org.cobalt

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import org.cobalt.command.CommandManager
import org.cobalt.command.impl.MainCommand
import org.cobalt.event.EventBus
import org.cobalt.event.impl.WorldRenderEvent
import org.cobalt.module.ModuleManager

object Cobalt : ClientModInitializer {

  @JvmField
  val MOD_CONTAINER: ModContainer = FabricLoader.getInstance().getModContainer("cobalt").orElseThrow()

  @JvmField
  val MOD_NAME: String = MOD_CONTAINER.metadata.name

  @JvmField
  val MOD_VERSION: String = MOD_CONTAINER.metadata.version.friendlyString

  override fun onInitializeClient() {
    ModuleManager.registerModules()
    CommandManager.register(MainCommand)

    // Dispatch Events
    LevelRenderEvents.END_MAIN.register { context ->
      EventBus.post(WorldRenderEvent(context))
    }
  }

}
