import com.android.build.api.dsl.ApplicationExtension
import com.groupec.salesb.configureKotlinAndroid
import com.groupec.salesb.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            with(project.pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("gradlePlugins.android.flipper")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
            }

            dependencies {
                add("testImplementation", libs.findLibrary("junit5").get())
            }

            tasks.withType<Test> {
                useJUnitPlatform()
            }
        }
    }
}
