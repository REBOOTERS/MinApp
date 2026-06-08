import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.tools.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.protobuf)
}

val buildTime: String = SimpleDateFormat("yyMMddHHmm").format(Date())
val apiKey: String = project.findProperty("API_KEY") as String
val sourceCodeValue: String = project.findProperty("source_code") as String
val sourceCode: Boolean = sourceCodeValue == "true"

class RoomSchemaArgProvider(
    @get:InputDirectory @get:PathSensitive(PathSensitivity.RELATIVE) val schemaDir: File
) : CommandLineArgumentProvider {

    override fun asArguments(): Iterable<String> {
        // Note: If you're using KAPT and javac, change the line below to
        // return listOf("-Aroom.schemaLocation=${schemaDir.path}").
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}

android {
    namespace = "com.engineer.android.mini"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.engineer.android.mini"
        minSdk = 24
        
        versionCode = 1
        versionName = "1.0.0_$buildTime"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("Boolean", "enable_log", "false")
        buildConfigField("String", "secret_id", "\"123456\"")
        buildConfigField("String", "api_key", "\"${apiKey}\"")
//        manifestPlaceholders.activity_exported = true
        manifestPlaceholders["max_aspect"] = 3
        manifestPlaceholders["extract_native_libs"] = true
        manifestPlaceholders["activity_exported"] = true


        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += setOf("arm64-v8a")
        }
    }
    androidResources {
        localeFilters += listOf("zh-rCN")
    }
    ksp {
        arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
    }

    signingConfigs {
        create("release") {
            storeFile = file(project.findProperty("MYAPP_RELEASE_STORE_FILE") as String)
            storePassword = project.findProperty("MYAPP_RELEASE_STORE_PASSWORD") as String
            keyAlias = project.findProperty("MYAPP_RELEASE_KEY_ALIAS") as String
            keyPassword = project.findProperty("MYAPP_RELEASE_KEY_PASSWORD") as String
        }
        this.getByName("debug") {
            storeFile = file(project.findProperty("MYAPP_RELEASE_STORE_FILE") as String)
            storePassword = project.findProperty("MYAPP_RELEASE_STORE_PASSWORD") as String
            keyAlias = project.findProperty("MYAPP_RELEASE_KEY_ALIAS") as String
            keyPassword = project.findProperty("MYAPP_RELEASE_KEY_PASSWORD") as String
        }
    }


    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.findByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
//    kotlinOptions {
//        jvmTarget = "21"
//    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
        aidl = true
        buildConfig = true
        compose = true
    }
    lint {
        checkReleaseBuilds = false
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            useLegacyPackaging = false
        }
    }
    sourceSets {
        create("local") {
            manifest.srcFile("src/local/AndroidManifest.xml")
        }
    }

//    flavorDimensions.add("channel")
    flavorDimensions.add("type")
    productFlavors {
//        create("xiaomi") { dimension = "channel" }
//        create("oppo") { dimension = "channel" }
//        create("huawei") { dimension = "channel" }
        create("local") {

            dimension = "type"
        }
        create("global") {
            isDefault = true
            dimension = "type" }
    }


//    variantFilter {
//        println("***************************")
//        val flavorChannel = flavors.find { it.dimension == "channel" }?.name
//        val flavorType = flavors.find { it.dimension == "type" }?.name
//
//
//        println("flavor=$flavorChannel,type=$flavorType")
//        if (flavorChannel == "huawei" && flavorType == "local") {
//            ignore = true
//        }
//        if (flavorChannel == "xiaomi" && flavorType == "local") {
//            ignore = true
//        }
//    }

}

androidComponents {
    beforeVariants { variantBuilder ->
        val flavorChannel = variantBuilder.productFlavors.find {
            it.first == "channel"
        }?.second
        if (flavorChannel == "oppo" || flavorChannel == "xiaomi") {
            variantBuilder.enable = false
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.material)


    implementation(libs.bumptech.glide)
    implementation(libs.androidx.preference.ktx)

    implementation(libs.facebook.stetho)
    implementation(libs.facebook.stetho.okhttp3)

    implementation(libs.zchu.rxcache)
    implementation(libs.zchu.rxcache.kotlin)

    if (sourceCode) {
        implementation(project(":deps:thirdlib"))
    } else {
        implementation(libs.engineer.thirdlib)
    }
    implementation(project(":deps:common"))
//    globalImplementation(project(":deps:compose"))
    add("globalImplementation",project(":deps:compose"))
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
//    debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.10'

    implementation(libs.google.flexbox)

    implementation(libs.alibaba.fastjson)
    implementation(libs.kotlin.script.runtime)


    // https://github.com/koral--/android-gif-drawable/tree/master
    implementation(libs.droidsonroids.gif)
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.logging.interceptor)

//    implementation("com.github.microshow:RxFFmpeg:4.9.0")

    implementation(libs.androidx.metrics.performance)
    implementation(libs.androidx.tracing.ktx)
    // AndroidDraw Library
    implementation(libs.divyanshub.androiddraw)

    implementation(libs.noties.markwon.core)
    implementation(libs.noties.markwon.ext.latex)
    implementation(libs.facebook.fresco) {
        exclude("com.facebook.soloader", "soloader")
        exclude("com.facebook.fresco", "soloader")
        exclude("com.facebook.fresco", "soloader")
        exclude("com.facebook.fresco", "nativeimagefilters")
        exclude("com.facebook.fresco", "memory-type-native")
        exclude("com.facebook.fresco", "imagepipeline-native")
    }

    add("localImplementation", "io.noties.markwon:core:4.6.2")
//    add("huaweiGlobalImplementation","io.noties.markwon:core:4.6.2")
    implementation(libs.protobuf.javalite)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)
}
apply(from = "../custom-gradle/test-dep.gradle")
apply(from = "../custom-gradle/viewmodel-dep.gradle")
apply(from = "../custom-gradle/coroutines-dep.gradle")
apply(from = "../custom-gradle/rx-retrofit-dep.gradle")
apply(from = "../custom-gradle/hilt-dep.gradle")
apply(from = "../custom-gradle/apk_dest_dir_change.gradle")
apply(from = "../custom-gradle/report_apk_size_after_package.gradle")
//apply(from = "../custom-gradle/protobuf-config.gradle.kts")

tasks.register("listConfigurations") {
    doLast {
        configurations.forEach { config ->
            println(config.name)
        }
    }
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:4.26.1" }
    generateProtoTasks { all().forEach { it.plugins { create("java") { option("lite") } } } }
}