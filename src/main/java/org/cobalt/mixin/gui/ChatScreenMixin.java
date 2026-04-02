package org.cobalt.mixin.gui;

import net.minecraft.client.gui.screens.ChatScreen;
import org.cobalt.event.EventBus;
import org.cobalt.event.impl.ChatSendEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

  @Inject(method = "handleChatInput", at = @At("HEAD"), cancellable = true)
  private void handleChatMessage(String msg, boolean addToRecent, CallbackInfo ci) {
    ChatSendEvent event = new ChatSendEvent(msg);
    EventBus.post(event);

    if (event.isCancelled()) {
      ci.cancel();
    }
  }


}
