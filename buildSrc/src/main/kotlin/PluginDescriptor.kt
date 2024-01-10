import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File

enum class PluginDescriptorFormat {
    YAML, JSON
}

abstract class PluginDescriptorTask : DefaultTask() {

    @get:Input
    @get:Optional
    abstract val descriptor: Property<String>

    @get:InputFile
    @get:Optional
    abstract val descriptorFile: RegularFileProperty

    @get:Input
    abstract val content: MapProperty<String, Any>

    @get:Input
    @get:Optional
    abstract val format: Property<PluginDescriptorFormat>

    @get:Input
    @get:Optional
    abstract val append: Property<Boolean>

    init {
        project.tasks.getByName("processResources") {
            finalizedBy(this@PluginDescriptorTask)
        }
    }

    @TaskAction
    fun writeFile() {
        val mapper = constructMapper()
        mapper.registerKotlinModule()
        val file = extractDescriptorFile()
        val fileContent = LinkedHashMap<String, Any>()
        if (append.getOrElse(false)) {
            fileContent.putAll(mapper.readValue<Map<String, Any>>(file))
        }
        fileContent.putAll(content.get())
        mapper.writeValue(
                file,
                fileContent,
        )
    }

    private fun constructMapper(): ObjectMapper {
        @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
        return ObjectMapper(when(format.getOrElse(PluginDescriptorFormat.YAML)) {
            PluginDescriptorFormat.JSON -> JsonFactory()
            PluginDescriptorFormat.YAML -> YAMLFactory()
        })
    }

    private fun extractDescriptorFile(): File {
        var file: File? = descriptorFile.map { it.asFile }.orNull
        if (file == null) {
            val sourceSets: SourceSetContainer = project.extensions.getByName("sourceSets") as SourceSetContainer
            file = sourceSets.getByName("main").output.resourcesDir!!.resolve(descriptor.get())
        }
        return file
    }
}