package org.cobalt.util.skia.helper

import org.cobalt.util.ResourceUtils

data class SkiaFont(val location: String) {

  val bytes: ByteArray by lazy {
    ResourceUtils.read(location)
  }

}
