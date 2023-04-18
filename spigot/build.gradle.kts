import kr.entree.spigradle.kotlin.spigot

plugins {
    java
    alias(libs.plugins.spigradle)
    alias(libs.plugins.shadow)
}

repositories {
    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
}

val bStats = with(libs.bstats.bukkit.get()) { "$module:$versionConstraint" }
val spigot_version: String by project
val protocolLib_version: String by project

spigot {
    name = "MapModCompanion"
    authors = listOf("turikhay")
    apiVersion = "1.13"
    softDepends = listOf("ProtocolLib")
    debug {
        jvmArgs = listOf(
                "-Xmx2048m",
                "-Dlog4j.configurationFile=${projectDir}${File.separatorChar}log4j2-debug.xml",
                "-Dcom.turikhay.mc.mapmodcompanion.spigot.debug=true"
        )
    }
    afterEvaluate {
        excludeLibraries = listOf(
                rootProject.allprojects.map { "${it.group}:${it.name}:${it.version}" },
                listOf(bStats)
        ).flatten()
    }
}

dependencies {
    implementation(project(":common"))
    implementation(libs.bstats.bukkit)

    // These dependencies are intentionally not present in libs.version.toml
    compileOnly(spigot(spigot_version))
    compileOnly("com.comphenix.protocol:ProtocolLib:${protocolLib_version}")
}

tasks.shadowJar {
    archiveFileName.set("MapModCompanion-Spigot.jar")
}
