package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.xaeros.LevelMapProperties;
import org.bukkit.World;
import org.bukkit.event.Listener;

import static com.turikhay.mc.mapmodcompanion.xaeros.XaerosCompanion.XAEROS_PACKET_REPEAT_TIMES;

public abstract class XaerosAbstractHandler extends Handler<LevelMapProperties, Void> implements Listener {
    public XaerosAbstractHandler(String channelName, CompanionSpigot plugin) {
        super(channelName, plugin);
    }

    @Override
    public void scheduleLevelIdPacket(Runnable r, Context<Void> context) {
        if (context.getSource() == EventSource.JOIN) {
            // Sometimes Xaero's World Map is not initialized at the time they receive our packet,
            // leading to world not being recognized. We fix this issue by sending our packet more than once.
            for (int i = 0; i < XAEROS_PACKET_REPEAT_TIMES; i++) {
                plugin.getServer().getScheduler().runTaskLater(plugin, r, 20L * i);
            }
        } else {
            r.run();
        }
    }

    @Override
    public LevelMapProperties getId(World world) {
        return new LevelMapProperties(world.getUID().hashCode());
    }
}
