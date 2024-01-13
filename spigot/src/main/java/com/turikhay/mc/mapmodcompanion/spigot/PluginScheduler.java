package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.Disposable;

public interface PluginScheduler extends Disposable {
    void schedule(Runnable r);
}
