import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*

abstract class PlatformReadmeTask : DefaultTask() {
    companion object {
        val PLATFORM_START = "<!-- platform.start -->"
        val PLATFORM_END = "<!-- platform.end -->"
    }

    @get:InputFile
    @get:Optional
    abstract val inputFile: RegularFileProperty

    @get:OutputFile
    @get:Optional
    abstract val outputFile: RegularFileProperty

    init {
        inputFile.convention(project.objects.fileProperty().fileValue(
                project.rootProject.file("README.md"))
        )
        outputFile.convention(project.objects.fileProperty().value(
                project.layout.buildDirectory.file("README-platform.md"))
        )
    }

    @TaskAction
    fun writeFile() {
        inputFile.get().asFile.reader().use { reader ->
            outputFile.get().asFile.writer().use { writer ->
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
        }
    }
}