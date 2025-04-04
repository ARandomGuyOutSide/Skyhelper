package org.polyfrost.example;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.polyfrost.example.player.Player;
import org.polyfrost.example.util.Chatter;
import org.polyfrost.example.util.InventoryStuff;
import org.polyfrost.example.util.Timer;

public class MacroController {
    private static int delayTicks = 0;
    private static String state = "";

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

        switch (state)
        {
            case "warpToForge":
                Chatter.sendChatLessageToUser("Macro status changed to §aenabled§r");
                Player.warpToForge();
                state = "getMiningTool";
                mc.thePlayer.addChatComponentMessage(new ChatComponentText("Waiting three seconds"));
                delayTicks = Timer.secondsToTicks(3);
                break;
            case "getMiningTool":
                Player.getMiningTool();
                state = "openSkyblockMenu";
                delayTicks = Timer.secondsToTicks(1);
                break;
            case "openSkyblockMenu":
                Player.openSkyblockMenu();
                delayTicks = Timer.secondsToTicks(1);
                InventoryStuff.clickOpenContainerSlot(44);
                state = "";
                break;
        }
    }

    public static void setState(String state) {
        MacroController.state = state;
    }
}
