package org.polyfrost.skyhelper.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.polyfrost.skyhelper.MainController;

public class Chatter {

    static Minecraft mc = Minecraft.getMinecraft();

    public static void sendChatMessageToUser(String message)
    {
        mc.thePlayer.addChatMessage(new ChatComponentText("[§cSkyblockHelper§r] " + message));

        /*
        Colors:
        c = red
        0 = black
        a = green
         */
    }

    public static void sendDebutChat(String message)
    {
        if(MainController.config.isDebugMode())
        {
            mc.thePlayer.addChatComponentMessage(new ChatComponentText(message));
        }
    }
}
