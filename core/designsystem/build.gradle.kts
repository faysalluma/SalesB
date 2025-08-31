plugins {
    alias(libs.plugins.common.android.library)
    alias(libs.plugins.common.android.library.compose)
}

android {
    namespace = "com.groupec.salesb.core.designsystem"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.androidx.material.icons.extended)
}