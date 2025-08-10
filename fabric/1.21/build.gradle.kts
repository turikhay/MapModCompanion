plugins {
    id("fabric-loom")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":api"))
    implementation(project(":common"))

    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${properties["yarn_mappings"]}:v2")
    modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"]}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:${properties["fabric_version"]}")
}

loom {
    accessWidenerPath = file("src/main/resources/mapmodcompanion.accesswidener")
}

tasks {
    processResources {
        filesMatching("fabric.mod.json") {
            expand("version" to version)
        }
    }
}
