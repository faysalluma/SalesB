plugins {
    alias(libs.plugins.common.android.library)
    alias(libs.plugins.common.android.hilt)
}

android {
    namespace = "com.groupec.salesb.core.data"
}

dependencies {

    // Modules calls
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:network"))
    implementation(project(":core:testing"))
    implementation(project(":core:model"))


    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime)

    // Retrofit
    implementation(libs.retrofit.converterGson)
    implementation (libs.retrofit.core)
    implementation (libs.okhttp.core)
    implementation(libs.okhttp.logging)

    // For bcrypt hashing
    implementation(libs.jbcrypt)
}