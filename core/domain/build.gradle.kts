plugins {
    alias(libs.plugins.common.android.library)
}

android {
    namespace = "com.groupec.salesb.core.domain"
}

dependencies {

    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    implementation(libs.hilt.android)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.itext7.core)
}
