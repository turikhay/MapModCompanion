package com.turikhay.mc.mapmodcompanion.xaeros;

public class XaerosCompanion {
    public static final String XAEROS_CHANNEL_NAME = "xaeroworldmap:main";
    public static final int XAEROS_PACKET_REPEAT_TIMES = Integer.parseInt(
            System.getProperty(XaerosCompanion.class.getPackage().getName() + ".repeats", "3")
    );
}
