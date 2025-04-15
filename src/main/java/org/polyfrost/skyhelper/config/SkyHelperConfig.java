package org.polyfrost.skyhelper.config;

import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.migration.VigilanceName;
import cc.polyfrost.oneconfig.libs.universal.UKeyboard;
import org.polyfrost.skyhelper.MainController;
import org.polyfrost.skyhelper.hud.SkyHelperHud;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;


public class SkyHelperConfig extends Config {

    private static final transient String General = "General";
    private static final transient String Mining = "Mining";
    private static final transient String QOL = "QOL";
    private static final transient String Hud = "Hud";

    // GENERAL

    @VigilanceName(name = "General", category = General, subcategory = "Macro settings")
    @KeyBind(name = "Macro Keybinding", category = General, subcategory = "Macro settings", size = 2)
    private OneKeyBind macroKeyBinding = new OneKeyBind(UKeyboard.KEY_NONE);

    @Dropdown(name = "Macro Type", category = General, subcategory = "Macro settings",
            options = { "Mithril macro", "Rest comming soon" })
    private int macroType = 0;

    @Switch(name = "Debug mode", category = General, subcategory = "Macro settings")
    private boolean debugMode = false;

    @Switch(name = "Use Mining speed boost", category = General, subcategory = "Macro settings")
    private boolean useSpeedBoost = true;

    @Switch(name = "Ungrab Mouse", category = General, subcategory = "Macro settings")
    private boolean ungrabMouse = true;

    // MINING

    @VigilanceName(name = "Mining", category = Mining, subcategory = "Mithril Macro")

    @Switch(name = "Mine Titanium", category = Mining, subcategory = "Mithril Macro")
    private boolean mineTitanium = true;

    @Switch(name = "Sneak while Mining", category = Mining, subcategory = "Mithril Macro")
    private boolean sneakWhileMining = true;

    @Switch(name = "Move while Mining", category = Mining, subcategory = "Mithril Macro")
    private boolean moveWhileMining = true;

    @Switch(name = "Show ignored blocks", category = Mining, subcategory = "Mithril Macro")
    private boolean showIgnoredBlocks = true;

    @Slider(name = "Rotation speed", category = Mining, subcategory = "Mithril Macro",
    min = 1, max = 10)
    private int rotationSpeed = 1;

    @Slider(name = "Wait for Seconds after block was mined (ms)", category = Mining, subcategory = "Mithril Macro",
            min = 10, max = 100)
    private int waitForSeconds = 35;

    @Slider(name = "Mining delay variation (%)", category = Mining, subcategory = "Mithril Macro",
            min = 10, max = 100)
    private int miningDelayVariation = 20;

    @Slider(name = "Max Mining time", category = Mining, subcategory = "Mithril Macro",
            min = 30, max = 1000)
    private int maxMiningTime = 20;

    // Hud

    @VigilanceName(name = "Hud", category = Hud, subcategory = "Hud")

    @HUD(

            name = "Example HUD"
    )
    public SkyHelperHud hud = new SkyHelperHud();

    public OneKeyBind getMacroKeyBinding()
    {
        return macroKeyBinding;
    }

    public boolean isMineTitanium()
    {
        return mineTitanium;
    }

    public boolean isMoveWhileMining()
    {
        return moveWhileMining;
    }

    public int getRotationSpeed()
    {
        return rotationSpeed;
    }

    public int getWaitForSeconds()
    {
        return waitForSeconds;
    }

    public boolean isSneakWhileMining()
    {
        return sneakWhileMining;
    }

    public int getMacroType()
    {
        return macroType;
    }

    public boolean isDebugMode()
    {
        return debugMode;
    }

    public boolean isUseSpeedBoost()
    {
        return useSpeedBoost;
    }

    public int getMiningDelayVariation()
    {
        return miningDelayVariation;
    }

    public boolean isUngrabMouse()
    {
        return ungrabMouse;
    }

    public boolean isShowIgnoredBlocks()
    {
        return showIgnoredBlocks;
    }

    public int getMaxMiningTime()
    {
        return maxMiningTime;
    }

    public SkyHelperConfig() {
        super(new Mod(MainController.NAME, ModType.UTIL_QOL), MainController.MODID + ".json");
        initialize();
    }
}

