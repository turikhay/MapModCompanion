plugins {
    id("java-convention")
    id("com.gradleup.shadow")
}

tasks {
    shadowJar {
        archiveFileName = "MapModCompanion-${
            project.name.replaceFirstChar {
                it.uppercaseChar()
            }
        }-shadow.jar"
        
        listOf(
            "org.bstats",
        ).forEach { pkg ->
            relocate(pkg, with(rootProject) { "${group}.${name}.shade.${pkg}" })
        }
    }

    val dedupShadowJar = register<Jar>("dedupShadowJar") {
        dependsOn(shadowJar)
        from(shadowJar.map { zipTree(it.singleFile) })
        archiveFileName = shadowJar.map {
            it.archiveFileName.get().replace("-shadow.jar", ".jar")
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    assemble {
        dependsOn(dedupShadowJar)
    }
}
