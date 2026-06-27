package org.cobalt.util.skia.surface

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.GpuTexture
import com.mojang.blaze3d.vulkan.VulkanDevice
import io.github.humbleui.skija.Canvas
import org.cobalt.mixin.mojang.GpuDeviceAccessor
import org.slf4j.LoggerFactory

interface SkiaSurface {

  fun render(
    width: Int,
    height: Int,
    texture: GpuTexture,
    draw: (Canvas) -> Unit,
  )

  fun close()

  companion object {

    fun getInstance(): SkiaSurface {
      val rawDevice = RenderSystem.getDevice()
      val backend = (rawDevice as GpuDeviceAccessor).getBackend()

      if (backend is VulkanDevice) {
        return VulkanSurface()
      }

      return GlSurface()
    }

  }

}
