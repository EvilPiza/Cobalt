package org.cobalt.mixin.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.cobalt.event.EventBus;
import org.cobalt.event.impl.HudEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public class HudMixin {

  @Inject(method = "extractRenderState", at = @At("TAIL"))
  public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
    HudEvent event = new HudEvent(graphics, deltaTracker);
    EventBus.post(event);
  }

}
