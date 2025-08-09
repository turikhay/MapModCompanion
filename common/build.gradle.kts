plugins {
    id("java-convention")
}

dependencies {
    implementation(project(":api"))
    compileOnly(libs.slf4j)
}
