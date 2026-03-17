plugins {
    alias(libs.plugins.common.android.library)
    alias(libs.plugins.common.android.library.compose)
}

android {
    namespace = "com.groupec.salesb.core.ui"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:print"))

    // Jetpack compose charts
    implementation(libs.compose.charts)
    implementation(libs.androidx.hilt.navigation.compose)
}
