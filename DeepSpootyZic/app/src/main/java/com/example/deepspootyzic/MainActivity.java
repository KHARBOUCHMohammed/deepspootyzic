package com.example.deepspootyzic;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    TextView mTextTv;
    Button btn;

    private static final String API_URL = "http://10.126.2.115:3000/getAllMusic";
    private static final String TAG = "MainActivity";
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextTv = findViewById(R.id.TextTv);
        btn = findViewById(R.id.btnRecord);

        listView = findViewById(R.id.listView);
        new FetchMp3Task().execute(API_URL);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Salut pouvez vous parler");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {

            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:{
                if(resultCode == RESULT_OK && null !=data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mTextTv.setText(result.get(0));

                    // Envoie le texte en HTTP
                    new SendTextTask().execute(result.get(0));
                }
            }
        }
    }

    private static final String SEND_URL = "http://10.126.2.115:3000/sendText";

    private class SendTextTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... texts) {
            try {
                URL url = new URL(SEND_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoOutput(true);

                // Crée une chaîne de requête pour envoyer le texte en tant que paramètre
                String query = String.format("text=%s", URLEncoder.encode(texts[0], "UTF-8"));

                // Envoie la requête
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                // Vérifie la réponse
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    throw new IOException("HTTP error code: " + responseCode);
                }

            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return null;
        }
    }


    private void showMp3List(List<String> mp3List) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mp3List);
        listView.setAdapter(adapter);
    }

    private class FetchMp3Task extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... urls) {
            List<String> mp3List = new ArrayList<>();

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;



                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                JSONArray jsonArray = new JSONArray(sb.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    mp3List.add(jsonArray.getString(i));
                }


                reader.close();

            } catch (IOException | JSONException e) {
                Log.e(TAG, e.getMessage(), e);
            }


            return mp3List;
        }

        @Override
        protected void onPostExecute(List<String> mp3List) {
            showMp3List(mp3List);
            Log.d("MainActivity", "MP3 List: " + mp3List);
        }
    }




}