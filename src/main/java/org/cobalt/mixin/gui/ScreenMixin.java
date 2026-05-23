package org.cobalt.mixin.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.cobalt.ui.screen.MainMenuScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.awt.Color;

@Mixin(Screen.class)
public class ScreenMixin {

  @Shadow
  @Final
  protected Minecraft minecraft;

  @Inject(method = "extractPanorama", at = @At("HEAD"), cancellable = true)
  public void extractPanorama(GuiGraphicsExtractor graphics, float a, CallbackInfo callbackInfo) {
    if (!(minecraft.screen instanceof MainMenuScreen)) {
      graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), Color.BLACK.getRGB());
      callbackInfo.cancel();
    }
  }

}
