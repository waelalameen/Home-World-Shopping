package com.wael.alameen.worldonlineshopping;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("TOKEN", token);
        SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
        sendToServer(token);
    }

    private void sendToServer(String token) {

        new AsyncTask<String, Void, Void>() {
            final String TOKEN_URL = "http://mallnet.me/shopping/tokens.php";
            @Override
            protected Void doInBackground(String... params) {
                String token = params[0];
                try {
                    URL url = new URL(TOKEN_URL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    String data = URLEncoder.encode("token", "UTF-8")+"="+URLEncoder.encode(token, "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    inputStream.close();
                    httpURLConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(token);
    }
}
