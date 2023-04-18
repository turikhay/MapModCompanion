plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.spotbugs.annotations)

    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

tasks.test {
    useJUnitPlatform()
}