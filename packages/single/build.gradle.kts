import net.swiftzer.semver.SemVer

plugins {
    id("java-shadow")
    id("com.modrinth.minotaur")
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
    assemble {
        if (System.getenv("MODRINTH_UPLOAD") == "true") {
            dependsOn("modrinth")
        }
    }
}