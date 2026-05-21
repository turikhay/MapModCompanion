plugins {
    id("java-shadow")
}

repositories {
    maven {
        name = "Sonatype"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    maven {
        name = "Spigot"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/public/")
    }
    maven {
        name = "CodeMC"
        url = uri("https://repo.codemc.io/repository/maven-releases/")
    }
}

tasks {
    val writePluginYml by registering(PluginDescriptorTask::class) {
        descriptor = "plugin.yml"
        content.putAll(mapOf(
                "name" to "MapModCompanion",
                "version" to project.version,
                "main" to "com.turikhay.mc.mapmodcompanion.spigot.MapModCompanion",
                "description" to "Plugin that fixes Multi-world detection for Xaero's Minimap, VoxelMap and JourneyMap",
                "authors" to listOf("turikhay"),
                "website" to "https://github.com/turikhay/MapModCompanion",
                "api-version" to "1.13",
                "softdepend" to listOf("ProtocolLib", "packetevents"),
                "folia-supported" to true,
        ))
    }
    processResources {
        finalizedBy(writePluginYml)
    }
}

// From gradle.properties
val spigot_version: String by project
val protocolLib_version: String by project
val packetEvents_version: String by project

dependencies {
    implementation(project(":common"))
    implementation(libs.bstats.bukkit)

    // These dependencies are intentionally not present in libs.version.toml
    compileOnly("org.spigotmc:spigot-api:${spigot_version}-R0.1-SNAPSHOT")
    compileOnly("net.dmulloy2:ProtocolLib:${protocolLib_version}")
    compileOnly("com.github.retrooper:packetevents-spigot:${packetEvents_version}")
}
