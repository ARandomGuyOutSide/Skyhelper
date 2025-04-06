package org.polyfrost.skyhelper.util;

public class Timer {

    public static int secondsToTicks(int seconds)
    {
        Chatter.sendChatMessageToUser("Waiting for " + seconds + " seconds");
        return seconds * 20;

    }
}
