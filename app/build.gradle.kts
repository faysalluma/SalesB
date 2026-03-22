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

val vcode = (((System.currentTimeMillis() / 1000) - 1451606400) / 10).toInt()

android {
    namespace = "com.groupec.salesb"

    defaultConfig {
        applicationId = "com.groupec.salesb"
        targetSdk = libs.versions.compileSdk.get().toInt()
        versionCode = vcode
        versionName = libs.versions.versionName.get()

        vectorDrawables {
            useSupportLibrary = true
        }
        buildFeatures {
            buildConfig = true
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
            buildConfigField("String", "SERVER_URL", "\"http://192.168.1.69/SalesBApi/\"")
            buildConfigField("int", "NETWORK_TIMEOUT_SECONDS", "30")
        }

        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")

            // Definies config data
            buildConfigField("boolean", "ENABLE_CRASH_REPORTING", "true")
            buildConfigField("String", "SERVER_URL", "\"https://salesbapi.groupec.net/\"")
            buildConfigField("int", "NETWORK_TIMEOUT_SECONDS", "30")
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
    implementation(project(":feature:configuration"))
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
    implementation(project(":feature:rayonlist"))
    implementation(project(":feature:rayondetail"))
    implementation(project(":feature:outputlist"))
    implementation(project(":feature:outputdetail"))
    implementation(project(":feature:accountlist"))
    implementation(project(":feature:accountdetail"))
    implementation(project(":feature:termsandconditions"))
    implementation(project(":feature:handleservice"))
    implementation(project(":core:config"))

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
