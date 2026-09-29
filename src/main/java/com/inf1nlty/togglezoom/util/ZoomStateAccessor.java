package com.inf1nlty.togglezoom.util;

public interface ZoomStateAccessor {

    boolean zoom$isToggleZoomActive();

    boolean zoom$isToggleZoomKeyHeld();

    double zoom$getTargetZoom();
}
