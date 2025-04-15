package org.polyfrost.skyhelper;

import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.polyfrost.skyhelper.config.SkyHelperConfig;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.polyfrost.skyhelper.features.MouseUngrab;
import org.polyfrost.skyhelper.mining.BlockESP;
import org.polyfrost.skyhelper.mining.ScanBlocks;
import org.polyfrost.skyhelper.player.Failsaves;
import org.polyfrost.skyhelper.player.Player;
import org.polyfrost.skyhelper.util.Chatter;

import java.util.LinkedList;

/**
 * The entry point of the SkyHelper Mod that initializes the mod.
 *
 * @see Mod
 * @see InitializationEvent
 */
@Mod(modid = MainController.MODID, name = MainController.NAME, version = MainController.VERSION)
public class MainController {

    // Set from `gradle.properties`. See the `blossom` config in `build.gradle.kts`.
    public static final String MODID = "@ID@";
    public static final String NAME = "SkyHelper";
    public static final String VERSION = "0.1 beta";

    @Mod.Instance(MODID)
    public static MainController INSTANCE; // Allows access to other variables.

    public static SkyHelperConfig config;

    private static Minecraft mc = Minecraft.getMinecraft();
    public static boolean macroEnabled;

    private enum MiningState {
        ENABLED,
        DISABLED
    }

    private static MiningState miningState = MiningState.DISABLED;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new SkyHelperConfig();
        MinecraftForge.EVENT_BUS.register(new BlockESP());
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ScanBlocks());
        MinecraftForge.EVENT_BUS.register(new MacroController());
        MinecraftForge.EVENT_BUS.register(new Failsaves());
        MinecraftForge.EVENT_BUS.register(new Player());
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        OneKeyBind macroKey = config.getMacroKeyBinding();

        if (macroKey.isActive()) {
            if (miningState == MiningState.DISABLED) {
                enableMacro();
            } else {
                disableMacro();
            }
        }
    }

    public static void enableMacro() {
        switch (config.getMacroType()) {
            case 0:
                cleanupOnDisable();
                refreshSurroundingBlocks();
                MacroController.setIgnoreList(new LinkedList<>());
                MacroController.setMaxMiningTime(config.getMaxMiningTime());
                MacroController.setState(MacroController.MiningState.GET_MINING_TOOL);
                if (config.isUngrabMouse()) MouseUngrab.ungrabMouse();
                Chatter.sendChatMessageToUser("Mining macro has been §aenabled");
                miningState = MiningState.ENABLED;
                macroEnabled = true;
                break;

            case 1:
                Chatter.sendChatMessageToUser("Features §ccoming soon§r");
                break;
        }
    }

    private static void refreshSurroundingBlocks() {
        EntityPlayer player = mc.thePlayer;
        BlockPos playerPos = new BlockPos(player.posX, player.posY, player.posZ);
        int radius = 6;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    // Forces the client to request block updates from the server
                    mc.theWorld.markBlockForUpdate(pos);
                }
            }
        }
    }

    public static void disableMacro() {
        MacroController.setState(MacroController.MiningState.NONE);
        Chatter.sendChatMessageToUser("Mining macro has been §cdisabled");
        cleanupOnDisable();
        macroEnabled = false;
        miningState = MiningState.DISABLED;
    }

    private static void cleanupOnDisable() {
        BlockESP.setSingleBlockESP(null);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        if (config.isUngrabMouse()) MouseUngrab.regrabMouse();
    }
}
