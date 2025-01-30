plugins {
    alias(libs.plugins.common.android.library)
    alias(libs.plugins.common.android.library.compose)
    alias(libs.plugins.common.android.feature)
}

android {
    namespace = "com.groupec.salesb.feature.home"
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.10")
}