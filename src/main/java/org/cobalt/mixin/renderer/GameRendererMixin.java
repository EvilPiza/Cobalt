package org.cobalt.mixin.renderer;

import net.minecraft.client.renderer.GameRenderer;
import org.cobalt.util.skia.SkiaCompositor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

  @Inject(method = "close", at = @At("TAIL"))
  private void close(CallbackInfo ci) {
    SkiaCompositor.getGlSurface().close();
  }

}
