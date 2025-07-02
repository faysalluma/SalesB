plugins {
    alias(libs.plugins.common.android.library)
}

android {
    namespace = "com.groupec.salesb.core.print"
}

dependencies {

    implementation(project(":core:model"))
    implementation(project(":core:common"))

    // Thermal print
    implementation(libs.escpos.thermalprinter.android)
    implementation(libs.accompanist.permissions)
}