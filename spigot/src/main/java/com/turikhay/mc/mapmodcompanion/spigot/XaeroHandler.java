package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class XaeroHandler implements Handler {
    private final ScheduledExecutorService scheduler;
    private final XaeroSpigotListener listener;

    public XaeroHandler(Logger logger, String configPath, String channelName, MapModCompanion plugin) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(
                new DaemonThreadFactory(ILogger.ofJava(logger), XaeroHandler.class)
        );
        this.listener = new XaeroSpigotListener(logger, plugin, channelName,
                new XaeroLevelMapSender(logger, plugin, channelName, configPath, scheduler)
        );
    }

    public void init() throws InitializationException {
        listener.init();
    }

    @Override
    public void cleanUp() {
        listener.cleanUp();
        scheduler.shutdown();
    }

    public static class Factory implements Handler.Factory<MapModCompanion> {
        private final String configPath;
        private final String channelName;

        public Factory(String configPath, String channelName) {
            this.configPath = configPath;
            this.channelName = channelName;
        }

        @Override
        public String getName() {
            return channelName;
        }

        @Override
        public XaeroHandler create(MapModCompanion plugin) throws InitializationException {
            plugin.checkEnabled(configPath);
            XaeroHandler handler = new XaeroHandler(
                    new PrefixLogger(plugin.getVerboseLogger(), channelName),
                    configPath, channelName, plugin
            );
            handler.init();
            return handler;
        }
    }


}
