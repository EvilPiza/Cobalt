package org.cobalt.mixin.render;

import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Frustum.class)
public interface FrustumInvoker {

  /**
   * Tests whether an axis-aligned bounding box is inside, intersecting, or outside the view frustum.
   *
   * @param minX the x-coordinate of the minimum corner
   * @param minY the y-coordinate of the minimum corner
   * @param minZ the z-coordinate of the minimum corner
   * @param maxX the x-coordinate of the maximum corner
   * @param maxY the y-coordinate of the maximum corner
   * @param maxZ the z-coordinate of the maximum corner
   * @return {@link org.joml.FrustumIntersection#INSIDE},
   * {@link org.joml.FrustumIntersection#INTERSECT},
   * or a plane index if outside the frustum
   */
  @Invoker
  int invokeCubeInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);

}
