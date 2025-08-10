plugins {
    alias(libs.plugins.common.android.library)
    alias(libs.plugins.common.android.library.compose)
    alias(libs.plugins.common.android.feature)
}

android {
    namespace = "com.groupec.feature.salelist"
}

dependencies {
    implementation(project(":core:print"))
}