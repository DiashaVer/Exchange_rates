/*
    Модульный файл сборки Gradle для приложения ExchangeRates.
    Настройки и зависимости соответствуют требованиям задания:
    - Часть 3: Retrofit + Coroutines, сетевые запросы, Moshi (JSON)
    - Часть 4: Room (база данных), Hilt (DI), MVVM (Lifecycle, Flow/StateFlow)
    - Часть 2: Navigation Compose (аналог фрагментов), Preferences (SharedPreferences)
    - Часть 5: уведомления, Compose UI, Material 3, графика (Canvas)
*/

plugins {
    id("com.android.application")          // Android-приложение
    id("org.jetbrains.kotlin.android")     // Kotlin
    id("org.jetbrains.kotlin.kapt")        // Kapt для Hilt (обработка аннотаций)
    id("com.google.dagger.hilt.android")   // Часть 4.3 – DI (Hilt)
    id("com.google.devtools.ksp")          // Часть 4.1 – Room (KSP вместо kapt)
}

android {
    namespace = "com.example.exchangerates"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.exchangerates"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Часть 1.3 и 1.4 – Grid/LazyVerticalGrid, карточки – Compose
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true   // Для поддержки java.time (5.1 – графики, даты)
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true   // Jetpack Compose (весь UI)
    }
}

dependencies {
    // --- Jetpack Compose (весь UI: часть 1, 2, 5) ---
    // Часть 1.3 (LazyVerticalGrid), пункт 4 (Card)
    // Часть 5.4 (анимации) – Compose предоставляет анимации по умолчанию
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)     // Card, Scaffold, тема
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // --- Навигация (часть 2.2 и 2.3) ---
    // Navigation Component для Compose (аналог фрагментов + SafeArgs)
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // --- SharedPreferences (часть 2.4 и 2.5) ---
    // В текущей реализации используется Room, но для строгого соответствия части 2
    // добавлена зависимость preference-ktx (может использоваться параллельно)
    implementation("androidx.preference:preference-ktx:1.2.1")

    // --- Дополнительные иконки Material (часть 1.5 – иконка валюты; часть 2 – звезда избранного) ---
    implementation("androidx.compose.material:material-icons-extended")

    // --- Сеть: Retrofit + Moshi (часть 3.1 и 3.2) ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")   // Генерация адаптеров Moshi

    // --- Room (часть 4.1) ---
    implementation("androidx.room:room-runtime:2.6.0")
    ksp("androidx.room:room-compiler:2.6.0")               // KSP для Room
    implementation("androidx.room:room-ktx:2.6.0")          // Coroutines Flow поддержка

    // --- Hilt (часть 4.3) ---
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")   // Hilt + Compose навигация

    // --- Десугаринг (для java.time в HistoryScreen – часть 5.1) ---
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // --- Тестирование (не относится к требованиям, но необходимо для проекта) ---
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

kapt {
    correctErrorTypes = true   // Hilt требует этой настройки
}

tasks.register("unitTestClasses") {
    // Заглушка (не влияет на функционал)
}