import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

// See https://github.com/gradle/gradle/issues/15383
val Project.libs get() = the<org.gradle.accessors.dm.LibrariesForLibs>()
