package org.cobalt.addon

data class AddonMetadata(
  val id: String,
  val name: String,
  val version: String,
  val entrypoints: List<String>,
  val mixins: List<String>,
)
