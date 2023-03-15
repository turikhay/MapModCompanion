package com.turikhay.mc.mapmodcompanion;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public interface Handler extends Disposable {
    static <PluginType> List<Handler> initialize(Logger logger, PluginType plugin, List<Factory<PluginType>> factories) {
        ArrayList<Handler> handlers = new ArrayList<>();
        for (Factory<PluginType> factory : factories) {
            logger.fine("Calling the handler factory: " + factory.getName());
            Handler handler;
            try {
                handler = factory.create(plugin);
            } catch (InitializationException e) {
                logger.info(factory.getName() + " handler will not be available (" + e.getMessage() + ")");
                continue;
            }
            handlers.add(handler);
            logger.fine("Handler has been initialized: " + handler);
        }
        return handlers;
    }

    static void cleanUp(Logger logger, List<Handler> handlers) {
        logger.fine("Cleaning up " + handlers.size() + " handlers");
        handlers.forEach(Handler::cleanUp);
    }

    interface Factory<PluginType> {
        String getName();
        Handler create(PluginType plugin) throws InitializationException;
    }

}
