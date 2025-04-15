package org.polyfrost.skyhelper.player;

import cc.polyfrost.oneconfig.events.event.WorldLoadEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.polyfrost.skyhelper.MainController;
import org.polyfrost.skyhelper.util.Chatter;
import org.polyfrost.skyhelper.util.Timer;

public class Failsaves {

    private Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event)
    {
        if(MainController.macroEnabled)
        {
            if (mc.thePlayer == null || mc.theWorld == null)
                return;

            MainController.macroEnabled = false;
            MainController.disableMacro();

            Chatter.sendChatMessageToUser("Macro was disabled do to world change");
        }

    }
}
