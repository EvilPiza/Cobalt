package org.cobalt.mixin.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.cobalt.event.EventBus;
import org.cobalt.event.impl.HudEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

  @Inject(method = "extractRenderState", at = @At("TAIL"))
  public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo callbackInfo) {
    HudEvent event = new HudEvent(graphics, deltaTracker);
    EventBus.post(event);
  }

}
