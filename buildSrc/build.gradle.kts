plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    gradlePluginPortal()
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
}

gradlePlugin {
    plugins {
        create("platformReadme") {
            id = "platform-readme"
            implementationClass = "PlatformReadmePlugin"
        }
    }
}

dependencies {
    // See https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(with(libs.plugins.shadow.get()) { "com.gradleup.shadow:com.gradleup.shadow.gradle.plugin:$version" })
    with(libs.versions.jackson) {
        val version = get()
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$version")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$version")
    }
    implementation(libs.semver.get().toString())
    implementation("com.modrinth.minotaur:Minotaur:2.+")
    implementation(with(libs.plugins.hangar.get()) { "io.papermc:hangar-publish-plugin:$version" })
    implementation(with(libs.plugins.fabric.loom.get()) { "net.fabricmc:fabric-loom:$version" })
}
