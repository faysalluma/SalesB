plugins {
    alias(libs.plugins.common.android.library)
    alias(libs.plugins.common.android.hilt)
}

android {
    namespace = "com.groupec.salesb.core.googlebilling"
}

dependencies {
    implementation(libs.google.play.billing)
    implementation(libs.kotlinx.coroutines.android)
}
