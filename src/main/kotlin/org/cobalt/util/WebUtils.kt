package org.cobalt.util

import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import org.cobalt.Cobalt

/** Small HTTP helpers for fetching resources. */
object WebUtils {

  /** Open an InputStream for the given URL using a simple GET request.
   *
   * @param url the URL to fetch
   * @param timeout connect/read timeout in milliseconds
   * @param cache whether to allow URLConnection caching
   * @return an InputStream for reading the response body; caller is responsible for closing it
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
