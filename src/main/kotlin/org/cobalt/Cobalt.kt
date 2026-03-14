package org.cobalt

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import org.cobalt.command.CommandManager
import org.cobalt.command.impl.MainCommand
import org.cobalt.event.EventBus
import org.cobalt.module.ModuleManager
import org.cobalt.util.helper.TickScheduler
import org.cobalt.util.ui.Renderer
import org.cobalt.util.ui.nvg.NanoVGImpl

object Cobalt : ClientModInitializer {

  @JvmField
  val MOD_CONTAINER: ModContainer = FabricLoader.getInstance().getModContainer("cobalt").orElseThrow()

  @JvmField
  val MOD_NAME: String = MOD_CONTAINER.metadata.name

  @JvmField
  val MOD_VERSION: String = MOD_CONTAINER.metadata.version.friendlyString

  @JvmStatic
  val renderer: Renderer
    get() = NanoVGImpl

  override fun onInitializeClient() {
    ModuleManager.registerModules()
    CommandManager.register(MainCommand)
  }

}
