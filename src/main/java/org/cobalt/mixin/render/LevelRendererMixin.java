package org.cobalt.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.cobalt.event.EventBus;
import org.cobalt.event.impl.RenderContext;
import org.cobalt.event.impl.WorldEvent;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

  @Unique
  private final RenderContext cobalt$context = new RenderContext();

  @Shadow
  @Final
  private LevelRenderState levelRenderState;

  @Shadow
  @Final
  private RenderBuffers renderBuffers;

  @Inject(method = "renderLevel", at = @At("HEAD"))
  private void render(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker, boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix, GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky, ChunkSectionsToRender chunkSectionsToRender, CallbackInfo ci) {
    cobalt$context.setBufferSource(renderBuffers.bufferSource());
    cobalt$context.setFrustum(cameraState.cullFrustum);
    cobalt$context.setDeltaTracker(deltaTracker);

    WorldEvent.RenderStart event = new WorldEvent.RenderStart(cobalt$context);
    EventBus.post(event);
  }

  @ModifyExpressionValue(method = "lambda$addMainPass$0", at = @At(value = "NEW", target = "Lcom/mojang/blaze3d/vertex/PoseStack;"))
  private PoseStack onCreatePoseStack(PoseStack poseStack) {
    cobalt$context.setPoseStack(poseStack);
    return poseStack;
  }

  @Inject(method = "lambda$addMainPass$0", at = @At("RETURN"))
  private void endMainRender(CallbackInfo ci) {
    cobalt$context.setFrustum(levelRenderState.cameraRenderState.cullFrustum);

    WorldEvent.RenderLast event = new WorldEvent.RenderLast(cobalt$context);
    EventBus.post(event);
  }

}
