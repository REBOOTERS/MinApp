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
    kotlinOptions {
        jvmTarget = "21"
    }
    namespace = "com.engineer.common"
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:$1.16.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.12.0")

    api("com.github.weijiaxing:LogcatViewer:1.0.3")
    api("jp.wasabeef:blurry:4.0.1")
    api("com.squareup.radiography:radiography:2.4.1")
    api("commons-io:commons-io:2.11.0")
    implementation("androidx.exifinterface:exifinterface:1.3.6")
    api("com.guolindev.permissionx:permissionx:1.7.1")

    api("io.github.scwang90:refresh-layout-kernel:2.0.6")      //核心必须依赖
    api("io.github.scwang90:refresh-header-classics:2.0.6")    //经典刷新头
    api("io.github.scwang90:refresh-header-material:2.0.6")    //谷歌刷新头
    api("io.github.scwang90:refresh-footer-classics:2.0.6")    //经典加载

    api("org.jsoup:jsoup:1.17.2")
}

apply(from = "../../custom-gradle/test-dep.gradle") // unit test
apply(from = "../../custom-gradle/rx-retrofit-dep.gradle") // rxjava retrofit gson 网络全家桶