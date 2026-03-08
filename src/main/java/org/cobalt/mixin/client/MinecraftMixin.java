package org.cobalt.mixin.client;

import net.minecraft.client.Minecraft;
import org.cobalt.Cobalt;
import org.cobalt.event.EventBus;
import org.cobalt.event.impl.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

  @Inject(method = "<init>", at = @At("RETURN"))
  private static void onInit(CallbackInfo ci) {
    Cobalt.getRenderer().init();
  }

  @Inject(at = @At("HEAD"), method = "tick")
  private void onStartTick(CallbackInfo callbackInfo) {
    TickEvent.Start event = new TickEvent.Start();
    EventBus.post(event);
  }

  @Inject(at = @At("RETURN"), method = "tick")
  private void onEndTick(CallbackInfo callbackInfo) {
    TickEvent.End event = new TickEvent.End();
    EventBus.post(event);
  }

  @ModifyArg(
    method = "updateTitle",
    at = @At(
      value = "INVOKE",
      target = "Lcom/mojang/blaze3d/platform/Window;setTitle(Ljava/lang/String;)V"
    ),
    index = 0
  )
  private String modifyTitle(String oldTitle) {
    return Cobalt.MOD_NAME + " " + Cobalt.MOD_VERSION;
  }

}
