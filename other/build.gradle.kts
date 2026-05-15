plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.tools.ksp)
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.engineer.other"
        minSdk = libs.versions.minSdk.get().toInt()
        
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(project.findProperty("MYAPP_RELEASE_STORE_FILE") as String)
            storePassword = project.findProperty("MYAPP_RELEASE_STORE_PASSWORD") as String
            keyAlias = project.findProperty("MYAPP_RELEASE_KEY_ALIAS") as String
            keyPassword = project.findProperty("MYAPP_RELEASE_KEY_PASSWORD") as String
        }

        getByName("debug") {
            storeFile = file(project.findProperty("MYAPP_RELEASE_STORE_FILE") as String)
            storePassword = project.findProperty("MYAPP_RELEASE_STORE_PASSWORD") as String
            keyAlias = project.findProperty("MYAPP_RELEASE_KEY_ALIAS") as String
            keyPassword = project.findProperty("MYAPP_RELEASE_KEY_PASSWORD") as String
        }
    }

    buildTypes {
        release {
            //混淆
            isMinifyEnabled = true
            //所以尽可能的减少第三方的使用 也是可以降低混淆的难度
            // 移除无用的resource文件
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
//    kotlinOptions {
//        jvmTarget = "21"
//    }
    namespace = "com.engineer.other"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(project(":deps:common"))
    implementation(project(":deps:thirdlib"))

}

apply(from = "../custom-gradle/test-dep.gradle") // unit test
apply(from = "../custom-gradle/viewmodel-dep.gradle") // view-model-lifecycle 全家桶