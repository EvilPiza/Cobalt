/*
 * This file is part of https://github.com/Lyzev/Skija.
 *
 * Copyright (c) 2025. Lyzev
 *
 * Skija is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at your option) any later version.
 *
 * Skija is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Skija. If not, see <https://www.gnu.org/licenses/>.
 */

package org.cobalt.util.skia.gl

import java.util.*
import org.lwjgl.opengl.GL30.glGetIntegerv
import org.lwjgl.opengl.GL30.GL_MAJOR_VERSION
import org.lwjgl.opengl.GL30.GL_MINOR_VERSION

private const val GL_MAJOR_MULTIPLIER = 100
private const val GL_MINOR_MULTIPLIER = 10

/**
 * Utility that manages a stack of OpenGL state snapshots.
 *
 * Use [push] to capture the current GL state and [pop] to restore the most
 * recently captured state. The object's initializer reads the OpenGL major
 * and minor version and stores a computed integer representation for use by
 * created [State] instances.
 */
object States {

  private val glVersion: Int
  private val states = Stack<State>()

  /**
   * Capture the current GL state and push a snapshot onto the internal stack.
   *
   * A new [State] is created using the GL version detected at startup and
   * its [State.push] method is invoked to record the GL state.
   */
  fun push() {
    states += State(glVersion).push()
  }

  /**
   * Restore and remove the most recently pushed GL state snapshot.
   *
   * Throws an [IllegalArgumentException] if there is no saved state to
   * restore.
   */
  fun pop() {
    require(states.isNotEmpty()) { "No state to restore." }
    states.pop().pop()
  }

  init {
    val major = IntArray(1)
    val minor = IntArray(1)
    glGetIntegerv(GL_MAJOR_VERSION, major)
    glGetIntegerv(GL_MINOR_VERSION, minor)
    glVersion = major[0] * GL_MAJOR_MULTIPLIER + minor[0] * GL_MINOR_MULTIPLIER
  }

}
