package org.cobalt

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import org.cobalt.command.CommandManager
import org.cobalt.command.impl.MainCommand
import org.cobalt.event.EventBus
import org.cobalt.module.ModuleManager
import org.cobalt.util.render.Render2D
import org.cobalt.util.render.Render3D
import org.cobalt.util.render.impl.GizmoRenderer3D
import org.cobalt.util.render.impl.SkijaRenderer2D

object Cobalt : ClientModInitializer {

  @JvmField
  val MOD_CONTAINER: ModContainer = FabricLoader.getInstance().getModContainer("cobalt").orElseThrow()

  @JvmField
  val MOD_NAME: String = MOD_CONTAINER.metadata.name

  @JvmField
  val MOD_VERSION: String = MOD_CONTAINER.metadata.version.friendlyString

  @JvmStatic
  val render2D: Render2D
    get() = SkijaRenderer2D

  @JvmStatic
  val render3D: Render3D
    get() = GizmoRenderer3D

  override fun onInitializeClient() {
    ModuleManager.registerModules()
    CommandManager.register(MainCommand)
  }

}
