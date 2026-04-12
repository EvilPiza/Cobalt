package org.cobalt.util

import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.world.phys.AABB
import org.cobalt.mixin.render.FrustumInvoker
import org.joml.FrustumIntersection

/** Utilities for frustum visibility testing used by rendering helpers. */
object FrustumUtils {

  /** Check whether an AABB is inside or intersects the provided frustum. */
  @JvmStatic
  fun isVisible(frustum: Frustum, box: AABB): Boolean {
    return isVisible(frustum, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)
  }

  /** Check whether the specified axis-aligned cube bounds are visible in the frustum.
   *
   * @return true when the bounds are inside or intersect the frustum
   */
  @JvmStatic
  fun isVisible(
    frustum: Frustum,
    minX: Double,
    minY: Double,
    minZ: Double,
    maxX: Double,
    maxY: Double,
    maxZ: Double,
  ): Boolean {
    val result = (frustum as FrustumInvoker).invokeCubeInFrustum(minX, minY, minZ, maxX, maxY, maxZ)
    return result == FrustumIntersection.INSIDE || result == FrustumIntersection.INTERSECT
  }

}
