#include <jni.h>
#include <string>
#include <android/log.h>

#define LOG_TAG "yAI_Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_banglalocalai_MainActivity_generateResponse(
        JNIEnv* env,
        jobject instance,
        jstring model_path_str,
        jstring prompt_str) {

    const char *model_path = env->GetStringUTFChars(model_path_str, 0);
    const char *prompt = env->GetStringUTFChars(prompt_str, 0);

    LOGI("Model Loading Path: %s", model_path);
    LOGI("User Prompt Received: %s", prompt);

    std::string response = "ধন্যবাদ! আমি আপনার লোকাল এআই সহকারী। আমি কিভাবে সাহায্য করতে পারি?";

    env->ReleaseStringUTFChars(model_path_str, model_path);
    env->ReleaseStringUTFChars(prompt_str, prompt);

    return env->NewStringUTF(response.c_str());
}
