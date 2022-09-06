package com.turikhay.mc.mapmodcompanion.worldid;

public class WorldIdCompanion {
    public static final String WORLD_ID_CHANNEL_NAME = "worldinfo:world_id";

    public static final int WORLD_ID_MAX_LENGTH = Integer.parseInt(
            System.getProperty(WorldIdCompanion.class.getPackage().getName() + ".max_length",
                    String.valueOf(Byte.MAX_VALUE))
    );

    public static final int WORLD_ID_PACKET_DELAY = Integer.parseInt(
            System.getProperty(WorldIdCompanion.class.getPackage().getName() + ".delay", "5")
    );
}
