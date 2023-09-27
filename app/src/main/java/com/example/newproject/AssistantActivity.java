package com.example.newproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AssistantActivity extends AppCompatActivity {
    TextView txtResponse;
    ImageView btnMic;
    TextToSpeech textToSpeech;
    EditText etQestion;
    Button btnAsk;
    // Constants for voice input
    private static final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);
        txtResponse = findViewById(R.id.textViewResponse);
        btnMic = findViewById(R.id.buttonMic);
        etQestion = findViewById(R.id.editTextQuestion);
        btnAsk=findViewById(R.id.buttonAsk);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start voice recognition when the microphone button is clicked
                startVoiceRecognition();
            }
        });

       btnAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Qes = etQestion.getText().toString();
                new GenerateResponseTask().execute(Qes);
            }
        });
    }
    // Method to start voice recognition
    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Speech recognition not supported on your device.", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the results of voice recognition
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    String userMessage = result.get(0);
                    new GenerateResponseTask().execute(userMessage);
                    etQestion.setText(userMessage);
                }
            }
        }
    }
    private class GenerateResponseTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String userMessage = strings[0];
            try {
                return OpenAIUtil.getChatGptResponse(userMessage);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return "Error occurred: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            txtResponse.setText(result);


            // Speak the response
            String toSpeak = getFirstNWords(result, 50);
            textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
    private String getFirstNWords(String text, int n) {
        String[] words = text.split("\\s+");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < Math.min(n, words.length); i++) {
            stringBuilder.append(words[i]).append(" ");
        }
        return stringBuilder.toString();
    }

}
