package org.cobalt.mixin.client;

import net.minecraft.client.MouseHandler;
import org.cobalt.util.MouseUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

  @Shadow
  private boolean mouseGrabbed;

  @Shadow
  public abstract void releaseMouse();

  @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
  private void onUpdateMouse(CallbackInfo callbackInfo) {
    if (MouseUtils.shouldBlockRotation()) {
      callbackInfo.cancel();
    }
  }

  @Inject(method = "isMouseGrabbed", at = @At("HEAD"), cancellable = true)
  private void onIsCursorLocked(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
    if (MouseUtils.isForceUngrabbed()) {
      if (this.mouseGrabbed) {
        this.releaseMouse();
      }

      callbackInfoReturnable.setReturnValue(false);
    }
  }

  @Inject(method = "grabMouse", at = @At("HEAD"), cancellable = true)
  private void onLockCursor(CallbackInfo callbackInfo) {
    if (MouseUtils.isForceUngrabbed()) {
      callbackInfo.cancel();
    }
  }

}
