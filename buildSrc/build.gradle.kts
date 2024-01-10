plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    // See https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(with(libs.plugins.shadow.get()) { "com.github.johnrengelman:shadow:$version" })
    with(libs.versions.jackson) {
        val version = get()
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$version")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$version")
    }
}
