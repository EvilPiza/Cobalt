package org.cobalt.mixin.client;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.cobalt.event.EventBus;
import org.cobalt.event.impl.MouseEvent;
import org.cobalt.util.MouseUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

  @Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
  private void onMouseButton(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo ci) {
    MouseEvent event = cobalt$createMouseEvent(rawButtonInfo.button(), action);

    if (event == null) {
      return;
    }

    EventBus.post(event);

    if (event.isCancelled()) {
      ci.cancel();
    }
  }

  @Unique
  private MouseEvent cobalt$createMouseEvent(int button, int action) {
    MouseEvent.Button mappedButton = switch (button) {
      case 0 -> MouseEvent.Button.LEFT;
      case 1 -> MouseEvent.Button.RIGHT;
      case 2 -> MouseEvent.Button.MIDDLE;
      default -> null;
    };

    if (mappedButton == null) {
      return null;
    }

    MouseEvent.Action mappedAction = switch (action) {
      case 1 -> MouseEvent.Action.PRESS;
      case 0 -> MouseEvent.Action.RELEASE;
      default -> null;
    };

    if (mappedAction == null) {
      return null;
    }

    return new MouseEvent(mappedButton, mappedAction);
  }

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
