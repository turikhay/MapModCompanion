package com.turikhay.mc.mapmodcompanion.spigot;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.turikhay.mc.mapmodcompanion.Handler;
import com.turikhay.mc.mapmodcompanion.InitializationException;
import org.bukkit.entity.Player;

public class ProtocolLib implements Handler {
    private final ProtocolManager manager;

    public ProtocolLib(ProtocolManager manager) {
        this.manager = manager;
    }

    public ProtocolLib() {
        this(ProtocolLibrary.getProtocolManager());
    }

    public int getProtocolVersion(Player player) {
        return manager.getProtocolVersion(player);
    }

    @Override
    public void cleanUp() {
    }

    public static class Factory implements Handler.Factory<MapModCompanion> {
        @Override
        public String getName() {
            return "ProtocolLib";
        }

        @Override
        public Handler create(MapModCompanion plugin) throws InitializationException {
            try {
                return new ProtocolLib();
            } catch (NoClassDefFoundError e) {
                throw new InitializationException("missing dependency", e);
            }
        }
    }
}
