package org.cobalt

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.minecraft.SharedConstants
import net.minecraft.client.Minecraft
import org.cobalt.command.CommandManager
import org.cobalt.command.impl.MainCommand
import org.cobalt.event.EventBus
import org.cobalt.event.impl.WorldRenderEvent
import org.cobalt.module.ModuleManager
import org.slf4j.LoggerFactory

object Cobalt : ClientModInitializer {

  @JvmStatic
  val minecraft: Minecraft
    get() = Minecraft.getInstance()

  private val logger = LoggerFactory.getLogger(this::class.java)

  @JvmField
  val MOD_CONTAINER: ModContainer = FabricLoader.getInstance().getModContainer("cobalt").orElseThrow()

  @JvmField
  val MOD_NAME: String = MOD_CONTAINER.metadata.name

  @JvmField
  val MOD_VERSION: String = MOD_CONTAINER.metadata.version.friendlyString

  override fun onInitializeClient() {
    logger.info("Initializing $MOD_NAME ${SharedConstants.getCurrentVersion().name()} (v$MOD_VERSION)")

    ModuleManager.registerModules()
    CommandManager.register(MainCommand)

    LevelRenderEvents.END_MAIN.register { context ->
      EventBus.post(WorldRenderEvent(context))
    }
  }

}
