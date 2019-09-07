package me.mattstudios.mf.components;

import org.bukkit.ChatColor;

public class Util {

    /**
     * Utility to use color codes easily
     *
     * @param message The message String
     * @return returns the string with color
     */
    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
