#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_banglalocalai_MainActivity_generateResponse(
        JNIEnv* env,
        jobject instance,
        jstring model_path_str,
        jstring prompt_str) {

    const char *model_path = env->GetStringUTFChars(model_path_str, 0);
    const char *prompt = env->GetStringUTFChars(prompt_str, 0);

    std::string response = "এটি অন-ডিভাইস C++ ইঞ্জিন থেকে প্রাপ্ত রেসপন্স।";

    env->ReleaseStringUTFChars(model_path_str, model_path);
    env->ReleaseStringUTFChars(prompt_str, prompt);

    return env->NewStringUTF(response.c_str());
}
