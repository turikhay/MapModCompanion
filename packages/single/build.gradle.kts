plugins {
    java
    alias(libs.plugins.shadow)
}

apply {
    from("$rootDir/gradle/java.gradle")
}

dependencies {
    implementation(project(":bungee"))
    implementation(project(":spigot"))
    implementation(project(":velocity"))
}

tasks {
    shadowJar {
        archiveFileName.set("MapModCompanion.jar")
    }
}