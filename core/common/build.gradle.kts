plugins {
    alias(libs.plugins.common.android.library)
    alias(libs.plugins.common.android.hilt)
}

android {
    namespace = "com.groupec.salesb.common"
}

dependencies {


    // Test
    testImplementation(libs.kotlinx.coroutines.test)
}