package com.inf1nlty.togglezoom.mixin.client;

import com.inf1nlty.togglezoom.util.KeyBindings;
import com.inf1nlty.togglezoom.util.ZoomStateAccessor;
import net.minecraft.src.EntityRenderer;
import net.minecraft.src.GameSettings;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow public GameSettings gameSettings;

    @Shadow public GuiScreen currentScreen;

    @Shadow public EntityRenderer entityRenderer;

    @Unique
    private boolean zoom$holdFovApplied;

    @Unique
    private float zoom$originalFov;

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I", remap = false))
    private int nmBlockHotbarScrollWhenZoom() {
        ZoomStateAccessor accessor = (ZoomStateAccessor)this.entityRenderer;

        if (accessor.zoom$isToggleZoomActive() && accessor.zoom$isToggleZoomKeyHeld()) {
            return 0;
        }

        return Mouse.getEventDWheel();
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityRenderer;updateCameraAndRender(F)V"))
    private void zoom$applyHoldFov(CallbackInfo ci) {
        if (Keyboard.isKeyDown(KeyBindings.ZoomHold.keyCode) && this.currentScreen == null) {
            this.zoom$originalFov = this.gameSettings.fovSetting;
            this.gameSettings.fovSetting = this.zoom$originalFov > 1.0F ? 22.0F : -1.2F;
            this.zoom$holdFovApplied = true;
        }
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityRenderer;updateCameraAndRender(F)V", shift = At.Shift.AFTER))
    private void zoom$restoreHoldFov(CallbackInfo ci) {
        if (this.zoom$holdFovApplied) {
            this.gameSettings.fovSetting = this.zoom$originalFov;
            this.zoom$holdFovApplied = false;
        }
    }
}
