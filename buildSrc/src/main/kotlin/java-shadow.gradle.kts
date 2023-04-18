plugins {
    id("java-convention")
    id("com.github.johnrengelman.shadow")
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName.set("MapModCompanion-${
            project.name.replaceFirstChar {
                it.uppercaseChar()
            }
        }.jar")
        
        relocate("org.bstats", "${rootProject.group}.${rootProject.name}.shade.org.bstats")
    }
}
