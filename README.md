![Alt](https://repobeats.axiom.co/api/embed/8ceb012f571254438ab95c18fa734cb400ffc368.svg "Repobeats analytics image")

[![Android CI](https://github.com/REBOOTERS/MinApp/actions/workflows/android.yml/badge.svg)](https://github.com/REBOOTERS/MinApp/actions/workflows/android.yml)

# MinApp

 - Android 版的 Hello World 最小可以有多小

 通过各种手段，对编译产物 apk 进行压缩，看看一个最基础的 App 可以有多小。

# ReLearn Android
## 适配

 ### icon 适配

 - Android 8.0 开始的 adaptive-icon 是个什么鬼？其实很简单，看着这篇[一起来学习Android 8.0系统的应用图标适配吧](https://mp.weixin.qq.com/s/WxgHJ1stBjokPi6lTUd1Mg?) 就会了。如果图标本身就是圆形的，并且够大，那么理论上也可以永远适配了。

 ### Android 10 作用域存储

 - [Android 10适配要点，作用域存储](https://guolin.blog.csdn.net/article/details/105419420)

 ### Android ViewBinding

 apply plugin: 'kotlin-android-extensions' 被官方废弃的场景下，如何使用 viewbinding

 - [kotlin-android-extensions插件也被废弃了？扶我起来](https://mp.weixin.qq.com/s/keR7bO-Nu9bBr5Nhevbe1Q)
 
## Android SourceCode

[ImageView](https://mp.weixin.qq.com/s/xGD68ia_9VgL-qgbZM4m3g)

## Hacks

 - 反射调用被标记为 ```@UnsupportedAppUsage(maxTargetSdk = 28) ``` 的字段或方法
   命令行执行
    ```shell
        adb shell settings put global hidden_api_policy  1

    ```
    即可，详细参考[Android 10 中有关限制非 SDK 接口的更新](https://developer.android.google.cn/about/versions/10/non-sdk-q?hl=zh-cn)

# Kotlin 

## Coroutines 
 - [Google codelabs](https://developer.android.google.cn/codelabs/kotlin-coroutines?return=https%3A%2F%2Fdeveloper.android.google.cn%2Fcourses%2Fpathways%2Fandroid-coroutines%3Futm_source%3Dandroidweekly.io%26utm_medium%3Dwebsite%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fkotlin-coroutines#3) 
## Coroutines 原理

 -[Kotlin Jetpack 实战：图解协程原理](https://mp.weixin.qq.com/mp/appmsgalbum?__biz=MzUyMzk0NTk4OQ==&action=getalbum&album_id=1458908928921960449#rd)

# Else

how to make a minimal app

 - [Material Design](https://mp.weixin.qq.com/s/6b5hIMxqz2WEutcxRbRfhg)
 
 ```gradle
implementation 'com.google.android.material:material:1.2.0'
```

## App 签名
```shell
apksigner verify -verbose -print-certs xxx.apk
```

### CI

[Continuous Integration for Flutter with GitHub Actions](https://admcpr.com/continuous-integration-for-flutter-with-github-actions/)

```yaml
on: push
jobs: 
  build-and-test: 
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v1 
    - uses: actions/setup-java@v1
      with:
        java-version: '12.x'
    - uses: subosito/flutter-action@v1
      with:
        channel: 'stable'  
    # Get flutter packages
    - run: flutter pub get
    # Build :D 
    - run: flutter build aot
```

