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
#include "yoyoimg/yolo_image.h"

using namespace std;


static char *jstringToChar(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);

    if (alen > 0) {
        rtn = new char[alen + 1];
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

extern "C" {


JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *) {

    return JNI_VERSION_1_6;
}


}


extern "C"
JNIEXPORT jint JNICALL
Java_com_engineer_rknn_util_ModelHandler_nativeInitYoyo(JNIEnv *env, jobject thiz, jint input_w,
                                                        jint input_h, jint input_c,
                                                        jstring model_path) {
    char *model_path_p = jstringToChar(env, model_path);
    return create(input_h, input_w, input_c, model_path_p);
}