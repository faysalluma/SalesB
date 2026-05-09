import com.android.build.api.dsl.ApplicationProductFlavor
import java.util.Properties

plugins {
    alias(libs.plugins.common.android.application)
    alias(libs.plugins.common.android.application.compose)
    alias(libs.plugins.common.android.hilt)

    // Make sure that you have the Google services Gradle plugin
    alias(libs.plugins.google.services)
    // Add the Crashlytics Gradle plugin
    alias(libs.plugins.firebase.crashlytics)
}

// val vcode = (((System.currentTimeMillis() / 1000) - 1451606400) / 10).toInt()

private data class ClientFeatures(
    val billingEnabled: Boolean,
    val subscriptionScreenEnabled: Boolean,
    val exportEnabled: Boolean,
    val receiptPrintingEnabled: Boolean,
    val productLimitEnabled: Boolean,
    val saleLimitEnabled: Boolean,
    val clientManagementEnabled: Boolean,
    val rayonManagementEnabled: Boolean,
)

private data class ClientFlavorConfig(
    val name: String,
    val applicationId: String,
    val displayName: String,
    val releaseServerUrl: String,
    val businessBuild: Boolean,
    val storeBuild: Boolean,
    val crashReportingEnabled: Boolean,
    val features: ClientFeatures,
)

private val defaultClientFeatures = ClientFeatures(
    billingEnabled = false,
    subscriptionScreenEnabled = false,
    exportEnabled = true,
    receiptPrintingEnabled = true,
    productLimitEnabled = false,
    saleLimitEnabled = false,
    clientManagementEnabled = true,
    rayonManagementEnabled = true,
)

private data class ClientConfig(
    val name: String,
    val applicationId: String,
    val storeApplicationId: String? = null,
    val displayName: String,
    val releaseServerUrl: String,
    val storeReleaseServerUrl: String = releaseServerUrl,
    val crashReportingEnabled: Boolean = true,
    val features: ClientFeatures = defaultClientFeatures,
)

private val salesbFlavor = ClientFlavorConfig(
    name = "salesb",
    applicationId = "com.groupec.salesb",
    displayName = "SalesB",
    releaseServerUrl = "https://salesbstoreapi.groupec.net/",
    businessBuild = false,
    storeBuild = true,
    crashReportingEnabled = true,
    features = ClientFeatures(
        billingEnabled = true,
        subscriptionScreenEnabled = true,
        exportEnabled = false,
        receiptPrintingEnabled = false,
        productLimitEnabled = true,
        saleLimitEnabled = true,
        clientManagementEnabled = true,
        rayonManagementEnabled = true,
    ),
)

private val clients = listOf<ClientConfig>(
    /*
    ClientConfig(
        name = "nomClient",
        applicationId = "com.groupec.salesb.nomclient",
        storeApplicationId = "com.groupec.salesb.nomclient.store",
        displayName = "Nom Client",
        releaseServerUrl = "https://api.nomclient.example/",
    ),
    */
)

private fun String.asBuildConfigString(): String =
    "\"${replace("\\", "\\\\").replace("\"", "\\\"")}\""

private fun ApplicationProductFlavor.buildConfigString(name: String, value: String) {
    buildConfigField("String", name, value.asBuildConfigString())
}

private fun ApplicationProductFlavor.buildConfigBoolean(name: String, value: Boolean) {
    buildConfigField("boolean", name, value.toString())
}

private fun ApplicationProductFlavor.applyClientFlavor(config: ClientFlavorConfig) {
    dimension = "edition"
    applicationId = config.applicationId

    buildConfigString("CLIENT_ID", config.name)
    buildConfigString("CLIENT_DISPLAY_NAME", config.displayName)
    buildConfigString("RELEASE_SERVER_URL", config.releaseServerUrl)

    buildConfigBoolean("IS_BUSINESS_BUILD", config.businessBuild)
    buildConfigBoolean("IS_STORE_BUILD", config.storeBuild)
    buildConfigBoolean("CLIENT_CRASH_REPORTING_ENABLED", config.crashReportingEnabled)

    buildConfigBoolean("FEATURE_BILLING", config.features.billingEnabled)
    buildConfigBoolean("FEATURE_SUBSCRIPTION_SCREEN", config.features.subscriptionScreenEnabled)
    buildConfigBoolean("FEATURE_EXPORTS", config.features.exportEnabled)
    buildConfigBoolean("FEATURE_RECEIPT_PRINTING", config.features.receiptPrintingEnabled)
    buildConfigBoolean("FEATURE_PRODUCT_LIMIT", config.features.productLimitEnabled)
    buildConfigBoolean("FEATURE_SALE_LIMIT", config.features.saleLimitEnabled)
    buildConfigBoolean("FEATURE_CLIENTS", config.features.clientManagementEnabled)
    buildConfigBoolean("FEATURE_RAYONS", config.features.rayonManagementEnabled)
}

