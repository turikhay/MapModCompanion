package com.turikhay.mc.mapmodcompanion.spigot;

import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class BukkitScheduler implements PluginScheduler {
    private final Plugin plugin;

    public BukkitScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void cleanUp() {
    }

    @Override
    public void schedule(Runnable r) {
        if (plugin.getServer().isPrimaryThread()) {
            executeTask(r);
        } else {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> executeTask(r));
        }
    }

    private void executeTask(Runnable r) {
        try {
            r.run();
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, "Failed to execute the task", t);
        }
    }

    @Override
    public String toString() {
        return "BukkitScheduler{}";
    }
}
