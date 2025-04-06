package org.polyfrost.skyhelper;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.polyfrost.skyhelper.mining.BlockESP;
import org.polyfrost.skyhelper.mining.ScanBlocks;
import org.polyfrost.skyhelper.player.Player;
import org.polyfrost.skyhelper.util.Chatter;
import org.polyfrost.skyhelper.util.InventoryStuff;
import org.polyfrost.skyhelper.util.Timer;

public class MacroController {
    private static int delayTicks = 0;

    public enum SetupState {
        WARP_TO_FORGE,
        GET_MINING_TOOL,
        OPEN_SKYBLOCK_MENU,
        NONE
    }

    public enum MiningState {
        SEARCH_FOR_BLOCK,
        NONE
    }


    private static SetupState setupState = SetupState.NONE;
    private static MiningState miningState = MiningState.NONE;

    private static Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.START) return;

        if(delayTicks > 0)
        {
            delayTicks--;
            return;
        }

        switch (setupState)
        {
            case WARP_TO_FORGE:
                Chatter.sendChatMessageToUser("Macro status changed to §aenabled§r");
                Player.warpToForge();
                setupState = SetupState.GET_MINING_TOOL;
                mc.thePlayer.addChatComponentMessage(new ChatComponentText("Waiting three seconds"));
                delayTicks = Timer.secondsToTicks(3);
                break;
            case GET_MINING_TOOL:
                Player.getMiningTool();
                setupState = SetupState.OPEN_SKYBLOCK_MENU;
                delayTicks = Timer.secondsToTicks(1);
                break;
            case OPEN_SKYBLOCK_MENU:
                Player.openSkyblockMenu();
                delayTicks = Timer.secondsToTicks(1);
                InventoryStuff.clickOpenContainerSlot(44);
                setupState = SetupState.NONE;
                break;
        }

        switch (miningState)
        {
            case SEARCH_FOR_BLOCK:
                BlockESP.setBlockESPOfBlockWithPos(ScanBlocks.findClosestBlockToMouse());

                break;
        }

    }

    public static void setState(SetupState state) {
        setupState = state;
    }

    public static void setState(MiningState state)
    {
         miningState = state;
    }
}
