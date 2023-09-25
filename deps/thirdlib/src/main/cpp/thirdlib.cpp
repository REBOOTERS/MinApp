// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("thirdlib");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("thirdlib")
//      }
//    }

#include <jni.h>
#include <string>
#include "androidlog.h"

using namespace std;

extern "C" {
JNIEXPORT jstring JNICALL
Java_com_engineer_third_CppActivity_getHello(JNIEnv *env, jobject) {
    string hello = "Hello From C++";
    char info[40000] = {0};
    sprintf(info, "11111111");
    hello = to_string(env->GetVersion()) + hello;
    return env->NewStringUTF(hello.c_str());
}
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_engineer_third_internal_NativeMethodsFactory_plus(JNIEnv *env, jobject thiz, jint a,
                                                           jint b) {
    jint sum = a + b;
    return sum;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_engineer_third_internal_NativeMethodsFactory_staticPlus(JNIEnv *env, jclass clazz, jint a,
                                                                 jint b) {
    jint sum = a + b;
    return sum;
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_engineer_third_internal_NativeMethodsFactory_transToNativeString(JNIEnv *env, jobject thiz,
                                                                          jstring input) {

    jstring nativeString = env->NewStringUTF("hello from native");
    const char *str = env->GetStringUTFChars(input, 0);
    if (str != nullptr) {
        printf("input is %s", str);
        LOGE("input is %s", str);
    }
    env->ReleaseStringUTFChars(input, str);
    return nativeString;
}