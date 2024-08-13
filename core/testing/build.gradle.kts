plugins {
    alias(libs.plugins.common.android.library)
    alias(libs.plugins.common.android.library.compose)
    alias(libs.plugins.common.android.hilt)
}

android {
    namespace = "com.groupec.salesb.core.testing"
}

dependencies {

    api(kotlin("test"))
    implementation(libs.hilt.android.testing)
    implementation(libs.kotlinx.coroutines.test)
}