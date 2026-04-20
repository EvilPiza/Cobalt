package org.cobalt

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.minecraft.client.Minecraft
import org.cobalt.Cobalt.MOD_CONTAINER
import org.cobalt.Cobalt.MOD_NAME
import org.cobalt.Cobalt.MOD_VERSION
import org.cobalt.Cobalt.minecraft
import org.cobalt.command.CommandManager
import org.cobalt.command.impl.MainCommand
import org.cobalt.event.EventBus
import org.cobalt.event.impl.WorldRenderEvent
import org.cobalt.module.ModuleManager

/**
 * Main mod entrypoint and contains shared constants for Cobalt.
 *
 * @property minecraft global Minecraft client instance
 * @property MOD_CONTAINER Fabric mod container for this mod
 * @property MOD_NAME display name of the mod from metadata
 * @property MOD_VERSION version string from mod metadata
 */
object Cobalt : ClientModInitializer {

  @JvmStatic
  val minecraft: Minecraft
    get() = Minecraft.getInstance()

  @JvmField
  val MOD_CONTAINER: ModContainer = FabricLoader.getInstance().getModContainer("cobalt").orElseThrow()

  @JvmField
  val MOD_NAME: String = MOD_CONTAINER.metadata.name

  @JvmField
  val MOD_VERSION: String = MOD_CONTAINER.metadata.version.friendlyString

  override fun onInitializeClient() {
    ModuleManager.registerModules()
    CommandManager.register(MainCommand)

    LevelRenderEvents.END_MAIN.register { context ->
      EventBus.post(WorldRenderEvent(context))
    }
  }

}
