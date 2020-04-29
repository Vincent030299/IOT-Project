package com.example.temperaturesensor;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {
    private ImageButton closeBtn,editBtn;
    private TextView low,high,last;
    private Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        closeBtn =findViewById(R.id.closeBtn);
        editBtn = findViewById(R.id.editBtn);
        last = findViewById(R.id.tempValue);
        refresh = findViewById(R.id.button);
        low = findViewById(R.id.low);
        high = findViewById(R.id.high);

        getData();
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String thingSpeakUrl = "https://api.thingspeak.com/channels/1048437/feeds.json?api_key=3VGZL2PLVQI545O1";
                OkHttpClient client = new OkHttpClient();

                //sends request to read data from thingspeak channel
                Request request = new Request.Builder()
                        .url(thingSpeakUrl)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    private String SensorOutput;

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.d("hoi", e.toString());
                        Toast.makeText(getApplicationContext(),"Failed to sync data", Toast.LENGTH_SHORT).show();
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
                                last.setText(SensorOutput);
                                DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext());
                                dataBaseHelper.addTemp(SensorOutput,high.getText().toString(),low.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),"Failed to sync data", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openEditActivity = new Intent(getApplicationContext(),EditTempActivity.class);
                startActivity(openEditActivity);
                finish();
            }
        });
    }

    public void getData(){
        DataBaseHelper db = new DataBaseHelper(getApplicationContext());
        Cursor data = db.getTemp();
        String lastTemp = "";
        String highTemp = "";
        String lowTemp = "";
        while(data.moveToNext()){
            lastTemp = data.getString(0);
            highTemp = data.getString(1);
            lowTemp = data.getString(2);
        }

        if (lowTemp.equals("")) {
            Intent openActivity = new Intent(getApplicationContext(), EditTempActivity.class);
            startActivity(openActivity);
            finish();
        } else {
            low.setText(lowTemp);
            high.setText(highTemp);
            last.setText(lastTemp);
        }
    }
}
