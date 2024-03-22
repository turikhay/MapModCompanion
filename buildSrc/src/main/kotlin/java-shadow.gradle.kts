plugins {
    id("java-convention")
    id("com.github.johnrengelman.shadow")
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    val createDedupJar = register<Jar>("createDedupJar") {
        from(shadowJar.map { zipTree(it.singleFile) })
        archiveFileName = shadowJar.map {
            it.archiveFileName.get().replace(".jar", "-dedup.jar")
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    val copyBackDedupJar = register("copyBackDedupJar") {
        dependsOn(createDedupJar)
        doLast {
            createDedupJar.singleFile.get().copyTo(
                    shadowJar.singleFile.get(),
                    overwrite = true
            )
        }
    }

    val dedupShadowJar = register("dedupShadowJar") {
        dependsOn(shadowJar, createDedupJar, copyBackDedupJar)
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

        finalizedBy(dedupShadowJar)
    }
}
