package com.example.weathertracker.fcm;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationSender {
    private String tkn, title, msg;

    public NotificationSender(String tkn, String title, String msg) {
        this.tkn = tkn;
        this.title = title;
        this.msg = msg;
    }

    public void send() {
        new Notify().execute();
    }

    public class Notify extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {


            try {
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "key=AAAAUWNT9rI:APA91bGeZ4lzW5KxKad-J0YvlZqp_vn5uQHaTeaTdMjcI9vBgMmjknMOqrpLlzSiqCx9ipTysA2jw2NsTxABMsQoZC85bYyMSdmd3BYD4FK4S_e34ulOLFdDumGn8NiQaYArDjPBUvjs");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();

                json.put("to", tkn);


                JSONObject info = new JSONObject();
                info.put("title", title);   // Notification title
                info.put("body", msg); // Notification body
                json.put("data", info);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(json.toString());
                wr.flush();
                conn.getInputStream();

            } catch (Exception e) {
                Log.d("Error", "" + e);
            }
            return null;
        }
    }
}

