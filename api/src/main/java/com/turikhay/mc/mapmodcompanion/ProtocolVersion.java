package com.turikhay.mc.mapmodcompanion;

/**
 * Well-known Minecraft protocol version numbers.
 *
 * <p>These constants can be used to interpret version-specific nuances in
 * packet formats.</p>
 */
public interface ProtocolVersion {
    /** Protocol version number for Minecraft 1.13.2. */
    int MINECRAFT_1_13_2 = 404;
    /** Protocol version number for Minecraft 1.14.4. */
    int MINECRAFT_1_14_4 = 498;
    /** Protocol version number for Minecraft 1.15.2. */
    int MINECRAFT_1_15_2 = 578;
    /** Protocol version number for Minecraft 1.16.3. */
    int MINECRAFT_1_16_3 = 753;
}
