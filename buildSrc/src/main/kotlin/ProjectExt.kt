import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskOutputs
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.the
import java.io.File

// See https://github.com/gradle/gradle/issues/15383
val Project.libs get() = the<org.gradle.accessors.dm.LibrariesForLibs>()

val Task.singleFile get() = outputs.files.singleFile

val <T : Task> TaskProvider<T>.singleFile get(): Provider<File> = this.map {
    it.outputs.files.singleFile
}