package org.cobalt.util

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import org.cobalt.Cobalt

object WebUtils {

  @JvmStatic
  fun setupConnection(url: String, timeout: Int = 5000, cache: Boolean = true): InputStream {
    val connection = (URI(url).toURL().openConnection() as HttpURLConnection).apply {
      requestMethod = "GET"
      useCaches = cache
      connectTimeout = timeout
      readTimeout = timeout
      setRequestProperty("User-Agent", Cobalt.MOD_NAME)
    }

    return connection.inputStream
  }

}
