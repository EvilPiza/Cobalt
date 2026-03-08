package org.cobalt.mixin.opengl;

import com.mojang.blaze3d.opengl.GlStateManager;
import org.cobalt.util.ui.nvg.NanoVGImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlStateManager.class)
public class GlStateManagerMixin {

  @Inject(method = "_bindTexture", at = @At("HEAD"), remap = false)
  private static void onBindTexture(int texture, CallbackInfo callbackInfo) {
    NanoVGImpl.prevBoundTexture = texture;
  }

}
