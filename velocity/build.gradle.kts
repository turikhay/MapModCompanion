plugins {
    id("java-shadow")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":common"))
    implementation(libs.bstats.velocity)

    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)
}

tasks {
    val rewriteVelocityPluginJson by creating(PluginDescriptorTask::class) {
        dependsOn(compileJava)
        descriptorFile = project.layout.buildDirectory.file("classes/java/main/velocity-plugin.json")
        format = PluginDescriptorFormat.JSON
        append = true
        content.putAll(mapOf(
                "version" to project.version
        ))
    }

    shadowJar {
        archiveFileName = "mapmodcompanion-shadow.jar"
    }
}
