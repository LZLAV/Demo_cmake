#include <jni.h>
#include <string>

/**
 * 成员方法，第二个参数类型为 jobject
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_lzlbuilder_demo_natives_NativeTest_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

/**
 * 静态方法，第二个参数类型为 jclass
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_lzlbuilder_demo_natives_NativeTest_stringFromJNIStatic(
        JNIEnv* env,
        jclass /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}