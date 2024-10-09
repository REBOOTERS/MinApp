import com.android.build.api.dsl.ApplicationProductFlavor
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.tools.ksp)
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
        targetSdk = libs.versions.targetSdk.get().toInt()
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


        resourceConfigurations += listOf("zh-rCN", "xxhdpi")

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += setOf("arm64-v8a")
        }
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
            signingConfig = signingConfigs.findByName("release")
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
    buildFeatures {
        dataBinding = true
        viewBinding = true
        aidl = true
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

    flavorDimensions.add("channel")
    flavorDimensions.add("type")
    productFlavors {
        create("xiaomi") { dimension = "channel" }
        create("oppo") { dimension = "channel" }
        create("huawei") { dimension = "channel" }
        create("local") {
            isDefault = true
            dimension = "type"  }
        create("global") { dimension = "type" }
    }


    variantFilter {
        println("***************************")
        val flavorChannel = flavors.find { it.dimension == "channel" }?.name
        val flavorType = flavors.find { it.dimension == "type" }?.name


        println("flavor=$flavorChannel,type=$flavorType")
        if (flavorChannel == "huawei" && flavorType == "local") {
            ignore = true
        }
        if (flavorChannel == "xiaomi" && flavorType == "local") {
            ignore = true
        }
    }

}

androidComponents {
    beforeVariants { variantBuilder ->
        val flavorChannel = variantBuilder.productFlavors.find {
            it.first == "channel"
        }?.second
        val flavorType = variantBuilder.productFlavors.find {
            it.first == "type"
        }?.second
        if (flavorChannel == "oppo" && flavorType == "global") {
            variantBuilder.enable = false
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.android.material:material:1.12.0")


    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.preference:preference-ktx:1.2.1")

    implementation("com.facebook.stetho:stetho:1.6.0")
    implementation("com.facebook.stetho:stetho-okhttp3:1.6.0")

    implementation("com.github.z-chu.RxCache:rxcache:2.3.5")
    implementation("com.github.z-chu.RxCache:rxcache-kotlin:2.3.5")

    if (sourceCode) {
        implementation(project(":deps:thirdlib"))
    } else {
        implementation("com.engineer.third:thirdlib:1.0.0")
    }
    implementation(project(":deps:common"))
    implementation(project(":deps:compose"))
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
//    debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.10'

    implementation("com.google.android.flexbox:flexbox:3.0.0")

    implementation("com.alibaba:fastjson:1.2.69")
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:2.0.20")


    // https://github.com/koral--/android-gif-drawable/tree/master
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.28")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

//    implementation("com.github.microshow:RxFFmpeg:4.9.0")

    implementation("androidx.metrics:metrics-performance:1.0.0-beta01")
    implementation("androidx.tracing:tracing-ktx:1.2.0")
    // AndroidDraw Library
    implementation("com.github.divyanshub024:AndroidDraw:v0.1")

    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:ext-latex:4.6.2")
    implementation("com.facebook.fresco:fresco:3.1.3") {
        exclude("com.facebook.soloader","soloader")
        exclude("com.facebook.fresco","soloader")
        exclude("com.facebook.fresco","soloader")
        exclude("com.facebook.fresco","nativeimagefilters")
        exclude("com.facebook.fresco","memory-type-native")
        exclude("com.facebook.fresco","imagepipeline-native")
    }

    add("globalImplementation","io.noties.markwon:core:4.6.2")
//    add("huaweiGlobalImplementation","io.noties.markwon:core:4.6.2")

}
apply(from = "../custom-gradle/test-dep.gradle")
apply(from = "../custom-gradle/viewmodel-dep.gradle")
apply(from = "../custom-gradle/coroutines-dep.gradle")
apply(from = "../custom-gradle/rx-retrofit-dep.gradle")
apply(from = "../custom-gradle/hilt-dep.gradle")
apply(from = "../custom-gradle/apk_dest_dir_change.gradle")
apply(from = "../custom-gradle/report_apk_size_after_package.gradle")

tasks.register("listConfigurations") {
    doLast {
        configurations.forEach { config ->
            println(config.name)
        }
    }
}