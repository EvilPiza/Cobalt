package org.cobalt.mixin.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.cobalt.ui.screen.MainMenuScreen;
import org.cobalt.util.helper.TickScheduler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

  @Inject(method = "added", at = @At("HEAD"), cancellable = true)
  public void added(CallbackInfo callbackInfo) {
    Minecraft minecraft = Minecraft.getInstance();
    TickScheduler.schedule(1L, () -> minecraft.setScreen(MainMenuScreen.INSTANCE));
    callbackInfo.cancel();
  }

}
