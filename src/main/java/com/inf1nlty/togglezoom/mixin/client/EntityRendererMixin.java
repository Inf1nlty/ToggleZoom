package com.inf1nlty.togglezoom.mixin.client;

import com.inf1nlty.togglezoom.util.KeyBindings;
import com.inf1nlty.togglezoom.util.ZoomStateAccessor;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin implements ZoomStateAccessor {

    @Unique
    private static final double zoom$defaultZoom = 4.0D;

    @Shadow
    private Minecraft mc;

    @Unique
    private boolean zoom$toggleActive;

    @Unique
    private boolean zoom$toggleKeyWasDown;

    @Unique
    private double zoom$targetZoom = 1.0D;

    @Override
    public boolean zoom$isToggleZoomActive() {
        return this.zoom$toggleActive;
    }

    @Override
    public boolean zoom$isToggleZoomKeyHeld() {
        return Keyboard.isKeyDown(KeyBindings.ZoomToggle.keyCode);
    }

    @Override
    public double zoom$getTargetZoom() {
        return this.zoom$toggleActive ? this.zoom$targetZoom : 1.0D;
    }

    @Inject(method = "updateCameraAndRender", at = @At("HEAD"))
    private void zoom$updateToggleState(float partialTicks, CallbackInfo ci) {
        boolean zoomKeyDown = Keyboard.isKeyDown(KeyBindings.ZoomToggle.keyCode);

        // toggle
        if (zoomKeyDown && !this.zoom$toggleKeyWasDown && this.mc.currentScreen == null) {
            this.zoom$toggleActive = !this.zoom$toggleActive;
            this.zoom$targetZoom = this.zoom$toggleActive ? zoom$defaultZoom : 1.0D;
        }

        this.zoom$toggleKeyWasDown = zoomKeyDown;

        // Allow scroll wheel to set zoom only in the frame just activated
        if (this.zoom$toggleActive && zoomKeyDown && this.mc.currentScreen == null) {
            int wheel = Mouse.getDWheel();

            if (wheel != 0) {
                double step = this.zoom$getScrollStep();
                this.zoom$targetZoom += wheel > 0 ? step : -step;
                this.zoom$targetZoom = Math.max(1.0D, Math.min(32.0D, this.zoom$targetZoom));
            }
        }
    }

    /**
     * Reduce mouse sensitivity in proportion to the active zoom level.
     */
    @ModifyConstant(method = "updateCameraAndRender", constant = @Constant(floatValue = 8.0F))
    private float zoom$scaleMouseSensitivity(float originalScale) {
        double zoom = this.zoom$getTargetZoom();

        if (zoom == 1.0D) {
            return originalScale;
        }

        return originalScale / (float)zoom;
    }

    /**
     * Preserve the original variable scroll increments across the supported zoom range.
     */
    @Unique
    private double zoom$getScrollStep() {
        if (this.zoom$targetZoom >= 12.0D) {
            return 2.0D;
        }

        if (this.zoom$targetZoom >= 8.0D) {
            return 1.5D;
        }

        if (this.zoom$targetZoom >= 4.0D) {
            return 1.0D;
        }

        if (this.zoom$targetZoom >= 1.5D) {
            return 0.25D;
        }

        return 0.1D;
    }
}
