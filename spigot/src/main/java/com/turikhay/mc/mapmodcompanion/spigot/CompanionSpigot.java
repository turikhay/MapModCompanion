package com.turikhay.mc.mapmodcompanion.spigot;

import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CompanionSpigot extends JavaPlugin implements Listener {
    public static final boolean ENABLE_LOGGING = Boolean.parseBoolean(
            System.getProperty(CompanionSpigot.class.getPackage().getName() + ".debug", "false")
    );

    public static final boolean DISABLE_DEFAULT_WORLD_ID = Boolean.parseBoolean(
            System.getProperty(CompanionSpigot.class.getPackage().getName() + ".defaultId", "false")
    );

    public static final boolean USE_TEXTUAL_WORLD_ID = Boolean.parseBoolean(
            System.getProperty(CompanionSpigot.class.getPackage().getName() + ".useTextualId", "false")
    );

    List<Handler<?, ?>> handlers = Arrays.asList(
            new XaerosMinimapHandler(this),
            new XaerosWorldMapHandler(this),
            new WorldIdHandler(this, false),
            new WorldIdHandler(this, true)
    );

    DefaultWorld defaultWorld;

    @Override
    public void onEnable() {
        if (DISABLE_DEFAULT_WORLD_ID) {
            getLogger().info("Plugin will not use default world ID for every world");
            defaultWorld = DefaultWorld.empty();
        } else {
            defaultWorld = DefaultWorld.detectDefaultWorld(this);
        }
        initializeHandlers();
    }

    private void initializeHandlers() {
        Iterator<Handler<?, ?>> i = handlers.iterator();
        while (i.hasNext()) {
            Handler<?, ?> handler = i.next();
            try {
                handler.init();
            } catch (Handler.InitializationException e) {
                if (ENABLE_LOGGING) {
                    getLogger().info(String.format(Locale.ROOT,
                            "\"%s\" channel handler will not be available: %s",
                            handler.channelName, e
                    ));
                    e.printStackTrace();
                }
                i.remove();
            }
        }
    }

    Optional<World> getDefaultWorld() {
        return defaultWorld.optional();
    }
}
