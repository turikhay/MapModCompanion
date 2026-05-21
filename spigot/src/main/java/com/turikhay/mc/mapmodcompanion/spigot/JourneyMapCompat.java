package com.turikhay.mc.mapmodcompanion.spigot;

import org.bukkit.Server;

public class JourneyMapCompat {
    public static boolean isInstalled(Server server) {
        return server.getPluginManager().getPlugin("journeymap") != null;
    }
}
