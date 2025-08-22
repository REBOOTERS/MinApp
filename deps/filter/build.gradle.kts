plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}


android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        vectorDrawables {
            useSupportLibrary=  true
        }
        // Switching to Renderscript support provided by framework.

        renderscriptTargetApi = 30
        renderscriptSupportModeEnabled = true

        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    namespace ="com.example.background.filter"
}

dependencies {

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

//    implementation deps.coroutines.android
//    implementation deps.glide.runtime

    implementation("androidx.work:work-runtime-ktx:2.10.0")




}
