package org.cobalt.dsl

inline val Int.red
  get() = this shr 16 and 0xFF

inline val Int.green
  get() = this shr 8 and 0xFF

inline val Int.blue
  get() = this and 0xFF

inline val Int.alpha
  get() = this shr 24 and 0xFF
