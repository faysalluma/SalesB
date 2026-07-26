plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    implementation(libs.gson)
}

gradlePlugin {
    plugins {
        register("androidLibraryComposeConventionPlugin") {
            id = "gradlePlugins.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidApplicationComposeConventionPlugin")  {
            id = "gradlePlugins.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplicationConventionPlugin") {
            id = "gradlePlugins.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidClientFlavorsConventionPlugin") {
            id = "gradlePlugins.android.application.client-flavors"
            implementationClass = "AndroidClientFlavorsConventionPlugin"
        }
        register("androidLibraryConventionPlugin") {
            id = "gradlePlugins.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "gradlePlugins.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "gradlePlugins.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidFlipper") {
            id = "gradlePlugins.android.flipper"
            implementationClass = "AndroidFlipperConventionPlugin"
        }

    }
}
