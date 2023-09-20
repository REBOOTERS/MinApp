pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io")  }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io")  }
    }
}

println("Start All")

println("rootProject=$rootProject")
println("rootDir    =$rootDir")

println("settings.dir=$settingsDir")
println("settings.dir.parent=" + settingsDir.parent)

rootProject.name = "MiniApp"
include (":thirdlib")
include (":app")

//setBinding(new Binding([gradle: this]))
//evaluate(new File(settingsDir, "sub_flutter/.android/include_flutter.groovy"))
//include ":sub_flutter"


include (":compose")
include (":other")
include (":common")
include (":lib_jar")
