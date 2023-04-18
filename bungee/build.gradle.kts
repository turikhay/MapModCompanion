import kr.entree.spigradle.kotlin.bungeecord

plugins {
    id("java-shadow")
    alias(libs.plugins.spigradle.bungee)
}

bungee {
    name = "MapModCompanion"
    author = "turikhay"
    debug {
        jvmArgs = listOf("-Xmx256m", "-Dnet.md_5.bungee.console-log-level=ALL")
    }
}

dependencies {
    implementation(project(":common"))
    implementation(libs.bstats.bungeecord)
    compileOnly(bungeecord())
}

tasks.shadowJar {
    archiveFileName.set("MapModCompanion-Bungee.jar")
}