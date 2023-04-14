plugins {
    java
    alias(libs.plugins.shadow)
}

apply {
    from("$rootDir/gradle/java.gradle")
    from("$rootDir/gradle/shadow.gradle")
}

dependencies {
    implementation(project(":bungee"))
    implementation(project(":spigot"))
    implementation(project(":velocity"))
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName.set("MapModCompanion.jar")
    }
}
