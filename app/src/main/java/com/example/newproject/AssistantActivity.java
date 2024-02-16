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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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

    String url = "https://api.openai.com/v1/chat/completions";
    String apiKey = "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";
    String model = "gpt-3.5-turbo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);
        txtResponse = findViewById(R.id.textViewResponse);
        btnMic = findViewById(R.id.buttonMic);
        etQestion = findViewById(R.id.editTextQuestion);
        btnAsk = findViewById(R.id.buttonAsk);

        btnAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = etQestion.getText().toString();
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        try {
                            URL obj = new URL(url);
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                            con.setRequestMethod("POST");
                            con.setRequestProperty("Authorization", "Bearer " + apiKey);
                            con.setRequestProperty("Content-Type", "application/json");

                            String body = "{\"model\":\"gpt-3.5-turbo\",\"messages\":[{\"role\":\"user\",\"content\": \"" + mess + "\"}]}";
                            con.setDoOutput(true);
                            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                            writer.write(body);
                            writer.flush();
                            writer.close();

                            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String inputLine;
                            StringBuilder response = new StringBuilder();
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();

                            return response.toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(String response) {
                        super.onPostExecute(response);
                        if (response != null) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                JSONArray choicesArray = jsonResponse.getJSONArray("choices");
                                if (choicesArray.length() > 0) {
                                    JSONObject firstChoice = choicesArray.getJSONObject(0);
                                    JSONObject messageObject = firstChoice.getJSONObject("message");
                                    String assistantResponse = messageObject.getString("content");
                                    txtResponse.setText(assistantResponse);
                                } else {
                                    Toast.makeText(AssistantActivity.this, "No response from the assistant", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(AssistantActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AssistantActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                        }
                    }

                }.execute();
            }
        });

        // Other methods...
    }
}
