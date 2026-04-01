package org.cobalt.mixin.gui;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.commands.SharedSuggestionProvider;
import org.cobalt.command.CommandManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandSuggestions.class)
public abstract class CommandSuggestionsMixin {

  @Shadow
  @Final
  private EditBox input;

  @Shadow
  private CompletableFuture<Suggestions> pendingSuggestions;

  @Shadow
  private ParseResults<SharedSuggestionProvider> currentParse;

  @Shadow
  private CommandSuggestions.SuggestionsList suggestions;

  @Inject(method = "updateCommandInfo", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/StringReader;canRead()Z", remap = false), cancellable = true)
  private void updateCommandInfo(CallbackInfo ci) {
    if (this.input.getValue().startsWith(CommandManager.getPrefix())) {
      this.pendingSuggestions = CommandManager.getPendingSuggestions(this.input.getValue(), this.input.getCursorPosition());

      this.pendingSuggestions.thenRun(() -> {
        if (suggestions == null) {
          this.showSuggestions(false);
        }
      });

      this.currentParse = null;
      ci.cancel();
    }
  }

  @Shadow
  public abstract void showSuggestions(boolean immediateNarration);

}
