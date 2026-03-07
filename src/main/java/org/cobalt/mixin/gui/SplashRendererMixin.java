package org.cobalt.mixin.gui;

import net.minecraft.client.gui.components.SplashRenderer;
import org.cobalt.Cobalt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SplashRenderer.class)
public class SplashRendererMixin {

  @ModifyArgs(
    method = "render",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V"
    )
  )
  private void modifySplash(Args args) {
    args.set(1, Cobalt.MOD_NAME + " on top!");
    args.set(4, 0xFF4F8CFF);
  }

}
