/*
 * This file is part of https://github.com/Lyzev/Skija.
 *
 * Copyright (c) 2024-2025. Lyzev
 *
 * Skija is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at your option) any later version.
 *
 * Skija is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Skija. If not, see <https://www.gnu.org/licenses/>.
 */

package org.cobalt.mixin.platform;

import com.mojang.blaze3d.platform.Window;
import org.cobalt.util.skia.SkiaContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {

  @Inject(method = "onFramebufferResize", at = @At("RETURN"))
  private void onFramebufferResize(long handle, int newWidth, int newHeight, CallbackInfo ci) {
    int finalWidth = Math.max(newWidth, 1);
    int finalHeight = Math.max(newHeight, 1);

    SkiaContext.INSTANCE.initSkia$cobalt(finalWidth, finalHeight);
  }

}
