package com.tai06dothe.handlerasynctask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int MSG_UP_NUMBER = 1;
    public static final int MSG_DONE = 2;

    private ProgressBar progressBar;
    private TextView txtNumber;
    private Button btnStart;
    private Handler handler;
    private boolean isCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initHandler();
    }

    private void init() {
        progressBar = findViewById(R.id.progressBar);
        txtNumber = findViewById(R.id.txtNumber);
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);
    }

    private void initHandler() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case MSG_UP_NUMBER:
                        txtNumber.setText(String.valueOf(msg.arg1));
                        isCheck = true;
                        break;
                    case MSG_DONE:
                        txtNumber.setText("SUCCESS!");
                        progressBar.setVisibility(View.VISIBLE);
                        new Async().execute();
                        break;
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                if (!isCheck){
                    progressBar.setProgress(0);
                    downCount();
                }
                break;
            default:
                break;
        }
    }

    private void downCount() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
            int count = 10;
            while (count > 0){
                Message msg = new Message();
                msg.what = MSG_UP_NUMBER;
                msg.arg1 = count;
                handler.sendMessage(msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count--;
            }
            handler.sendEmptyMessage(MSG_DONE);
            }
        });
        thread.start();
    }

    private class Async extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... voids) {
            int count = 0;
            while (count <= 10){
                publishProgress(count);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
            }
            return "DONE";
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            isCheck = false;
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
    }
}