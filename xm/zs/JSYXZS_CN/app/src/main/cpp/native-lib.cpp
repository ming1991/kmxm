#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_md_jsyxzs_1cn_zym_1xs_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
