package com.turikhay.mc.mapmodcompanion.spigot;

import com.turikhay.mc.mapmodcompanion.Disposable;
import com.turikhay.mc.mapmodcompanion.InitializationException;

import java.util.Set;

public interface XaeroListener extends Disposable {
    String name();
    void init(Set<String> neighbors) throws InitializationException;
}
