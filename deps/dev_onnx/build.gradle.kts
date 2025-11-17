plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {

    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {


        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    namespace ="com.engineer.oxnn"
//    aidlPackagedList = mutableListOf("com.engineer.android.mini.ipc.aidl")
}

dependencies {
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.7.0")

    implementation("com.microsoft.onnxruntime:onnxruntime-android:1.17.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
}
apply(from = "../../custom-gradle/coroutines-dep.gradle")
