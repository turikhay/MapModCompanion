plugins {
    java
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":bungee"))
    implementation(project(":spigot"))
    implementation(project(":velocity"))
}

tasks.shadowJar {
    archiveFileName.set("MapModCompanion.jar")
}
