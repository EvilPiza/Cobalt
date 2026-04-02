package org.cobalt.mixin.gui;

import net.minecraft.client.multiplayer.ClientPacketListener;
import org.cobalt.command.CommandManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

  @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
  public void sendChatMessage(String content, CallbackInfo ci) {
    if (content.startsWith(String.valueOf(CommandManager.getPrefix()))) {
      CommandManager.handleCommandExecution(content);
      ci.cancel();
    }
  }

}
