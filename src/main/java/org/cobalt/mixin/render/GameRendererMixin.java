package org.cobalt.mixin.render;

import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.cobalt.Cobalt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

  @Unique
  private static final CubeMap cobalt$cubeMap = new CubeMap(ResourceLocation.fromNamespaceAndPath(Cobalt.MOD_NAME.toLowerCase(), "gui/panorama/panorama"));

  @Redirect(
    method = "<init>",
    at = @At(
      value = "NEW",
      target = "(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/CubeMap;"
    )
  )
  private CubeMap replaceCubeMap(ResourceLocation id) {
    return cobalt$cubeMap;
  }

  @Inject(
    method = "close",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/renderer/CubeMap;close()V"
    )
  )
  private void closeCubeMap(CallbackInfo ci) {
    cobalt$cubeMap.close();
  }

}
