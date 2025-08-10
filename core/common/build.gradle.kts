plugins {
    alias(libs.plugins.common.android.library)
    alias(libs.plugins.common.android.hilt)
}

android {
    namespace = "com.groupec.salesb.core"
}

dependencies {
    // Upload
    implementation(libs.commons.net)

    // Convert decimal amount into capital letter
    implementation(libs.icu4j)

    // Test
    testImplementation(libs.kotlinx.coroutines.test)
}