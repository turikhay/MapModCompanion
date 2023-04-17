import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin

plugins {
    java
    alias(libs.plugins.shadow)
}

subprojects {
    plugins.withType<JavaPlugin> {
        val libs = rootProject.project.libs

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
    }

    plugins.withType<ShadowJavaPlugin> {
        tasks {
            assemble {
                dependsOn(shadowJar)
            }

            shadowJar {
                relocate("org.bstats", "${rootProject.group}.${rootProject.name}.shade.org.bstats")
            }
        }
    }
}