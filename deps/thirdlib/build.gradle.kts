plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {

    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {


        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cppFlags.add(" ")
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    externalNativeBuild {
        cmake {
            path =file("src/main/cpp/CMakeLists.txt")
            version ="3.22.1"
        }
    }
    namespace ="com.engineer.gif.thirdlib"
    ndkVersion ="25.2.9519653"
    buildFeatures {
        aidl= true
    }
}

dependencies {
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    compileOnly("com.squareup.radiography:radiography:2.7")
    implementation("androidx.appcompat:appcompat:1.7.0")
    api("com.google.android.material:material:1.12.0")
}

apply(from="publish.gradle") // publish-self-aar