import com.groupec.salesb.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFlipperConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                "debugImplementation" (libs.findLibrary("facebook.flipper").get())
                "debugImplementation" (libs.findLibrary("facebook.soloader").get())
                "releaseImplementation" (libs.findLibrary("facebook.flipper.noop").get())
                "debugImplementation" (libs.findLibrary("facebook.flipper.network").get())
            }
        }
    }

}
