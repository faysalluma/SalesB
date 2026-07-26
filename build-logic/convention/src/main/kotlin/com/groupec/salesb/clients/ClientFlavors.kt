package com.groupec.salesb.clients

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.google.gson.Gson
import java.io.File

internal data class ClientConfig(
    val id: String,
    val displayName: String,
    val backendId: String,
    val crashReportingEnabled: Boolean,
    val features: ClientFeatures,
    val flavors: List<ClientFlavorConfig>,
)

internal data class ClientFeatures(
    val billing: Boolean,
    val subscriptionScreen: Boolean,
    val exports: Boolean,
    val receiptPrinting: Boolean,
    val quickSignup: Boolean,
    val productLimit: Boolean,
    val saleLimit: Boolean,
    val clientManagement: Boolean,
    val rayonManagement: Boolean,
)

internal data class ClientFlavorConfig(
    val name: String,
    val applicationId: String,
    val businessBuild: Boolean,
    val storeBuild: Boolean,
    val sourceSet: String?,
)

internal fun loadClientConfigs(directory: File): List<ClientConfig> {
    val jsonFiles = directory
        .listFiles { file -> file.extension == "json" }
        .orEmpty()
        .sortedBy(File::getName)

    require(jsonFiles.isNotEmpty()) {
        "No client JSON file found in ${directory.invariantSeparatorsPath}"
    }

    return jsonFiles.map { file ->
        file.reader().use { reader ->
            Gson().fromJson(reader, ClientConfig::class.java)
        }
    }
}

internal fun ApplicationExtension.registerClientFlavors(clients: List<ClientConfig>) {
    flavorDimensions += CLIENT_FLAVOR_DIMENSION

    productFlavors {
        clients.forEach { client ->
            client.flavors.forEach { flavor ->
                create(flavor.name) {
                    applyClientFlavor(client, flavor)
                }
            }
        }
    }

    sourceSets {
        clients.forEach { client ->
            client.flavors.forEach { flavor ->
                flavor.sourceSet?.let { sharedSourceSet ->
                    getByName(flavor.name) {
                        java.srcDirs(
                            "src/$sharedSourceSet/java",
                            "src/$sharedSourceSet/kotlin",
                        )
                        res.srcDirs("src/$sharedSourceSet/res")
                        assets.srcDirs("src/$sharedSourceSet/assets")
                    }
                }
            }
        }
    }
}

private fun ApplicationProductFlavor.applyClientFlavor(
    client: ClientConfig,
    flavor: ClientFlavorConfig,
) {
    dimension = CLIENT_FLAVOR_DIMENSION
    applicationId = flavor.applicationId

    buildConfigString("CLIENT_ID", client.id)
    buildConfigString("CLIENT_DISPLAY_NAME", client.displayName)
    buildConfigString("BACKEND_ID", client.backendId)

    buildConfigBoolean("IS_BUSINESS_BUILD", flavor.businessBuild)
    buildConfigBoolean("IS_STORE_BUILD", flavor.storeBuild)
    buildConfigBoolean("CLIENT_CRASH_REPORTING_ENABLED", client.crashReportingEnabled)

    buildConfigBoolean("FEATURE_BILLING", client.features.billing)
    buildConfigBoolean("FEATURE_SUBSCRIPTION_SCREEN", client.features.subscriptionScreen)
    buildConfigBoolean("FEATURE_EXPORTS", client.features.exports)
    buildConfigBoolean("FEATURE_RECEIPT_PRINTING", client.features.receiptPrinting)
    buildConfigBoolean("FEATURE_QUICK_SIGNUP", client.features.quickSignup)
    buildConfigBoolean("FEATURE_PRODUCT_LIMIT", client.features.productLimit)
    buildConfigBoolean("FEATURE_SALE_LIMIT", client.features.saleLimit)
    buildConfigBoolean("FEATURE_CLIENTS", client.features.clientManagement)
    buildConfigBoolean("FEATURE_RAYONS", client.features.rayonManagement)
}

private fun ApplicationProductFlavor.buildConfigString(name: String, value: String) {
    buildConfigField("String", name, value.asBuildConfigString())
}

private fun ApplicationProductFlavor.buildConfigBoolean(name: String, value: Boolean) {
    buildConfigField("boolean", name, value.toString())
}

private fun String.asBuildConfigString(): String =
    "\"${replace("\\", "\\\\").replace("\"", "\\\"")}\""

private const val CLIENT_FLAVOR_DIMENSION = "edition"
