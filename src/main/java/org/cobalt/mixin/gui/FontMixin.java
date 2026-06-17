package org.cobalt.mixin.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.cobalt.module.impl.misc.NickHider;
import org.cobalt.util.PlayerUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Font.class)
public class FontMixin {

  @ModifyVariable(
    method = "prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;",
    at = @At("HEAD"),
    argsOnly = true,
    name = "text"
  )
  private FormattedCharSequence modifyCharSequence(FormattedCharSequence text) {
    if (NickHider.INSTANCE.getEnabled()) {
      MutableComponent component = Component.literal(NickHider.INSTANCE.getNickname());
      return cobalt$replaceWordWithText(text, PlayerUtils.getIgn(), component);
    }

    return text;
  }

  @Unique
  private static FormattedCharSequence cobalt$replaceWordWithText(
    FormattedCharSequence text,
    String target,
    MutableComponent replacement
  ) {
    MutableComponent rebuilt = Component.empty();
    StringBuilder rawBuilder = new StringBuilder();

    List<Style> styles = new ArrayList<>();

    text.accept((index, style, codePoint) -> {
      rawBuilder.appendCodePoint(codePoint);
      styles.add(style);
      return true;
    });

    String raw = rawBuilder.toString();
    int targetLen = target.length();
    int i = 0;

    while (i < raw.length()) {
      int found = raw.indexOf(target, i);

      if (found == -1) {
        for (int j = i; j < raw.length(); j++) {
          rebuilt.append(Component.literal(new String(Character.toChars(raw.codePointAt(j)))).setStyle(styles.get(j)));
        }

        break;
      }

      for (int j = i; j < found; j++) {
        rebuilt.append(Component.literal(new String(Character.toChars(raw.codePointAt(j)))).setStyle(styles.get(j)));
      }

      rebuilt.append(replacement.copy().setStyle(styles.get(found)));
      i = found + targetLen;
    }

    return rebuilt.getVisualOrderText();
  }

}
