pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://maven.pkg.github.com/jan-tennert/supabase") {
            credentials {
                username = "jan-tennert"
                password = ""
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.pkg.github.com/jan-tennert/supabase") {
            credentials {
                username = "jan-tennert"
                password = ""
            }
        }
    }
}

rootProject.name = "periody"
include(":app")
