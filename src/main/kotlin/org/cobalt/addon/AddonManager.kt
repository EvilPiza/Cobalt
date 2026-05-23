package org.cobalt.addon

import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory

object AddonManager {

  private val logger = LoggerFactory.getLogger(this::class.java)

  @JvmStatic
  val addons = mutableListOf<Pair<AddonMetadata, Addon>>()

  @JvmStatic
  internal fun loadAddons() {
    val entries = FabricLoader.getInstance().getEntrypointContainers("cobalt", Addon::class.java)

    for (entry in entries) {
      val metadata = entry.provider.metadata

      val addonMetadata = AddonMetadata(
        id = metadata.id,
        name = metadata.name,
        version = metadata.version?.toString() ?: "unknown"
      )

      val addonInstance: Addon = try {
        entry.entrypoint
      } catch (exception: Exception) {
        logger.error("Failed to initialize ${metadata.name} (${metadata.id})", exception)
        continue
      }

      addons.add(addonMetadata to addonInstance)
    }

    addons.forEach { addon ->
      addon.second.onLoad()
    }
  }

}
