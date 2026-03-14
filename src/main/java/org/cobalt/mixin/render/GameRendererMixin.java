package org.cobalt.mixin.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.cobalt.Cobalt;
import org.cobalt.event.EventBus;
import org.cobalt.event.impl.UIEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

  @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;incrementFrameNumber()V", shift = At.Shift.AFTER))
  public void hookRender(DeltaTracker deltaTracker, boolean tick, CallbackInfo callbackInfo) {
    UIEvent.NanoVG event = new UIEvent.NanoVG();
    EventBus.post(event);
  }

  @Unique
  private static final CubeMap cobalt$cubeMap = new CubeMap(ResourceLocation.fromNamespaceAndPath(Cobalt.MOD_NAME.toLowerCase(), "gui/panorama/panorama"));

  @Redirect(
    method = "<init>",
    at = @At(
      value = "NEW",
      target = "(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/CubeMap;"
    )
  )
  private CubeMap replaceCubeMap(ResourceLocation id) {
    return cobalt$cubeMap;
  }

  @Inject(
    method = "close",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/client/renderer/CubeMap;close()V"
    )
  )
  private void closeCubeMap(CallbackInfo ci) {
    cobalt$cubeMap.close();
  }

}
