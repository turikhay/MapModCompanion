package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XaeroHandler implements Handler {
    private final ScheduledExecutorService scheduler;
    private final List<XaeroListener> listeners;

    public XaeroHandler(ScheduledExecutorService scheduler, List<XaeroListener> listeners) {
        this.scheduler = scheduler;
        this.listeners = listeners;
    }

    @Override
    public void cleanUp() {
        try {
            listeners.forEach(Disposable::cleanUp);
        } finally {
            scheduler.shutdown();
        }
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
            PrefixLogger logger = new PrefixLogger(plugin.getVerboseLogger(), channelName);
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(
                    new DaemonThreadFactory(ILogger.ofJava(logger), XaeroHandler.class)
            );
            ListenerFactory factory = new ListenerFactory(
                    plugin,
                    logger,
                    new XaeroLevelMapSender(logger, plugin, channelName, configPath, scheduler)
            );
            List<Candidate> candidates = factory.getCandidateFactories();
            List<XaeroListener> listeners = new ArrayList<>(candidates.size());
            Set<String> neighbors = new HashSet<>(candidates.size());
            List<Throwable> suppressed = new ArrayList<>(candidates.size());
            for (Candidate candidate : candidates) {
                XaeroListener listener;
                try {
                    listener = candidate.create();
                    listener.init(neighbors);
                } catch (Throwable t) {
                    if (t instanceof InitializationException) {
                        logger.log(Level.INFO, "One of the listeners will not be available: " + t.getMessage());
                    } else {
                        logger.log(Level.WARNING, "Failed to create or initialize a listener", t);
                    }
                    suppressed.add(t);
                    continue;
                }
                neighbors.add(listener.name());
                listeners.add(listener);
                logger.fine("Listener created: " + listener.name());
            }
            if (listeners.isEmpty()) {
                InitializationException e = new InitializationException("Failed to create at least one of listeners; check suppressed exceptions");
                suppressed.forEach(e::addSuppressed);
                throw e;
            }
            logger.info("Created listeners: " + listeners);
            return new XaeroHandler(scheduler, listeners);
        }

        private class ListenerFactory {
            final MapModCompanion plugin;
            final Logger logger;
            final XaeroLevelMapSender sender;

            private ListenerFactory(MapModCompanion plugin, Logger logger, XaeroLevelMapSender sender) {
                this.plugin = plugin;
                this.logger = logger;
                this.sender = sender;
            }

            public List<Candidate> getCandidateFactories() {
                List<Candidate> listeners = new ArrayList<>();
                listeners.add(this::createPacketEventsListener);
                listeners.add(this::createSpigotListener);
                return listeners;
            }

            XaeroListener createPacketEventsListener() throws InitializationException {
                try {
                    return new XaeroRespawnPacketListener(sender);
                } catch (NoClassDefFoundError e) {
                    if (FoliaSupport.isFoliaServer()) {
                        logger.log(Level.WARNING, "PacketEvents is not found. Please install it, if it's available for your Folia version.");
                        logger.log(Level.WARNING, "While it is not required, it is strongly advised to have it in your plugins folder.");
                        logger.log(Level.WARNING, "For more info, see: https://github.com/turikhay/MapModCompanion/issues/224");
                        logger.log(Level.WARNING, "We'll print the stack trace for your attention :)", e);
                    }
                    throw new InitializationException("PacketEvents is not found", e);
                }
            }

            XaeroListener createSpigotListener() {
                return new XaeroSpigotListener(logger, plugin, channelName, sender);
            }
        }

        private interface Candidate {
            XaeroListener create() throws InitializationException;
        }
    }
}
