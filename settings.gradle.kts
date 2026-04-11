import org.gradle.kotlin.dsl.project

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "SalesB"
include(":app")
include(":core:common")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:designsystem")
include(":core:domain")
include(":core:model")
include(":core:network")
include(":core:testing")
include(":core:ui")
include(":feature:home")
include(":feature:login")
include(":feature:configuration")
include(":feature:loading")
include(":feature:changepassword")
include(":feature:productlist")
include(":feature:productdetail")
include(":feature:sale")
include(":feature:salelist")
include(":feature:salechart")
include(":feature:forgotpassword")
include(":feature:categorydetail")
include(":feature:categorylist")
include(":feature:clientdetail")
include(":feature:clientlist")
include(":feature:rayonlist")
include(":feature:rayondetail")
include(":feature:outputdetail")
include(":feature:outputlist")
include(":core:print")
include(":feature:editinvoicing")
include(":feature:accountlist")
include(":feature:accountdetail")
include(":feature:termsandconditions")
include(":core:firebaseremoteconfig")
include(":core:googlebilling")
include(":core:config")

include(":feature:handleservice")
include(":feature:signup")
include(":feature:subscription")
include(":feature:printreceiptguide")
include(":feature:faq")
include(":feature:updatebusinessinfo")
