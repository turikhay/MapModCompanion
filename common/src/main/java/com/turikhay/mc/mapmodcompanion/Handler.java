package com.turikhay.mc.mapmodcompanion;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public interface Handler extends Disposable {
    static <PluginType> List<Handler> initialize(ILogger logger, PluginType plugin, List<Factory<PluginType>> factories) {
        ArrayList<Handler> handlers = new ArrayList<>();
        for (Factory<PluginType> factory : factories) {
            Handler handler = initialize(logger, plugin, factory);
            handlers.add(handler);
        }
        return handlers;
    }

    @SuppressWarnings({"unchecked"})
    static <HandlerType, PluginType> @Nullable HandlerType initialize(ILogger logger, PluginType plugin, Factory<PluginType> factory) {
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

    static <PluginType> List<Handler> initialize(java.util.logging.Logger logger, PluginType plugin, List<Factory<PluginType>> factories) {
        return initialize(ILogger.ofJava(logger), plugin, factories);
    }

    static <HandlerType, PluginType> @Nullable HandlerType initialize(java.util.logging.Logger logger, PluginType plugin, Factory<PluginType> factory) {
        return initialize(ILogger.ofJava(logger), plugin, factory);
    }

    static void cleanUp(ILogger logger, List<Handler> handlers) {
        logger.fine("Cleaning up " + handlers.size() + " handlers");
        handlers.forEach(Handler::cleanUp);
    }

    static void cleanUp(java.util.logging.Logger logger, List<Handler> handlers) {
        cleanUp(ILogger.ofJava(logger), handlers);
    }

    interface Factory<PluginType> {
        String getName();
        Handler create(PluginType plugin) throws InitializationException;
    }

}
