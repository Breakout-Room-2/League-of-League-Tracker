package com.example.leagueoflegendstracker;

import java.util.Locale;

/**
 * A copy of the timeFormatter class used int he twitterclone project, moedified to work with the
 * unixEpochTime long value that the MatchSummary's timeStamp stores
 * String representing the relative time difference "30s ago", "2m ago", "6d ago"
 */
public class TimeFormatter {
    public static String getTimeDifference(long timeStamp){
        // base String to build up
        String time;

        // Convert difference from ms to s
        long diff = (System.currentTimeMillis() - timeStamp) / 1000;

        // determine whether to return time difference in s, m, h, or d
        if (diff < 60)
            time = String.format(Locale.US, "%ds ago", diff);
        else if (diff < 60*60)
            time = String.format(Locale.US, "%dm ago", diff / 60);
        else if (diff < 60*60*24)
            time = String.format(Locale.US, "%dh ago", diff / (60*60));
        else
            time = String.format(Locale.US, "%dd ago", diff / (60*60*24));

        return time;
    }

    public static String getTimeDuration(long duration){
        int minutes = (int) duration / 60;
        int seconds = (int) duration % 60;
        return String.format(Locale.US, "%dm %ds", minutes, seconds);
    }

    // To check matchDuration < 5 (remake)
    public static int getTimeMinutes(long duration){
        return (int) duration / 60;
    }

    public static String getTimeLeft(long diff){
        int hours   = (int) (diff / 1000) / (60*60);
        int minutes = ((int) (diff / 1000)  % (60*60)) / (60);

        return String.format(Locale.US, "%dh %dm", hours, minutes);
    }
}