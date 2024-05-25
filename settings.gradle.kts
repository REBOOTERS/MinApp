pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("${rootDir}/local_repo/") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 添加 jitpack 的依赖
        maven { url = uri("https://jitpack.io") }
        // 添加 私有仓库的依赖
        maven {
            this.isAllowInsecureProtocol = true
            url = uri("http://192.168.11.112")
        }
        // 添加本地仓库的依赖
        maven { url = uri("${rootDir}/local_repo/") }
    }
}

println("Start All")

println("rootProject=$rootProject")
println("rootDir    =$rootDir")

println("settings.dir=$settingsDir")
println("settings.dir.parent=" + settingsDir.parent)

rootProject.name = "MiniApp"
include(":app")
include(":deps:thirdlib")
include(":deps:compose")
include(":deps:filter")
include(":other")
include(":deps:common")
include(":deps:lib_jar")
 