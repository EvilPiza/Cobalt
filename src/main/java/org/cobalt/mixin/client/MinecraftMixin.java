package org.cobalt.mixin.client;

import net.minecraft.client.Minecraft;
import org.cobalt.Cobalt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Minecraft.class)
public class MinecraftMixin {

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
