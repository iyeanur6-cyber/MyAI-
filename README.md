# MyAI-
# yAI - On-Device Local Bangla AI Assistant 

yAI (Your Artificial Intelligence) is a fully privacy-focused, on-device AI assistant tailored for the Bengali language. It operates 100% offline using a quantized Small Language Model (SLM), ensures data safety via local SQLite database storage, and features an optional web search engine.

---

## 🌟 Key Features

- **100% On-Device Processing:** Powered by `llama.cpp` C++ engine running locally on Android.
- **Privacy First:** No user data or chat logs leave your phone.
- **Save Chat Mode:** Encrypted chat history saved locally using Android Room SQLite Database.
- **Web Search Engine:** Scrapes DuckDuckGo HTML for context when enabled, bypassing high API costs.
- **Voice Support:** Built-in Bangla Speech Recognition and Text-to-Speech (TTS).

---

## 🏗️ Project Architecture
```
BanglaLocalAI/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── cpp/               # C++ JNI Bridge for llama.cpp
│   │   │   │   ├── native-lib.cpp
│   │   │   │   └── CMakeLists.txt
│   │   │   ├── java/com/example/banglalocalai/
│   │   │   │   ├── data/          # Room Database (ChatEntity, ChatDao, AppDatabase)
│   │   │   │   ├── network/       # DuckDuckGo Web Searcher
│   │   │   │   └── MainActivity.java
│   │   │   ├── res/
│   │   │   │   ├── drawable/      # yAI Silver-on-Black Vector Icon
│   │   │   │   └── layout/        # Activity UI Layout
│   │   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```
--

## 📥 Model Setup (Mandatory)

Due to GitHub file size limits, the model is not bundled inside the repository.

1. Download the quantized **Qwen 2.5 0.5B Instruct GGUF** model:
   - **Recommended Model:** `qwen2.5-0.5b-instruct-q4_k_m.gguf` (~350 MB)
   - **Download Link:** [Hugging Face Repository](https://huggingface.co/Qwen/Qwen2.5-0.5B-Instruct-GGUF)

2. Place the downloaded `.gguf` file into your phone's internal application storage directory:
   ```text
   /data/user/0/com.example.banglalocalai/files/qwen2.5-0.5b-instruct-q4_k_m.gguf
   '''
   🛠️ How to Build from Source
Prerequisites
Android SDK 34
Android NDK & CMake
JDK 17
Building via Terminal
```
git clone [https://github.com/your-username/BanglaLocalAI.git](https://github.com/your-username/BanglaLocalAI.git)
cd BanglaLocalAI
./gradlew assembleDebug
```
The compiled APK will be located at:
app/build/outputs/apk/debug/app-debug.apk
📄 License
This project is open-source and available under the MIT License.
```

