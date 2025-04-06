package org.polyfrost.skyhelper.hud;

import cc.polyfrost.oneconfig.hud.SingleTextHud;
import org.polyfrost.skyhelper.config.TestConfig;

/**
 * An example OneConfig HUD that is started in the config and displays text.
 *
 * @see TestConfig#hud
 */
public class TestHud extends SingleTextHud {
    public TestHud() {
        super("Test", true);
    }

    @Override
    public String getText(boolean example) {
        return "I'm an example HUD";
    }
}
