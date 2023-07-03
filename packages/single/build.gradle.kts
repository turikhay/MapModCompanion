plugins {
    id("java-shadow")
}

dependencies {
    implementation(project(":bungee"))
    implementation(project(":spigot"))
    implementation(project(":velocity"))
}

tasks.shadowJar {
    archiveFileName = "MapModCompanion.jar"
}
