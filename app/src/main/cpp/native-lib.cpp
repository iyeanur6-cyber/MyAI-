#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_banglalocalai_MainActivity_generateResponse(
        JNIEnv* env,
        jobject instance,
        jstring model_path_str,
        jstring prompt_str) {

    const char *prompt = env->GetStringUTFChars(prompt_str, 0);
    std::string userPrompt(prompt);

    std::string response;

    if (userPrompt.find("Context:") != std::string::npos || userPrompt.find("তথ্য:") != std::string::npos) {
        response = "অনলাইন থেকে প্রাপ্ত তথ্যের ভিত্তিতে বিশ্লেষণ করা হচ্ছে: " + userPrompt;
    } else {
        response = "আপনার প্রম্পট প্রসেস করা হয়েছে: " + userPrompt;
    }

    env->ReleaseStringUTFChars(prompt_str, prompt);

    return env->NewStringUTF(response.c_str());
}
