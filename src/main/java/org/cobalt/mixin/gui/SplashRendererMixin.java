package org.cobalt.mixin.gui;

import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.cobalt.Cobalt;
import org.cobalt.ui.theme.ThemeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SplashRenderer.class)
public class SplashRendererMixin {

  @ModifyArgs(
    method = "extractRenderState",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/gui/ActiveTextCollector;accept(Lnet/minecraft/client/gui/TextAlignment;IILnet/minecraft/client/gui/ActiveTextCollector$Parameters;Lnet/minecraft/network/chat/Component;)V"
    )
  )
  private void modifySplash(Args args) {
    args.set(4, cobalt$createSplash());
  }

  @Unique
  private Component cobalt$createSplash() {
    int color = ThemeManager.getActiveTheme().getAccentPrimary();

    return Component.literal(Cobalt.MOD_NAME + " on top!")
      .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)));
  }

}
