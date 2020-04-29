package com.example.temperaturesensor;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotifWorker extends Worker {
    private static final String CHANNEL_ID = "tempNotif";
    private String SensorOutput = "";
    private String lowTemp,highTemp = "";
    private DataBaseHelper db = new DataBaseHelper(getApplicationContext());

    public NotifWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

    }

    //runs whenever this class is called
    @NonNull
    @Override
    public Result doWork() {
        Cursor data = db.getTemp();

        //gets data from database
        while(data.moveToNext()){
            highTemp = data.getString(1);
            lowTemp = data.getString(2);
        }
        String thingSpeakUrl = "https://api.thingspeak.com/channels/1048437/feeds.json?api_key=3VGZL2PLVQI545O1";
        OkHttpClient client = new OkHttpClient();

        //sends request to read data from thingspeak channel
        Request request = new Request.Builder()
                .url(thingSpeakUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("hoi", e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String thingSpeakResponse = response.body().string();
                    try {
                        //handles the response from thingspeak
                        JSONObject responseJson = new JSONObject(thingSpeakResponse);
                        JSONArray feedArray = responseJson.getJSONArray("feeds");
                        JSONObject channelArray = responseJson.getJSONObject("channel");
                        int lastEntryId = channelArray.getInt("last_entry_id");
                        int lastSensorUpdateIndex = lastEntryId - 1;
                        JSONObject SensorOutputArray = feedArray.getJSONObject(lastSensorUpdateIndex);
                        SensorOutput = SensorOutputArray.getString("field1");
                        Log.d("hoi", SensorOutput);

                        if(Float.parseFloat(SensorOutput) < Float.parseFloat(lowTemp)){
                            MakeNotification("The temperature is too cold", "Turn up the heat");
                        } else if(Float.parseFloat(SensorOutput) > Float.parseFloat(highTemp)){
                            MakeNotification("The temperature is too hot", "Turn down the heat");
                        }
                        db.addTemp(SensorOutput,highTemp,lowTemp);
                        Log.d("RESPONSE", "onResponse: " + SensorOutput);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        return Result.success();
    }

    private void MakeNotification(String title, String descriptionNoti){
        Log.d("Notification", "the worker is working");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NotifChannel";
            String description = "Notifications for temperatures";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.thermometer)
                .setColor(Color.parseColor("#1976d2"))
                .setColorized(true)
                .setContentTitle(title)
                .setContentText(descriptionNoti)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());
    }
}
