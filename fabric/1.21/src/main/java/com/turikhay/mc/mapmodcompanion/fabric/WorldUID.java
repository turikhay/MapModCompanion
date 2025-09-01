package com.turikhay.mc.mapmodcompanion.fabric;

import net.minecraft.server.world.ServerWorld;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

// CraftBukkit-style uid.dat file reader/writer
public class WorldUID {
    public static UUID getOrCreate(ServerWorld world) throws IOException {
        Path worldDir = getWorldDir(world);
        return getOrCreate(worldDir);
    }

    static Path getWorldDir(ServerWorld world) {
        return world.getServer().session.getDirectory().path();
    }

    static UUID getOrCreate(Path worldDir) throws IOException {
        return getOrCreate(worldDir, UUID.randomUUID());
    }

    static UUID getOrCreate(Path worldDir, UUID fallbackUid) throws IOException {
        Path uidFile = getUidFile(worldDir);
        if (Files.exists(uidFile)) {
            return read(uidFile);
        }
        write(uidFile, fallbackUid);
        return fallbackUid;
    }

    static Path getUidFile(Path worldDir) {
        return worldDir.resolve("uid.dat");
    }

    static UUID read(Path uidFile) throws IOException {
        try (DataInputStream inputStream = new DataInputStream(Files.newInputStream(uidFile))) {
            return new UUID(inputStream.readLong(), inputStream.readLong());
        }
    }

    static void write(Path uidFile, UUID uuid) throws IOException {
        try (DataOutputStream outputStream = new DataOutputStream(Files.newOutputStream(uidFile))) {
            outputStream.writeLong(uuid.getMostSignificantBits());
            outputStream.writeLong(uuid.getLeastSignificantBits());
        }
    }
}
