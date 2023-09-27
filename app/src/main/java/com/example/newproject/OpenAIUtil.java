package com.example.newproject;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenAIUtil {
    private static final String OPENAI_API_KEY = "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";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/engines/text-davinci-003/completions";

    public static String getChatGptResponse(String userMessage) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();

        JSONObject requestBody = new JSONObject()
                .put("prompt", userMessage)
                .put("max_tokens", 4000); // Adjust as needed

        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .post(RequestBody.create(MediaType.parse("application/json"), requestBody.toString()))
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            return jsonResponse.getJSONArray("choices").getJSONObject(0).getString("text");
        } else {
            String errorBody = response.body().string();
            Log.e("OpenAI Error", "Response code: " + response.code() + ", Body: " + errorBody);
            return "Failed to get response.";
        }

    }
}