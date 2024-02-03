import java.net.URI
import java.net.http.HttpRequest

plugins {
    base
}



tasks {
    register("updateVersionsCf") {
        doLast {
            val token = System.getenv("CURSEFORGE_TOKEN") ?: throw RuntimeException("missing CurseForge token")
            val response = jsonRequest(
                    HttpRequest.newBuilder()
                            .uri(URI("https://api.curseforge.com/v1/minecraft/version"))
                            .header("X-Api-Token", token)
                            .build()
            )
            assert(response.statusCode() == 200) { "Invalid status code (${response.statusCode()})" }
            val versions = response.body()["data"].associate {
                it["versionString"].asText() to it["gameVersionId"].asInt()
            }
            project.file("VERSIONS.txt").reader().use { reader ->
                project.file("VERSIONS_CF.txt").writer().use { writer ->
                    reader.useLines { lines ->
                        for (version in lines) {
                            val versionId = versions[version]
                            if (versionId != null) {
                                writer.write("$versionId\n")
                            }
                        }
                    }
                }
            }
        }
    }
}