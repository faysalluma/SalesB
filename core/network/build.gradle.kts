
plugins {
    alias(libs.plugins.common.android.library)
    alias(libs.plugins.common.android.hilt)
    alias(libs.plugins.common.android.flipper)
}

android {
    namespace = "com.groupec.salesb.core.network"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    implementation(libs.coil.kt.compose)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converterScalars)
    implementation(libs.retrofit.converterGson)
    implementation(libs.retrofit.converterAdapter)
    implementation(libs.retrofit.kotlin.serialization)


    testImplementation(libs.kotlinx.coroutines.test)
}