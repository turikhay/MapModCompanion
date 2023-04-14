import groovy.json.JsonSlurper
import groovy.json.JsonOutput

plugins {
    java
    alias(libs.plugins.shadow)
}

apply {
    from("$rootDir/gradle/java.gradle")
    from("$rootDir/gradle/shadow.gradle")
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
    classes {
        doLast {
            val pluginFile = file("$buildDir/classes/java/main/velocity-plugin.json")
            @Suppress("UNCHECKED_CAST")
            val pluginDescriptor = JsonSlurper().parse(pluginFile) as MutableMap<String, Any>
            pluginDescriptor["version"] = version
            pluginFile.writeText(JsonOutput.toJson(pluginDescriptor))
        }
    }

    shadowJar {
        archiveFileName.set("mapmodcompanion.jar")
    }
}