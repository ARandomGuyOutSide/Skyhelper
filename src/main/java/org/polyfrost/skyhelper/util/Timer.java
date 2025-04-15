package org.polyfrost.skyhelper.util;

public class Timer {

    public static double secondsToTicks(double seconds)
    {
        Chatter.sendDebutChat("Waiting for " + seconds + " seconds");
        return seconds * 20;

    }
}
