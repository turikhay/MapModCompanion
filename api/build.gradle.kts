plugins {
    id("java-convention")
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    fun env(name: String) = System.getenv(name) ?: name
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${env("GITHUB_REPOSITORY")}")
            credentials {
                username = env("GITHUB_ACTOR")
                password = env("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name = "MapModCompanion (API)"
                artifactId = "mapmodcompanion-api"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }
            }
        }
    }
}
