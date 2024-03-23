import net.swiftzer.semver.SemVer

plugins {
    id("java-shadow")
    id("com.modrinth.minotaur")
    id("io.papermc.hangar-publish-plugin")
}

dependencies {
    implementation(project(":bungee"))
    implementation(project(":spigot"))
    implementation(project(":velocity"))
}

val dedupShadowJar = tasks.named("dedupShadowJar")
val semVer = SemVer.parse(project.version as String)
val isRelease = semVer.preRelease == null
val commonChangelog = """
    Changelog is available on
    [GitHub](https://github.com/turikhay/MapModCompanion/releases/tag/v${project.version})
""".trimIndent()
val allVersions = provider { rootProject.file("VERSIONS.txt").readLines() }
val platformReadme = provider { generatePlatformReadme(project) }

modrinth {
    token = System.getenv("MODRINTH_TOKEN")
    projectId = "UO7aDcrF"
    versionNumber = project.version as String
    changelog = commonChangelog
    versionType = run {
        val preRelease = semVer.preRelease
        if (preRelease != null) {
            if (preRelease.contains("beta")) {
                "beta"
            } else {
                "alpha"
            }
        } else {
            "release"
        }
    }
    syncBodyFrom = platformReadme
    file = dedupShadowJar.singleFile
    gameVersions = allVersions
    loaders.addAll(listOf(
            "bukkit",
            "bungeecord",
            "folia",
            "paper",
            "spigot",
            "velocity",
            "waterfall",
    ))
}

hangarPublish {
    publications.register("plugin") {
        version = project.version as String
        id = "MapModCompanion"
        channel = run {
            val preRelease = semVer.preRelease
            if (preRelease != null) {
                "Beta"
            } else {
                "Release"
            }
        }
        changelog = commonChangelog
        apiKey = System.getenv("HANGAR_TOKEN")
        platforms {
            val singleJar = dedupShadowJar.singleFile
            val families = allVersions.map { list -> list.map {
                val split = it.split(".") // -> 1, 20[, 4]
                assert(split.size > 1)
                assert(split.first() == "1") // will Minecraft 2.0 ever come out?
                Integer.parseInt(split[1]) // "1.20.4" -> 20
            }.sorted() }
            paper {
                jar = singleJar
                platformVersions = families.map {
                    listOf("1.${it.first()}-1.${it.last()}") // 1.8 - latest
                }
                dependencies {
                    hangar("ProtocolLib") {
                        required = false
                    }
                }
            }
            waterfall {
                jar = singleJar
                platformVersions = families.map {
                    f -> f.filter { it >= 11 } // Waterfall is only available >= 1.11
                }.map {
                    listOf("1.${it.first()}-1.${it.last()}")
                }
            }
            velocity {
                val velocityFamily = libs.versions.velocity.api.map {
                    val split = it.split(".")
                    "${split[0]}.${split[1]}"
                }
                jar = singleJar
                platformVersions = velocityFamily.map { listOf(it) }
            }
        }
        pages {
            resourcePage(platformReadme)
        }
    }
}

tasks {
    shadowJar {
        archiveFileName = "MapModCompanion-shadow.jar"
    }
    getByName("modrinth") {
        dependsOn(
                dedupShadowJar,
                modrinthSyncBody
        )
    }
    getByName("publishPluginPublicationToHangar") {
        dependsOn(
                dedupShadowJar,
                getByName("syncAllPluginPublicationPagesToHangar")
        )
    }
}