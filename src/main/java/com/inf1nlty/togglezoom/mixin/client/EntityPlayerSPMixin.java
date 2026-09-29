package com.inf1nlty.togglezoom.mixin.client;

import com.inf1nlty.togglezoom.util.ZoomStateAccessor;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin {

    @Shadow
    protected Minecraft mc;

    /**
     * Feed toggle zoom into the existing FOV multiplier pipeline so the renderer handles interpolation.
     */
    @Inject(method = "getFOVMultiplier", at = @At("RETURN"), cancellable = true)
    private void zoom$applyToggleZoomMultiplier(CallbackInfoReturnable<Float> cir) {
        ZoomStateAccessor accessor = (ZoomStateAccessor)this.mc.entityRenderer;
        double zoom = accessor.zoom$getTargetZoom();

        if (zoom == 1.0D) {
            return;
        }

        cir.setReturnValue(cir.getReturnValue() / (float)zoom);
    }
}
