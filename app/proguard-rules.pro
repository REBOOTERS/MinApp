# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-keep class com.engineer.android.mini.proguards.Utils {*;}
#-keep class com.engineer.android.mini.proguards.Utils$MyBuilder {*;}
#-keep class com.engineer.android.mini.proguards.A
#-keep class com.engineer.android.mini.proguards.B {*;}

-keep class com.engineer.android.mini.proguards.Utils
#-keepnames class com.engineer.android.mini.proguards.Utils{*;}
#-keepclassmembernames class com.engineer.android.mini.proguards.Utils {
#    public static void test3(android.content.Context);
#}

-keep class com.engineer.android.mini.proguards.BlankFragment {*;}

-keepclasseswithmembers class com.engineer.android.mini.proguards.Utils {
    public static void test3(android.content.Context);
}

-keepclassmembers enum * {  # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#-keep class * implements java.io.Serializable {*;}

-keepclassmembers class com.engineer.android.mini.proguards.ModelA$ModelB {
    <fields>;
    <methods>;
}
-keepclasseswithmembers class * {
    public <fields>;
    public <methods>;
    public *** aaa(java.lang.String,java.lang.String);

}

####

## 反射
-keep class androidx.recyclerview.widget.RecyclerView {*;}
-keep class androidx.recyclerview.widget.RecyclerView$Recycler {*;}
## 反射

-keepattributes SourceFile,LineNumberTable
-optimizationpasses 5  # 指定代码的压缩级别
-allowaccessmodification #优化时允许访问并修改有修饰符的类和类的成员
-dontusemixedcaseclassnames  # 是否使用大小写混合
-dontskipnonpubliclibraryclasses  # 是否混淆第三方jar
-dontpreverify  # 混淆时是否做预校验
-verbose    # 混淆时是否记录日志
-ignorewarnings  # 忽略警告，避免打包时某些警告出现
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*  # 混淆时所采用的算法
