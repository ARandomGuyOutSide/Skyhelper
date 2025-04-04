package org.polyfrost.example.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class Chatter {

    static Minecraft mc = Minecraft.getMinecraft();

    public static void sendChatLessageToUser(String message)
    {
        mc.thePlayer.addChatMessage(new ChatComponentText("[§cSkyblockHelper§r] " + message));

        /*
        Colors:
        c = red
        0 = black
        a = green
         */
    }
}
