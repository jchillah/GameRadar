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
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.21"
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Compose

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // Material3 Adaptive
    implementation(libs.androidx.material3.window.size)
    
    // UI Components
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.compose.shimmer)
    implementation(libs.accompanist.flowlayout)
    
    // Data Storage
    implementation(libs.androidx.datastore.preferences)
    
    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // Paging
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.runtime)
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    
    // JSON Serialization
    implementation(libs.moshi)
    implementation(libs.kotlinx.serialization)
    
    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    
    // Dependency Injection
    implementation(libs.insert.koin.koin.android)
    implementation(libs.insert.koin.koin.androidx.compose)
    implementation(libs.insert.koin.koin.core)
    implementation(platform(libs.koin.bom))
    
    // Google Services
    implementation(libs.firebase.components)
    implementation(libs.play.services.games)
    
    // Utilities
    implementation(libs.guava)
    api(libs.juneau.marshall)
    
    // Testing Dependencies
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.core)
    testImplementation(libs.retrofit)
    
    // Android Testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.paging.testing)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    
    // Compose Testing
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}