plugins {
    alias(libs.plugins.common.android.library)
    alias(libs.plugins.common.android.hilt)
}

android {
    namespace = "com.groupec.salesb.core.datastore"
}

dependencies {

    // Modules calls
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    // DataStore
    implementation(libs.androidx.dataStore.core)
    implementation(libs.androidx.dataStore.preferences)

    // Test
    testImplementation(libs.kotlinx.coroutines.test)
}