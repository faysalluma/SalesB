import com.android.build.gradle.LibraryExtension
import com.groupec.salesb.configureKotlinAndroid
import com.groupec.salesb.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            with(project.pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig { consumerProguardFiles("consumer-rules.pro") }
            }

            dependencies {
                add("implementation", libs.findLibrary("androidx.paging.compose").get())
                add("implementation", libs.findLibrary("androidx.paging.runtime").get())
                add("testImplementation", libs.findLibrary("junit5").get())
                add("testImplementation", libs.findLibrary("mockk").get())
                add("testImplementation", libs.findLibrary("kotlinx.coroutines.test").get())
                add("androidTestImplementation", libs.findLibrary("androidx.espresso.core").get())
            }

            tasks.withType<Test> {
                useJUnitPlatform()
            }
        }
    }
}
