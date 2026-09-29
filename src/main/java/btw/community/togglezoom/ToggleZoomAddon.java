package btw.community.togglezoom;

import api.AddonHandler;
import api.BTWAddon;

public class ToggleZoomAddon extends BTWAddon {

    @Override
    public void initialize() {
        AddonHandler.logMessage(getName() + " v" + getVersionString() + " Initializing...");
    }
}
