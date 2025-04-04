package org.polyfrost.example;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import org.polyfrost.example.command.BlockESPCommand;
import org.polyfrost.example.command.HomeCommand;
import org.polyfrost.example.command.MacroStatusCommand;
import org.polyfrost.example.config.TestConfig;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.polyfrost.example.mining.BlockESP;
import org.polyfrost.example.mining.ScanBlocks;
import org.polyfrost.example.util.Chatter;

/**
 * The entrypoint of the Example Mod that initializes it.
 *
 * @see Mod
 * @see InitializationEvent
 *
 */

@Mod(modid = MainController.MODID, name = MainController.NAME, version = MainController.VERSION)
public class MainController {

    // Sets the variables from `gradle.properties`. See the `blossom` config in `build.gradle.kts`.
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    @Mod.Instance(MODID)
    public static MainController INSTANCE; // Adds the instance of the mod, so we can access other variables.
    public static TestConfig config;

    // Register the config and commands.
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new TestConfig();
        ClientCommandHandler.instance.registerCommand(new HomeCommand());
        ClientCommandHandler.instance.registerCommand(new BlockESPCommand());
        MinecraftForge.EVENT_BUS.register(new BlockESP());
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ScanBlocks());
        ClientCommandHandler.instance.registerCommand(new MacroStatusCommand());
        MinecraftForge.EVENT_BUS.register(new MacroController());
    }

    public static void onMacroDisable()
    {
        Chatter.sendChatLessageToUser("Macro status chanced to §cdisabled§r");
    }
}
