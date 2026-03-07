package org.cobalt.mixin.gui;

import net.minecraft.client.gui.components.SplashRenderer;
import org.cobalt.Cobalt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashRenderer.class)
public class SplashRendererMixin {

  @Final
  @Mutable
  @Shadow
  private String splash;

  @Inject(method = "<init>", at = @At("TAIL"))
  private void changeSplash(String string, CallbackInfo ci) {
    this.splash = Cobalt.MOD_NAME + " on top!";
  }

}
