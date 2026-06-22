package org.cobalt.module.impl.misc

import com.jagrosh.discordipc.IPCClient
import com.jagrosh.discordipc.entities.ActivityType
import com.jagrosh.discordipc.entities.RichPresence
import com.jagrosh.discordipc.entities.pipe.PipeStatus
import java.time.OffsetDateTime
import java.time.OffsetTime
import org.cobalt.Cobalt
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.TickEvent
import org.cobalt.module.Module
import org.cobalt.module.ModuleCategory
import org.slf4j.LoggerFactory

internal object DiscordRPC : Module(
  name = "DiscordRPC",
  category = ModuleCategory.MISC,
) {

  private val logger = LoggerFactory.getLogger(this::class.java)

  private var client: IPCClient? = null
  private var rpc = RichPresence.Builder()
  private var lastUpdate = 0L

  private const val APPLICATION_ID = 1441864552936636519L

  init {
    val minecraftVersion = "Minecraft ${Cobalt.MINECRAFT_VERSION}"

    rpc.setActivityType(ActivityType.Playing)
    rpc.setLargeImage("logo", "${Cobalt.MOD_NAME} ${Cobalt.MOD_VERSION}", null)
    rpc.setSmallImage("minecraft", minecraftVersion, null)
    rpc.setDetails("Playing $minecraftVersion")
    rpc.setStartTimestamp(System.currentTimeMillis())

    EventBus.register(this)
  }

  @SubscribeEvent
  fun onTick(ignored: TickEvent.Start) {
    if (enabled) {
      connectIpc()
    } else {
      disconnectIpc()
    }

    val client = client

    if (client == null || client.status != PipeStatus.CONNECTED) {
      return
    }

    if (System.currentTimeMillis() - lastUpdate < 1800000) {
      return
    }

    rpc.setState(states.random())
    client.sendRichPresence(rpc.build())
    lastUpdate = System.currentTimeMillis()
  }

  private fun connectIpc() {
    if (client?.status == PipeStatus.CONNECTED) {
      return
    }

    runCatching {
      client = IPCClient(APPLICATION_ID)
      client?.connect()
    }.onFailure {
      logger.error("Failed to connect to Discord RPC.", it)
    }
  }

  private fun disconnectIpc() {
    val client = client

    if (client == null || client.status != PipeStatus.CONNECTED) {
      return
    }

    client.close()
  }

  private val states = listOf(
    "Arguing With Jerry",
    "Selling Dirt",
    "Not Macroing (Trust)",
    "Fell Into the Void",
    "Waiting for Diana",
    "Mining Cobble",
    "Talking to Bank Guard",
    "Broke Again",
    "AFK But Not Really",
    "Forgot Arrows",
    "Reorganizing Chests",
    "Selling My Minions",
    "Questioning Life",
    "Lagging in the Hub",
    "Flexing Nonexistent Necron",
    "Staring at Minions",
    "Lost 10M to Taxes",
    "Fishing Seaweed",
    "Mining Dirt Passionately",
    "Touching Grass (Rare)"
  )

}
