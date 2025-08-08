plugins {
    id("java-shadow")
}

repositories {
    maven {
        name = "ProtocolLib"
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
    maven {
        name = "Sonatype"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    maven {
        name = "Spigot"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

tasks {
    val writePluginYml by creating(PluginDescriptorTask::class) {
        descriptor = "plugin.yml"
        content.putAll(mapOf(
                "name" to "MapModCompanion",
                "version" to project.version,
                "main" to "com.turikhay.mc.mapmodcompanion.spigot.MapModCompanion",
                "description" to "Plugin that fixes Multi-world detection for Xaero's Minimap, VoxelMap and JourneyMap",
                "authors" to listOf("turikhay"),
                "website" to "https://github.com/turikhay/MapModCompanion",
                "api-version" to "1.13",
                "softdepend" to listOf("ProtocolLib"),
                "folia-supported" to true,
        ))
    }
}

// From gradle.properties
val spigot_version: String by project
val protocolLib_version: String by project

dependencies {
    implementation(project(":api"))
    implementation(project(":common"))
    implementation(libs.bstats.bukkit)

    // These dependencies are intentionally not present in libs.version.toml
    compileOnly("org.spigotmc:spigot-api:${spigot_version}-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:${protocolLib_version}")
}
