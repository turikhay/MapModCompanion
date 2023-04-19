plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(with(libs.plugins.shadow.get()) { "com.github.johnrengelman:shadow:$version" })
}
