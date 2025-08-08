package com.turikhay.mc.mapmodcompanion;

/**
 * Utility holder for network channel identifiers used by various map
 * modifications.
 * <p>
 * These constants can be used with the platform specific networking API to
 * register listeners or send packets. For example:
 *
 * <pre>{@code
 * registerChannel(Channels.WORLDID_CHANNEL, data -> {
 *     // ...
 * });
 * }</pre>
 */
public interface Channels {
    /** Plugin channel used by Xaero's Minimap. */
    String XAERO_MINIMAP_CHANNEL = "xaerominimap:main";
    /** Plugin channel used by Xaero's World Map. */
    String XAERO_WORLDMAP_CHANNEL = "xaeroworldmap:main";
    /** Official MapModCompanion channel used to query world identifiers. */
    String WORLDID_CHANNEL = "worldinfo:world_id";
    /** Legacy channel name supported for backwards compatibility. */
    String WORLDID_LEGACY_CHANNEL = "world_id";
}
