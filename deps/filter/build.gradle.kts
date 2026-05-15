plugins {
    alias(libs.plugins.android.library)
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
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
//    kotlinOptions {
//        jvmTarget = "21"
//    }
    namespace ="com.example.background.filter"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)

//    implementation deps.coroutines.android
//    implementation deps.glide.runtime

    implementation(libs.androidx.work.runtime.ktx)




}
