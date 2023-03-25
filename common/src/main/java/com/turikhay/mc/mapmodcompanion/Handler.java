package com.turikhay.mc.mapmodcompanion;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public interface Handler extends Disposable {
    static <PluginType> List<Handler> initialize(Logger logger, PluginType plugin, List<Factory<PluginType>> factories) {
        ArrayList<Handler> handlers = new ArrayList<>();
        for (Factory<PluginType> factory : factories) {
            Handler handler = initialize(logger, plugin, factory);
            handlers.add(handler);
        }
        return handlers;
    }

    @SuppressWarnings({"unchecked"})
    static <HandlerType, PluginType> @Nullable HandlerType initialize(Logger logger, PluginType plugin, Factory<PluginType> factory) {
        logger.fine("Calling the handler factory: " + factory.getName());
        Handler handler;
        try {
            handler = factory.create(plugin);
        } catch (InitializationException e) {
            logger.info(factory.getName() + " handler will not be available (" + e.getMessage() + ")");
            return null;
        }
        logger.fine("Handler has been initialized: " + handler);
        return (HandlerType) handler;
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
