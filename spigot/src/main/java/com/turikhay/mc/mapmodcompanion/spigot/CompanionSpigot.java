package com.turikhay.mc.mapmodcompanion.spigot;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class CompanionSpigot extends JavaPlugin implements Listener {
    List<Handler<?>> handlers = Arrays.asList(
            new XaerosHandler(this),
            new WorldIdHandler(this)
    );

    @Override
    public void onEnable() {
        handlers.forEach(Handler::init);
    }
}
