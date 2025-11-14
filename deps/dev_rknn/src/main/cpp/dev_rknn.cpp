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
#include "wekws/wekws.h"
#include "rknn_resnet_wrapper.h"

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


extern "C"
JNIEXPORT void JNICALL
Java_com_engineer_rknn_util_ModelHandler_onDestroyYoyo(JNIEnv *env, jobject thiz) {
    destroy();
}

wekws::WeKws wekwsObj;

extern "C"
JNIEXPORT jint JNICALL
Java_com_engineer_rknn_util_ModelHandler_nativeInitWekws(JNIEnv *env, jobject thiz,
                                                         jstring model_path) {
    const char *p_model_path = env->GetStringUTFChars(model_path, nullptr);

    std::string input_path(p_model_path);

    return wekwsObj.create(input_path);

}


extern "C"
JNIEXPORT void JNICALL
Java_com_engineer_rknn_util_ModelHandler_onDestroyWekws(JNIEnv *env, jobject thiz) {
    wekwsObj.destroy("哈哈哈");
}
extern "C"
JNIEXPORT void JNICALL
Java_com_engineer_rknn_util_ModelHandler_infer(JNIEnv *env, jobject thiz) {
    wekwsObj.infer();
}

resnet50::RKNNResNetWrapper resnet5Obj;

extern "C"
JNIEXPORT jint JNICALL
Java_com_engineer_rknn_util_ModelHandler_nativeInitResnet(JNIEnv *env, jobject thiz,
                                                          jstring model_path) {
    const char *p_model_path = env->GetStringUTFChars(model_path, nullptr);

    std::string input_path(p_model_path);
    return resnet5Obj.initModelFromBytes(p_model_path);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_engineer_rknn_util_ModelHandler_inferResnet(JNIEnv *env, jobject thiz) {

}
extern "C"
JNIEXPORT void JNICALL
Java_com_engineer_rknn_util_ModelHandler_onDestroyResnet(JNIEnv *env, jobject thiz) {
    resnet5Obj.release();
}
extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_engineer_rknn_util_ModelHandler_resNetInfer(JNIEnv *env, jobject thiz,
                                                     jbyteArray image_byte, jint w, jint h,
                                                     jint format) {
    jsize len = env->GetArrayLength(image_byte);
    if (len <= 0) {
        LOGE("infer: input image empty");
        return nullptr;
    }
    std::vector<uint8_t> buf(len);
    env->GetByteArrayRegion(image_byte, 0, len, reinterpret_cast<jbyte *>(buf.data()));
    resnet50::RKNNResNetWrapper::ImageFormat fmt =
            (format ==1) ? resnet50::RKNNResNetWrapper::FORMAT_NV21 : resnet50::RKNNResNetWrapper::FORMAT_ARGB;
    std::vector<float> probs = resnet5Obj.inferFromBuffer(buf, w, h, fmt);
    if (probs.empty()) {
        LOGE("infer result empty");
        return nullptr;
    }
    jfloatArray out = env->NewFloatArray((jsize) probs.size());
    env->SetFloatArrayRegion(out, 0, (jsize) probs.size(), probs.data());
    return out;
}