package org.cobalt.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import org.cobalt.event.EventBus;
import org.cobalt.event.impl.RenderContext;
import org.cobalt.event.impl.WorldEvent;
import org.joml.Matrix4f;
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
  private final RenderContext context = new RenderContext();

  @Shadow
  @Final
  private RenderBuffers renderBuffers;

  @Inject(method = "renderLevel", at = @At("HEAD"))
  private void render(GraphicsResourceAllocator allocator, DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
    context.setConsumers(renderBuffers.bufferSource());
    context.setCamera(camera);

    WorldEvent.RenderStart event = new WorldEvent.RenderStart(context);
    EventBus.post(event);
  }


  @Inject(method = "method_62214", at = @At("RETURN"))
  private void postRender(GpuBufferSlice gpuBufferSlice, LevelRenderState worldRenderState, ProfilerFiller profiler, Matrix4f matrix4f, ResourceHandle handle, ResourceHandle handle2, boolean bl, Frustum frustum, ResourceHandle handle3, ResourceHandle handle4, CallbackInfo ci) {
    context.setFrustum(frustum);

    WorldEvent.RenderLast event = new WorldEvent.RenderLast(context);
    EventBus.post(event);
  }

  @ModifyExpressionValue(method = "method_62214", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
  private PoseStack setInternalStack(PoseStack original) {
    context.setMatrixStack(original);
    return original;
  }

}
