import org.gradle.api.Project
import java.io.StringWriter

private val PLATFORM_START = "<!-- platform.start -->"
private val PLATFORM_END = "<!-- platform.end -->"

fun generatePlatformReadme(project: Project): String {
    val writer = StringWriter()
    project.rootProject.file("README.md").reader().use { reader ->
        reader.useLines { lines ->
            var blockAtLineIndex = -1
            for (indexed in lines.withIndex()) {
                val lineIndex = indexed.index
                val line = indexed.value
                if (line == PLATFORM_START) {
                    blockAtLineIndex = lineIndex
                    continue
                }
                if (line == PLATFORM_END) {
                    blockAtLineIndex = -1
                    continue
                }
                if (blockAtLineIndex > 0) {
                    continue
                }
                writer.write(line)
                writer.write("\n")
            }
            if (blockAtLineIndex > 0) {
                throw IllegalArgumentException("platform.start block was never closed " +
                        "(see line ${blockAtLineIndex + 1})")
            }
        }
    }
    return writer.toString()
}