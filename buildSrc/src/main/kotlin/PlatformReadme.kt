import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import java.io.StringWriter

interface PlatformReadmeExtension {
    val contents: Property<String>
}

class PlatformReadmePlugin : Plugin<Project> {
    private val PLATFORM_START = "<!-- platform.start -->"
    private val PLATFORM_END = "<!-- platform.end -->"

    private fun generatePlatformReadme(project: Project): String {
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

    override fun apply(p: Project) {
        val ext = p.extensions.create<PlatformReadmeExtension>("platformReadme")
        ext.contents.set(p.provider { generatePlatformReadme(p) })
    }
}