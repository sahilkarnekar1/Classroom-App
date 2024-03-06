package com.example.newproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class AssistantActivity extends AppCompatActivity {
    TextView txtResponse;
    EditText etQuestion;
    Button btnAsk;

    String url = "https://api.openai.com/v1/chat/completions";
    String apiKey = "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";
    String model = "gpt-3.5-turbo";
    String strOutput = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant);

        txtResponse = findViewById(R.id.textViewResponse);
        etQuestion = findViewById(R.id.editTextQuestion);
        btnAsk = findViewById(R.id.buttonAsk);

        btnAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = etQuestion.getText().toString();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("model", model);
                    JSONArray messages = new JSONArray();
                    JSONObject message = new JSONObject();
                    message.put("role", "user");
                    message.put("content", userMessage); // Use user input
                    messages.put(message);
                    jsonObject.put("messages", messages);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray choices = response.getJSONArray("choices");
                            if (choices.length() > 0) {
                                JSONObject firstCo = choices.getJSONObject(0);
                                      JSONObject message = firstCo.getJSONObject("message");
                                      String text = message.getString("content");
                                txtResponse.setText(text);
                            }else{
                                Toast.makeText(AssistantActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + apiKey);
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
            }
        });

    }

}



