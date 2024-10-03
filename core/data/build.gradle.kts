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
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")

    // For bcrypt hashing
    implementation(libs.jbcrypt)
}