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

val semVer = SemVer.parse(project.version as String)
val isRelease = semVer.preRelease == null
val commonChangelog = """
    Changelog is available on
    [GitHub](https://github.com/turikhay/MapModCompanion/releases/tag/v${project.version})
""".trimIndent()
val readmeText: String by lazy { rootProject.file("README.md").readText() }
val allVersions: List<String> by lazy { rootProject.file("VERSIONS.txt").readLines() }

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
    if (isRelease) {
        syncBodyFrom = readmeText
    }
    uploadFile = tasks.getByPath("shadowJar")
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
        id = "MapModCompanion-test"
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
            val singleJar = tasks.shadowJar.map { it.outputs.files.singleFile }
            val families = allVersions.map {
                val split = it.split(".") // -> 1, 20[, 4]
                assert(split.size > 1)
                assert(split.first() == "1") // will Minecraft 2.0 ever come out?
                Integer.parseInt(split[1]) // "1.20.4" -> 20
            }.sorted()
            paper {
                jar = singleJar
                platformVersions = listOf("1.${families.first()}-1.${families.last()}") // 1.8 - latest
//                dependencies {
//                    hangar("ProtocolLib") {
//                        required = false
//                    }
//                }
            }
            waterfall {
                val wfFamilies = families.filter { it >= 11 } // Waterfall is only available >= 1.11
                jar = singleJar
                platformVersions = listOf("1.${wfFamilies.first()}-1.${wfFamilies.last()}")
            }
            velocity {
                val velocityFamily = libs.versions.velocity.api.map {
                    val split = it.split(".")
                    "${split[0]}.${split[1]}"
                }.get()
                jar = singleJar
                platformVersions = listOf(velocityFamily)
            }
        }
        pages {
            if (isRelease) {
                resourcePage(readmeText)
            }
        }
    }
}

tasks {
    shadowJar {
        archiveFileName = "MapModCompanion.jar"
    }
    getByName("modrinth") {
        dependsOn(
                shadowJar,
                modrinthSyncBody
        )
    }
    getByName("publishPluginPublicationToHangar") {
        dependsOn(shadowJar)
    }
    assemble {
        if (System.getenv("MODRINTH_UPLOAD") == "true") {
            dependsOn("modrinth")
        }
        if (System.getenv("HANGAR_UPLOAD") == "true") {
            dependsOn("publishPluginPublicationToHangar")
        }
    }
}