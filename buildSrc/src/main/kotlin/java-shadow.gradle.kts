plugins {
    id("java-convention")
    id("com.github.johnrengelman.shadow")
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName = "MapModCompanion-${
            project.name.replaceFirstChar {
                it.uppercaseChar()
            }
        }.jar"
        
        listOf(
            "org.bstats",
        ).forEach { pkg ->
            relocate(pkg, with(rootProject) { "${group}.${name}.shade.${pkg}" })
        }
    }
}
