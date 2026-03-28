package org.cobalt.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.cobalt.Cobalt;
import org.cobalt.event.EventBus;
import org.cobalt.event.impl.TickEvent;
import org.cobalt.util.skia.SkiaContext;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

  @Inject(method = "<init>", at = @At("TAIL"))
  private void registerSkia(GameConfig gameConfig, CallbackInfo ci) {
    int[] width = new int[1];
    int[] height = new int[1];

    long windowHandle = Minecraft.getInstance().getWindow().handle();
    GLFW.glfwGetFramebufferSize(windowHandle, width, height);

    int finalWidth = Math.max(width[0], 1);
    int finalHeight = Math.max(height[0], 1);

    SkiaContext.INSTANCE.initSkia(finalWidth, finalHeight);
  }

  @Inject(
    method = "renderFrame",
    at = @At(
      value = "INVOKE",
      target = "Lcom/mojang/blaze3d/systems/RenderSystem;flipFrame(Lcom/mojang/blaze3d/TracyFrameCapture;)V"
    ),
    require = 1
  )
  private void onBeforeFlipFrame(boolean advanceGameTime, CallbackInfo ci) {
    SkiaContext.INSTANCE.draw();
  }

  @Inject(method = "tick", at = @At("HEAD"))
  private void onStartTick(CallbackInfo callbackInfo) {
    TickEvent.Start event = new TickEvent.Start();
    EventBus.post(event);
  }

  @Inject(method = "tick", at = @At("RETURN"))
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
