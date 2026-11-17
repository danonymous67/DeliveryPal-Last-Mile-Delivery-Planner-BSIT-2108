plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.deliverypal"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.deliverypal"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Mapsforge Core
    implementation ("org.mapsforge:mapsforge-core:0.23.0")
    implementation ("org.mapsforge:mapsforge-map:0.23.0")
    implementation ("org.mapsforge:mapsforge-map-reader:0.23.0")
    implementation ("org.mapsforge:mapsforge-themes:0.23.0")

// Android dependencies for Mapsforge
    implementation ("org.mapsforge:mapsforge-map-android:0.23.0")
    implementation ("com.caverock:androidsvg:1.4")

    // Mapsforge POI core (OPTIONAL)
    implementation ("org.mapsforge:mapsforge-poi:0.23.0")

    // Mapsforge POI android specific (OPTIONAL)
    implementation ("org.mapsforge:mapsforge-poi-android:0.23.0")

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}