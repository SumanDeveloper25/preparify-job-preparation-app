plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.preparify_jobpreparationapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.preparify_jobpreparationapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    dataBinding {
        enable = true
    }

    viewBinding {
        enable = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Firebase BoM
    implementation(platform(libs.firebase.bom))
    // Firebase Auth
    implementation(libs.firebase.auth)
    implementation(libs.firebase.auth.ktx) // or the latest version
    // Firebase Fire store
    implementation(libs.firebase.firestore.ktx)

    // ROOM
    implementation(libs.androidx.room.runtime)
//    implementation(libs.mediation.test.suite)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Google Sign-In
    implementation(libs.play.services.auth) // Use the latest version

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Facebook Auth
    implementation(libs.facebook.android.sdk)

    // ViewModel and LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    // picasso for image
    implementation (libs.picasso)
    implementation (libs.lottie)
    // card
    implementation(libs.cardview)
    implementation(libs.glide)

}
