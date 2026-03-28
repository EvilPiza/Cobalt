package org.cobalt.mixin.gui;

import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.cobalt.Cobalt;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {

  @Unique
  private static final CubeMap cobalt$cubeMap = new CubeMap(Identifier.fromNamespaceAndPath(Cobalt.MOD_NAME.toLowerCase(), "panorama/panorama"));

  @Redirect(
    method = "render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
    at = @At(
      value = "FIELD",
      target = "Lnet/minecraft/client/gui/render/GuiRenderer;cubeMap:Lnet/minecraft/client/renderer/CubeMap;",
      opcode = Opcodes.GETFIELD
    )
  )
  private CubeMap redirectCubeMap(GuiRenderer instance) {
    return cobalt$cubeMap;
  }

  @Inject(method = "registerPanoramaTextures", at = @At("HEAD"))
  private void registerCustomCubeMapTextures(TextureManager textureManager, CallbackInfo ci) {
    cobalt$cubeMap.registerTextures(textureManager);
  }

  @Inject(method = "close", at = @At("RETURN"))
  private void onClose(CallbackInfo ci) {
    cobalt$cubeMap.close();
  }

}
