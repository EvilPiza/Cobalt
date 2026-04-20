package org.cobalt.util

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import org.cobalt.Cobalt

object WebUtils {

  /**
   * Opens an InputStream for the given URL using an HTTP GET request.
   *
   * The caller is responsible for closing the returned stream.
   *
   * @param url target URL
   * @param timeout connection and read timeout in milliseconds
   * @param cache whether URLConnection caching is enabled
   * @return [InputStream] of the HTTP response body
   */
  @JvmStatic
  fun getInputStream(url: String, timeout: Int = 5000, cache: Boolean = true): InputStream {
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
