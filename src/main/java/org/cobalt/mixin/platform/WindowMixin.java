package org.cobalt.mixin.platform;

import com.mojang.blaze3d.platform.Window;
import org.cobalt.util.skia.SkiaContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {

  @Inject(method = "onFramebufferResize", at = @At("RETURN"))
  private void onFramebufferResize(long window, int width, int height, CallbackInfo ci) {
    int finalWidth = Math.max(width, 1);
    int finalHeight = Math.max(height, 1);

    SkiaContext.INSTANCE.initSkia(finalWidth, finalHeight);
  }

}
