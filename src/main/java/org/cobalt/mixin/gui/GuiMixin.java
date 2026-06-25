package org.cobalt.mixin.gui;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.cobalt.event.EventBus;
import org.cobalt.event.impl.RenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

  @Inject(
    method = "extractRenderState",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V",
      ordinal = 3
    )
  )
  private void beforeFinalPop(
    DeltaTracker deltaTracker,
    boolean shouldRenderLevel,
    boolean resourcesLoaded,
    CallbackInfo ci,
    @Local(name = "graphics") GuiGraphicsExtractor graphics
  ) {
    RenderEvent.Notification event = new RenderEvent.Notification(graphics, deltaTracker);
    EventBus.post(event);
  }
}
