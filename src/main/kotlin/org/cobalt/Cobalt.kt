package org.cobalt

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.minecraft.client.Minecraft
import org.cobalt.command.CommandManager
import org.cobalt.command.impl.MainCommand
import org.cobalt.event.EventBus
import org.cobalt.event.impl.WorldRenderEvent
import org.cobalt.module.ModuleManager

/** Main mod entrypoint and shared constants for the Cobalt client mod. */
object Cobalt : ClientModInitializer {

  /** Cached Minecraft client instance. */
  @JvmField
  val minecraft: Minecraft = Minecraft.getInstance()

  /** The Fabric ModContainer for this mod. */
  @JvmField
  val MOD_CONTAINER: ModContainer = FabricLoader.getInstance().getModContainer("cobalt").orElseThrow()

  /** Human-readable mod name. */
  @JvmField
  val MOD_NAME: String = MOD_CONTAINER.metadata.name

  /** Friendly mod version string. */
  @JvmField
  val MOD_VERSION: String = MOD_CONTAINER.metadata.version.friendlyString

  /** Called when the client initializes; registers modules and commands and wires render events. */
  override fun onInitializeClient() {
    ModuleManager.registerModules()
    CommandManager.register(MainCommand)

    // Dispatch Events
    LevelRenderEvents.END_MAIN.register { context ->
      EventBus.post(WorldRenderEvent(context))
    }
  }

}
