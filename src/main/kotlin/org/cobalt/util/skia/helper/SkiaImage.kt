package org.cobalt.util.skia.helper

import org.cobalt.util.ResourceUtils

class SkiaImage(
  val location: String,
  val isSvg: Boolean = location.endsWith(".svg", ignoreCase = true),
) {

  val bytes: ByteArray by lazy {
    ResourceUtils.read(location)
  }

}
