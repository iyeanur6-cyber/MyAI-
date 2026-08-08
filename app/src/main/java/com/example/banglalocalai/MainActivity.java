package com.example.banglalocalai;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.example.banglalocalai.data.AppDatabase;
import com.example.banglalocalai.data.ChatEntity;
import com.example.banglalocalai.network.WebSearcher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private AppDatabase db;
    private CheckBox saveChatCheck;
    private CheckBox webSearchCheck;
    private EditText inputEdit;
    private TextView chatView;
    private LinearLayout downloadContainer;
    private Button downloadBtn;
    private ProgressBar downloadProgress;

    static {
        System.loadLibrary("native-lib");
    }

    public native String generateResponse(String modelPath, String prompt);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveChatCheck = findViewById(R.id.saveChatCheck);
        webSearchCheck = findViewById(R.id.webSearchCheck);
        inputEdit = findViewById(R.id.inputEdit);
        chatView = findViewById(R.id.chatView);
        downloadContainer = findViewById(R.id.downloadContainer);
        downloadBtn = findViewById(R.id.downloadBtn);
        downloadProgress = findViewById(R.id.downloadProgress);
        Button sendBtn = findViewById(R.id.sendBtn);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "local_ai_db").allowMainThreadQueries().build();

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("bn", "BD"));
            }
        });

        checkModelExistence();
        loadPreviousChat();

        downloadBtn.setOnClickListener(v -> startModelDownload());
        sendBtn.setOnClickListener(v -> processUserInput());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_delete) {
            new Thread(() -> {
                db.chatDao().deleteAllChats();
                runOnUiThread(() -> {
                    chatView.setText("");
                    chatView.append("[System]: সমস্ত চ্যাট হিস্ট্রি মুছে ফেলা হয়েছে।");
                });
            }).start();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkModelExistence() {
        String modelPath = getFilesDir().getAbsolutePath() + "/qwen2.5-0.5b-instruct-q4_k_m.gguf";
        File file = new File(modelPath);
        if (!file.exists()) {
            downloadContainer.setVisibility(View.VISIBLE);
        } else {
            downloadContainer.setVisibility(View.GONE);
        }
    }

    private void startModelDownload() {
        downloadBtn.setEnabled(false);
        downloadProgress.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                String downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-0.5B-Instruct-GGUF/resolve/main/qwen2.5-0.5b-instruct-q4_k_m.gguf";
                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int fileLength = connection.getContentLength();
                InputStream input = connection.getInputStream();
                File outputFile = new File(getFilesDir(), "qwen2.5-0.5b-instruct-q4_k_m.gguf");
                FileOutputStream output = new FileOutputStream(outputFile);

                byte[] data = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    if (fileLength > 0) {
                        int progress = (int) (total * 100 / fileLength);
                        runOnUiThread(() -> downloadProgress.setProgress(progress));
                    }
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                runOnUiThread(() -> {
                    downloadContainer.setVisibility(View.GONE);
                    chatView.append("\n[System]: মডেল ডাউনলোড সম্পূর্ণ হয়েছে। আপনি এখন চ্যাট করতে পারেন।");
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    downloadBtn.setEnabled(true);
                    downloadProgress.setVisibility(View.GONE);
                    chatView.append("\n[System]: ডাউনলোড ব্যর্থ হয়েছে। ইন্টারনেট কানেকশন চেক করুন।");
                });
            }
        }).start();
    }

    private void loadPreviousChat() {
        new Thread(() -> {
            List<ChatEntity> history = db.chatDao().getAllMessages();
            runOnUiThread(() -> {
                for (ChatEntity chat : history) {
                    chatView.append("\n" + chat.sender + ": " + chat.message);
                }
            });
        }).start();
    }

    private void processUserInput() {
        String userQuery = inputEdit.getText().toString().trim();
        if (userQuery.isEmpty()) return;

        inputEdit.setText("");
        chatView.append("\n\nUser: " + userQuery);

        if (saveChatCheck.isChecked()) {
            new Thread(() -> db.chatDao().insertMessage(
                    new ChatEntity("User", userQuery, System.currentTimeMillis())
            )).start();
        }

        new Thread(() -> {
            String context = "";
            if (webSearchCheck.isChecked()) {
                context = WebSearcher.searchWeb(userQuery);
            }

            String fullPrompt;
            if (!context.isEmpty()) {
                fullPrompt = "<|im_start|>system\nতুমি একজন অভিজ্ঞ বাংলা এআই সহকারী। নিচে দেওয়া তথ্যের ভিত্তিতে উত্তর দাও।<|im_end|>\n" +
                        "<|im_start|>user\nতথ্য: " + context + "\nপ্রশ্ন: " + userQuery + "<|im_end|>\n" +
                        "<|im_start|>assistant\n";
            } else {
                fullPrompt = "<|im_start|>system\nতুমি একজন অভিজ্ঞ বাংলা এআই সহকারী। সর্বদা বাংলায় উত্তর দেবে।<|im_end|>\n" +
                        "<|im_start|>user\n" + userQuery + "<|im_end|>\n" +
                        "<|im_start|>assistant\n";
            }

            String modelPath = getFilesDir().getAbsolutePath() + "/qwen2.5-0.5b-instruct-q4_k_m.gguf";
            File modelFile = new File(modelPath);

            String response;
            if (!modelFile.exists()) {
                response = "মডেল ফাইলটি পাওয়া যায়নি। অনুগ্রহ করে ডাউনলোড করুন।";
            } else {
                response = generateResponse(modelPath, fullPrompt);
            }

            runOnUiThread(() -> {
                chatView.append("\nyAI: " + response);
                tts.speak(response, TextToSpeech.QUEUE_FLUSH, null, null);
                if (saveChatCheck.isChecked()) {
                    new Thread(() -> db.chatDao().insertMessage(
                            new ChatEntity("yAI", response, System.currentTimeMillis())
                    )).start();
                }
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
                        }
                    
