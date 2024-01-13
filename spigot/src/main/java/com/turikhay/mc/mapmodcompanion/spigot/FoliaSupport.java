package com.turikhay.mc.mapmodcompanion.spigot;

public class FoliaSupport {
    private static Boolean IS_FOLIA_SERVER;

    public static boolean isFoliaServer() {
        if (IS_FOLIA_SERVER == null) {
            boolean isIt = true;
            try {
                Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            } catch (Throwable t) {
                isIt = false;
            }
            IS_FOLIA_SERVER = isIt;
        }
        return IS_FOLIA_SERVER;
    }
}
