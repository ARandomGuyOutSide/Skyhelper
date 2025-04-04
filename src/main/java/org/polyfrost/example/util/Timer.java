package org.polyfrost.example.util;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.*;

public class Timer {

    public static int secondsToTicks(int seconds)
    {
        Chatter.sendChatLessageToUser("Waiting for " + seconds + " seconds");
        return seconds * 20;

    }
}
