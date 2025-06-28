import java.io.*
import java.util.*

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}
val apiKey = localProperties.getProperty("API_KEY") ?: ""

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "de.syntax_institut.androidabschlussprojekt"
    compileSdk = 35

    packaging {
        resources {
            excludes += "META-INF/gradle/incremental.annotation.processors"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/notice.txt"
        }
    }

    defaultConfig {
        applicationId = "de.syntax_institut.androidabschlussprojekt"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_KEY", "\"$apiKey\"")
        }
        release {
            isMinifyEnabled = false
            buildConfigField("String", "API_KEY", "\"$apiKey\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {

    implementation(libs.guava)
    api(libs.juneau.marshall)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization)

    implementation (libs.material3)
    implementation (libs.androidx.material3.window.size.class1)

    implementation (libs.adaptive)
    implementation (libs.adaptive.layout)
    implementation (libs.androidx.adaptive.navigation)

    implementation (libs.androidx.ui.text.google.fonts)

    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.moshi)

    implementation(libs.retrofit)
    implementation(libs.converterMoshi)

    implementation(libs.logging.interceptor)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.moshi.kotlin.vmoshiversion)
    implementation(libs.logging.interceptor.vokhttpversion)

    implementation(libs.androidx.datastore.preferences)

    // Paging
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.runtime)
    
    // Paging Testing für End-to-End Tests
    androidTestImplementation(libs.androidx.paging.testing)

    // accompanist-flowlayout
    implementation(libs.accompanist.flowlayout)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Hilt implementation(libs.hilt.android)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Koin
    implementation(libs.insert.koin.koin.android)
    implementation(libs.insert.koin.koin.androidx.compose)

    // Koin für App
    implementation(libs.insert.koin.koin.android)
    implementation(libs.insert.koin.koin.androidx.compose)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.insert.koin.koin.android)
    implementation(libs.insert.koin.koin.androidx.compose)
    implementation(libs.insert.koin.koin.core)

    implementation(libs.compose.shimmer)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.components)
    implementation(libs.play.services.games)

// OkHttp3 Core (für Util-Klassen, LoggingInterceptor etc.)
    implementation(libs.okhttp)

    // MockWebServer für Unit‑Tests
    testImplementation(libs.mockwebserver)

    // Moshi Converter
    testImplementation(libs.converterMoshi)
    testImplementation(libs.logging.interceptor.vokhttpversion)

    // Coroutines Test
    testImplementation(libs.kotlinx.coroutines.test)

    // JUnit
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Koin für Tests
    testImplementation(libs.insert.koin.koin.test)
    testImplementation(libs.koin.test.junit4)
    androidTestImplementation(libs.insert.koin.koin.test)
    androidTestImplementation(libs.koin.test.junit4)

    testImplementation(libs.mockk)
    androidTestImplementation(libs.mockk.android)

// Für Compose UI-Testing
    debugImplementation(libs.androidx.compose.ui.ui.test.manifest)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.compose.ui.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)
    testImplementation(libs.androidx.core)
    testImplementation(libs.robolectric)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)

    testImplementation(libs.retrofit)

}
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}