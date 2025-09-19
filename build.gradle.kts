// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.tools.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false
}
//apply(from = rootProject.rootDir.absolutePath + "/custom-gradle/check-style.gradle")

tasks.register("clean") {
    delete(rootProject.layout.buildDirectory)
    delete(rootProject.rootDir.absolutePath + "/final.apk")
    delete(rootDir.absolutePath + "/local_repo/")
}