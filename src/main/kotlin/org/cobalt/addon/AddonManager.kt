package org.cobalt.addon

import net.fabricmc.loader.api.FabricLoader

object AddonManager {

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
      } catch (e: Throwable) {
        throw RuntimeException("Failed to initialize ${metadata.name} (${metadata.id})", e)
      }

      addons.add(addonMetadata to addonInstance)
    }

    addons.forEach { addon ->
      addon.second.onLoad()
    }
  }

}
