pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        plugins {
            id("com.android.application") version "8.8.0" apply false
            id("org.jetbrains.kotlin.android") version "1.9.20" apply false
            id("org.jetbrains.kotlin.kapt") version "1.9.20" apply false

            // ОБНОВИТЕ ЭТУ СТРОКУ:
            id("com.google.dagger.hilt.android") version "2.50" apply false

            id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
        }

    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Exchange rates"
include(":app")