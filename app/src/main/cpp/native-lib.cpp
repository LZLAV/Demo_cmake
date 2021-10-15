#include <jni.h>
#include <stdio.h>

#include <android/log.h>

#define ENABLE_LOGD 0
#define LOG_TAG "native_lib"

#if ENABLE_LOGD
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#else
#define LOGD(...)
#endif
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#ifdef __cplusplus
extern "C" {
#endif
/**
 * 成员方法，第二个参数类型为 jobject
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_lzlbuilder_demo_natives_NativeTest_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return (*env)->NewStringUTF(env,hello.c_str());
}

/**
 * 静态方法，第二个参数类型为 jclass
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_lzlbuilder_demo_natives_NativeTest_stringFromJNIStatic(
        JNIEnv* env,
        jclass /* this */) {
    std::string hello = "Hello from C++";
    return (*env)->NewStringUTF(env,hello.c_str());
}

#ifdef __cplusplus
}
#endif