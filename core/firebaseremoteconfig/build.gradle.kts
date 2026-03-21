plugins {
    alias(libs.plugins.common.android.library)
    alias(libs.plugins.common.android.hilt)
}

android {
    namespace = "com.groupec.core.firebaseremoteconfig"
}

dependencies {
    // Firebase remote config
    implementation(libs.firebase.config)
}