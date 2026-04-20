package org.cobalt.util

import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.world.phys.AABB
import org.cobalt.mixin.render.FrustumInvoker
import org.joml.FrustumIntersection

/**
 * Utility functions for frustum-based visibility checks.
 */
object FrustumUtils {

  /**
   * Checks whether an axis-aligned bounding box is visible within the given frustum.
   *
   * @param frustum the view frustum used for visibility testing
   * @param box the bounding box to test
   * @return true if the box is inside or intersects the frustum, false otherwise
   */
  @JvmStatic
  fun isVisible(frustum: Frustum, box: AABB): Boolean {
    return isVisible(frustum, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)
  }

  /**
   * Checks whether an axis-aligned bounding box is visible within the given frustum.
   *
   * @param frustum the view frustum used for visibility testing
   * @param minX minimum X coordinate of the box
   * @param minY minimum Y coordinate of the box
   * @param minZ minimum Z coordinate of the box
   * @param maxX maximum X coordinate of the box
   * @param maxY maximum Y coordinate of the box
   * @param maxZ maximum Z coordinate of the box
   * @return true if the box is inside or intersects the frustum, false otherwise
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
