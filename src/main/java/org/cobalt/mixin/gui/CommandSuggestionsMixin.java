package org.cobalt.mixin.gui;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.commands.SharedSuggestionProvider;
import org.cobalt.command.CommandManager;
import org.cobalt.util.ChatUtils;
import org.cobalt.util.MessageType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandSuggestions.class)
public abstract class CommandSuggestionsMixin {

  @Shadow @Final
  private EditBox input;

  @Shadow
  private CompletableFuture<Suggestions> pendingSuggestions;

  @Shadow
  private ParseResults<SharedSuggestionProvider> currentParse;

  @Shadow
  private CommandSuggestions.SuggestionsList suggestions;

  @Shadow
  public abstract void showSuggestions(boolean immediateNarration);

  @Inject(method = "updateCommandInfo", at = @At("HEAD"), cancellable = true)
  private void updateCommandInfo(CallbackInfo ci) {
    String text = this.input.getValue();

    if (!text.startsWith(CommandManager.getPrefix())) return;
    ci.cancel();

    Suggestions customSuggestions = CommandManager.getSuggestions(text);
    this.pendingSuggestions = CompletableFuture.completedFuture(customSuggestions);

    this.showSuggestions(true);
  }
}
