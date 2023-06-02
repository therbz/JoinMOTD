package me.therbz.joinmotd;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MessageUtils {
    public static void sendNoPermMessage(JoinMOTD main, Audience audience){
        audience.sendMessage(MiniMessage.miniMessage().deserialize(Objects.requireNonNull(main.getConfig().getString("messages.no-perm"))));
    }

    // https://www.spigotmc.org/threads/parsing-time-with-strings.162162/
    public static long parseTime(String string) {
        // checks if the string is either null and empty and if so returns 0
        if (string == null || string.isEmpty())
            return 0L;
        // this replaces the regex for 0-9 and the other characters
        string = string.replaceAll("[^0-9smhdw]", "");
        // checks if the new string is empty since we removed some characters
        if (string.isEmpty())
            return 0L;
        // Check if string contains "w"
        if (string.contains("w")) {
            // Replace all non numbers with nothing
            string = string.replaceAll("[^0-9]", "");
            // Another empty check
            if (string.isEmpty())
                return 0L;
            // If it has a number we change the number value to days by
            // multiplying by 7 then we can change it to seconds
            return TimeUnit.DAYS.toSeconds(Long.parseLong(string) * 7);
        }
        // First we check for days using "d"
        TimeUnit unit = string.contains("d") ? TimeUnit.DAYS
                // If the string contains "h" it goes for hours
                : string.contains("h") ? TimeUnit.HOURS
                // If the string contains "m" it goes for minutes
                : string.contains("m") ? TimeUnit.MINUTES
                // Finally, if none match we go with seconds
                : TimeUnit.SECONDS;
        // Next we replace all the non-numbers with nothing so it can match a
        // number
        string = string.replaceAll("[^0-9]", "");
        // Another empty check to make sure something is there
        if (string.isEmpty())
            return 0L;
        // Then we return the string as a long in seconds using the unit
        // selected earlier
        return unit.toSeconds(Long.parseLong(string));
    }

    public static String stringifyTimeInSeconds(long timeInSeconds, JoinMOTD main) {
        // Get config strings
        String daysString = main.getConfig().getString("time.days");
        String hoursString = main.getConfig().getString("time.hours");
        String minutesString = main.getConfig().getString("time.minutes");
        String secondsString = main.getConfig().getString("time.seconds");
        String separatorString = main.getConfig().getString("time.separator");

        // Calculate time intervals
        long days = timeInSeconds / 86400;
        timeInSeconds = timeInSeconds % 86400;

        long hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds % 3600;

        long minutes = timeInSeconds / 60;
        timeInSeconds = timeInSeconds % 60;

        long seconds = timeInSeconds;

        // Build
        boolean startOfString = true;
        StringBuilder builder = new StringBuilder();
        if (days > 0) { // Days will always be at the start
            builder.append(days).append(daysString);
            startOfString = false;
        }
        if (hours > 0) {
            if (!startOfString) { builder.append(separatorString); }
            builder.append(hours).append(hoursString);
            startOfString = false;
        }
        if (minutes > 0) {
            if (!startOfString) { builder.append(separatorString); }
            builder.append(minutes).append(minutesString);
            startOfString = false;
        }
        if (seconds > 0) {
            if (!startOfString) { builder.append(separatorString); }
            builder.append(seconds).append(secondsString);
        } // Don't bother setting it false, we don't care anymore

        // Return
        return builder.toString();
    }
}
