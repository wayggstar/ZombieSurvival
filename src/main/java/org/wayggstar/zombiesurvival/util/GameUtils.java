package org.wayggstar.zombiesurvival.util;

public class GameUtils {

    public static String convertTicksToTime(long ticks) {
        int hours = (int) ((ticks / 1000 + 6) % 24);
        int minutes = (int) (ticks % 1000 * 60 / 1000);
        return String.format("%02d시%02d분", hours, minutes);
    }
}