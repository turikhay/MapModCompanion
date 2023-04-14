import kr.entree.spigradle.kotlin.bungeecord

plugins {
    java
    alias(libs.plugins.spigradle.bungee)
    alias(libs.plugins.shadow)
}

apply {
    from("$rootDir/gradle/java.gradle")
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