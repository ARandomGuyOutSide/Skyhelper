package org.polyfrost.skyhelper.hud;

import cc.polyfrost.oneconfig.hud.SingleTextHud;
import org.polyfrost.skyhelper.config.SkyHelperConfig;

/**
 * An example OneConfig HUD that is started in the config and displays text.
 *
 * @see SkyHelperConfig#hud
 */
public class SkyHelperHud extends SingleTextHud {
    public SkyHelperHud() {
        super("Skyhelper", false);
    }

    @Override
    public String getText(boolean example) {
        return "This is just beta right now just disable this";
    }
}
