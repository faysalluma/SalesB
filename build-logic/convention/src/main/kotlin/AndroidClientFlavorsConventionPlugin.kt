import com.android.build.api.dsl.ApplicationExtension
import com.groupec.salesb.clients.loadClientConfigs
import com.groupec.salesb.clients.registerClientFlavors
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidClientFlavorsConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.withPlugin("com.android.application") {
            val clientsDirectory = project.layout.projectDirectory.dir("clients").asFile
            val clients = loadClientConfigs(clientsDirectory)

            project.extensions.configure<ApplicationExtension> {
                registerClientFlavors(clients)
            }
        }
    }
}
