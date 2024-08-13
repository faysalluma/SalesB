plugins {
    alias(libs.plugins.common.android.library)
    id("kotlin-parcelize")

}

android {
    namespace = "com.groupec.salesb.core.model"
}

dependencies {
    implementation(libs.retrofit.converterGson)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime)
}