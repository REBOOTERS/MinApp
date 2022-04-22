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

using namespace std;

extern "C" {
JNIEXPORT jstring JNICALL
Java_com_engineer_third_CppActivity_getHello(JNIEnv *env, jobject) {
    string hello = "Hello From C++";
    char info[40000] = {0};
    sprintf(info,"11111111");
    hello = to_string(env->GetVersion()) + hello;
    return env->NewStringUTF(hello.c_str());
}
}