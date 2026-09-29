package com.inf1nlty.togglezoom.util;

import net.minecraft.src.KeyBinding;

/**
 * Holds custom key bindings.
 * Appended once to GameSettings via GameSettingsMixin.
 */
public final class KeyBindings {

    public static KeyBinding ZoomHold;
    public static KeyBinding ZoomToggle;

    private static boolean registered;

    /**
     * Returns true only the first time this is called (used to guard array expansion).
     */
    public static boolean markRegistered() {
        if (!registered) {
            registered = true;
            return true;
        }
        return false;
    }

    /**
     * All custom bindings to append.
     */
    public static KeyBinding[] all() {
        return new KeyBinding[]{ ZoomHold, ZoomToggle };
    }
}