private fun ClientConfig.toPrivateFlavor() = ClientFlavorConfig(
    name = name,
    applicationId = applicationId,
    displayName = displayName,
    releaseServerUrl = releaseServerUrl,
    businessBuild = true,
    storeBuild = false,
    crashReportingEnabled = crashReportingEnabled,
    features = features,
)

private fun ClientConfig.toStoreFlavor() = storeApplicationId?.let { storeApplicationId ->
    ClientFlavorConfig(
        name = "${name}Store",
        applicationId = storeApplicationId,
        displayName = displayName,
        releaseServerUrl = storeReleaseServerUrl,
        businessBuild = true,
        storeBuild = true,
        crashReportingEnabled = crashReportingEnabled,
        features = features,
    )
}

private val appFlavors =
    listOf(salesbFlavor) + clients.flatMap { client ->
        listOfNotNull(client.toPrivateFlavor(), client.toStoreFlavor())
    }

android {
    namespace = "com.groupec.salesb"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.groupec.salesb"
        targetSdk = libs.versions.compileSdk.get().toInt()
        versionCode = 11
        versionName = "1.1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val properties = Properties().apply {
                load(project.rootProject.file("local.properties").inputStream())
            }
            storeFile = file(properties.getProperty("RELEASE_STORE_FILE"))
            storePassword = properties.getProperty("RELEASE_STORE_PASSWORD")
            keyAlias = properties.getProperty("RELEASE_KEY_ALIAS")
            keyPassword = properties.getProperty("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {

        debug {
            // Definies config data
            buildConfigField("boolean", "ENABLE_CRASH_REPORTING", "false")
            buildConfigField("String", "DEBUG_SERVER_URL", "\"http://192.168.1.69/SalesBStoreApi/\"")
            buildConfigField("int", "NETWORK_TIMEOUT_SECONDS", "30")
            manifestPlaceholders["usesCleartextTraffic"] = "true"
        }

        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            // Definies config data https://salesbstoreapi.groupec.net/
            buildConfigField("boolean", "ENABLE_CRASH_REPORTING", "true")
            buildConfigField("String", "DEBUG_SERVER_URL", "\"\"")
            buildConfigField("int", "NETWORK_TIMEOUT_SECONDS", "30")
            manifestPlaceholders["usesCleartextTraffic"] = "false"
        }
    }

    flavorDimensions += "edition"

    productFlavors {
        appFlavors.forEach { config ->
            create(config.name) {
                applyClientFlavor(config)
            }
        }
    }

    sourceSets {
        clients
            .filter { it.storeApplicationId != null }
            .forEach { client ->
                getByName("${client.name}Store") {
                    java.srcDirs(
                        "src/${client.name}/java",
                        "src/${client.name}/kotlin",
                    )
                    res.srcDirs("src/${client.name}/res")
                    assets.srcDirs("src/${client.name}/assets")
                }
            }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":feature:home"))
    implementation(project(":feature:login"))
    implementation(project(":feature:loading"))
    implementation(project(":feature:changepassword"))
    implementation(project(":feature:productlist"))
    implementation(project(":feature:productdetail"))
    implementation(project(":feature:sale"))
    implementation(project(":feature:salelist"))
    implementation(project(":feature:salechart"))
    implementation(project(":feature:forgotpassword"))
    implementation(project(":feature:categorylist"))
    implementation(project(":feature:categorydetail"))
    implementation(project(":feature:clientlist"))
    implementation(project(":feature:clientdetail"))
    implementation(project(":feature:rayonlist"))
    implementation(project(":feature:rayondetail"))
    implementation(project(":feature:outputlist"))
    implementation(project(":feature:outputdetail"))
    implementation(project(":feature:accountlist"))
    implementation(project(":feature:accountdetail"))
    implementation(project(":feature:termsandconditions"))
    implementation(project(":feature:handleservice"))
    implementation(project(":feature:signup"))
    implementation(project(":feature:subscription"))
    implementation(project(":core:config"))
    implementation(project(":feature:printreceiptguide"))
    implementation(project(":feature:faq"))
    implementation(project(":feature:updatebusinessinfo"))

    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:datastore"))
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:testing"))
    implementation(project(":core:domain"))
    implementation(project(":core:print"))
    implementation(project(":core:firebaseremoteconfig"))
    implementation(project(":core:googlebilling"))

    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.retrofit.converterGson)

    ksp(libs.hilt.compiler)
    kspTest(libs.hilt.compiler)
    testImplementation(libs.hilt.android.testing)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.hilt.android.testing)

    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))

    // Add the dependencies for the Crashlytics and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
}
