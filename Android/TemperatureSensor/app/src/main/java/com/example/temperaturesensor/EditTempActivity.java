package com.example.temperaturesensor;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class EditTempActivity extends AppCompatActivity {
    private Button submitBtn;
    private ImageButton closeBtn;
    private EditText low,high;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        submitBtn = findViewById(R.id.submit);
        closeBtn = findViewById(R.id.closeBtn2);
        low = findViewById(R.id.lowInput);
        high = findViewById(R.id.highInput);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(openActivity);
                finish();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitData();
            }
        });
    }

    private void submitData() {
        DataBaseHelper db = new DataBaseHelper(getApplicationContext());
        if (low.getText().toString().equals("") || high.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),"Fill all input fields", Toast.LENGTH_SHORT).show();
        }
        else if(low.getText().toString().compareTo(high.getText().toString()) >= 0){
            Toast.makeText(getApplicationContext(), "Low shouldn't be higher than high", Toast.LENGTH_LONG).show();
        }
        else{
            Cursor data = db.getTemp();

            String lastTemp = "";
            while(data.moveToNext()){
                lastTemp = data.getString(0);
            }
            if(db.addTemp(lastTemp,high.getText().toString(),low.getText().toString())){
                Toast.makeText(getApplicationContext(),"Data added",Toast.LENGTH_SHORT).show();
                PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(NotifWorker.class,15, TimeUnit.MINUTES).build();
                WorkManager.getInstance(getApplicationContext()).cancelAllWork();
                WorkManager.getInstance(getApplicationContext()).enqueue(req);
                Intent openActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(openActivity);
                finish();
            }
        }
    }
}
