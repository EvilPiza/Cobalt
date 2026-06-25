package org.cobalt.mixin.renderer;

import net.minecraft.client.gui.render.GuiRenderer;
import org.cobalt.util.skia.SkiaCompositor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {

  @Inject(method = "draw", at = @At("HEAD"))
  private void render(CallbackInfo ci) {
    SkiaCompositor.composite();
  }

}
