plugins {
    id("java-convention")
    id("com.github.johnrengelman.shadow")
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        relocate("org.bstats", "${rootProject.group}.${rootProject.name}.shade.org.bstats")
    }
}