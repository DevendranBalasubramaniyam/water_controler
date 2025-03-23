plugins {
    // It's generally recommended to keep these lines uncommented to ensure proper plugin application
    id("com.android.application")
    id("com.google.gms.google-services")
    alias(libs.plugins.kotlin.android) // Using the Kotlin Android plugin
}

android {
    namespace = "com.example.water"
    compileSdk = 34 // Target the latest SDK

    defaultConfig {
        applicationId = "com.example.water"
        minSdk = 26 // Minimum SDK version your app will support
        //noinspection OldTargetApi
        targetSdk = 34 // Target the latest SDK
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true // Enables vector drawable support
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Disable code shrinking for release builds
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8" // Set the Kotlin JVM target
    }

    buildFeatures {
        compose = true // Enable Jetpack Compose
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Set Compose compiler extension version
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}" // Exclude certain license files
        }
    }
}

dependencies {
    // Firebase Database
    implementation ("com.google.firebase:firebase-database:20.0.5")

    implementation("androidx.cardview:cardview:1.0.0")





    // Use Firebase BoM for managing Firebase dependencies
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.google.firebase.database.ktx) // Firebase Realtime Database
    implementation ("com.google.firebase:firebase-bom:32.2.0")
    implementation ("com.google.firebase:firebase-database-ktx")

        implementation(libs.kotlin.stdlib) // Latest Kotlin standard library
    implementation("com.github.anastr:speedviewlib:1.5.5")
    // AndroidX libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation ("androidx.appcompat:appcompat:1.7.0")
    implementation(libs.firebase.database)


    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    // Debugging libraries
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
