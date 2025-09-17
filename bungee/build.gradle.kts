plugins {
    id("java-shadow")
}

repositories {
    maven {
        name = "Sonatype"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    maven {
        name = "Minecraft"
        url = uri("https://libraries.minecraft.net")
        content {
            includeGroup("com.mojang")
        }
    }
}

tasks {
    val writeBungeeYml by creating(PluginDescriptorTask::class) {
        descriptor = "bungee.yml"
        content.putAll(mapOf(
                "name" to "MapModCompanion",
                "version" to project.version,
                "author" to "turikhay",
                "main" to "com.turikhay.mc.mapmodcompanion.bungee.MapModCompanion"
        ))
    }
}

dependencies {
    implementation(project(":api"))
    implementation(project(":common"))
    implementation(libs.bstats.bungeecord)
    compileOnly(libs.bungeecord.api)
}
