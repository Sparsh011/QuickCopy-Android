plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.sparshchadha.clipboard.image"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sparshchadha.clipboard.image"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

        implementation ("androidx.appcompat:appcompat:1.6.1")
        implementation ("androidx.core:core-ktx:1.10.1")
        implementation ("org.jsoup:jsoup:1.15.4") // For parsing HTML to get images from links
        implementation ("com.squareup.okhttp3:okhttp:4.10.0") // For downloading image from URL
        implementation ("com.github.bumptech.glide:glide:4.15.1") // Use the latest version
        kapt ("com.github.bumptech.glide:compiler:4.15.1")
}