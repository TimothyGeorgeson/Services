package com.example.consultants.services;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.consultants.services.services.MyBoundService;
import com.example.consultants.services.services.MyIntentService;
import com.example.consultants.services.services.MyJobService;
import com.example.consultants.services.services.MyNormalService;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    TextView tvMain;
    ResultReceiver resultReceiver;
    private EditText etBoundData;
    private TextView tvBoundData;
    private MyBoundService myBoundService;
    private boolean isBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultReceiver = new MyResultReceiver(null);
        tvMain = findViewById(R.id.tvMain);
        etBoundData = findViewById(R.id.etBoundData);
        tvBoundData = findViewById(R.id.tvBoundData);
    }

    public void onNormalService(View view) {

        Intent intent = new Intent(getApplicationContext(), MyNormalService.class);

        switch (view.getId()) {
            case R.id.btnStartNormal:
                intent.putExtra("receiver", resultReceiver);
                startService(intent);
                break;
            case R.id.btnStopNormal:
                stopService(intent);
                break;
        }
    }

    public void onIntentService(View view) {
        switch (view.getId())
        {
            case R.id.btnStartFoo:
                MyIntentService.startActionFoo(getApplicationContext(), "one", "two");
                break;
            case R.id.btnStartBaz:
                MyIntentService.startActionBaz(getApplicationContext(), "three", "four");
                break;
        }
    }

    public void onBoundService(View view) {
        Intent intent = new Intent(getApplicationContext(), MyBoundService.class);

        switch (view.getId())
        {
            case R.id.btnBindService:
                if (!isBound) {
                    bindService(intent, this, Context.BIND_AUTO_CREATE);
                }

                break;
            case R.id.btnUnbindService:
                if(isBound){
                    isBound = false;
                    unbindService(this);
                    myBoundService = null;
                    Toast.makeText(getApplicationContext(), "Unbind", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnGetData:
                if (isBound){
                    tvBoundData.setText(myBoundService.getBoundData());
                }
                break;
            case R.id.btnUpdateData:
                if (isBound){
                    myBoundService.updateData(etBoundData.getText().toString());
                }
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MyBoundService.MyBinder myBinder = (MyBoundService.MyBinder) service;
        myBoundService = myBinder.getService();
        isBound = true;
        Toast.makeText(myBoundService, "Bound", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isBound = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onScheduleService(View view) {
        JobScheduler jobScheduler = (JobScheduler)getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(getApplicationContext(), MyJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(0, componentName)
                //.setOverrideDeadline(2000)
                .setMinimumLatency(1000)
                .setRequiresCharging(true)
                .build();

        jobScheduler.schedule(jobInfo);
    }

    class UpdateUI implements Runnable
    {
        String updateString;

        public UpdateUI(String updateString) {
            this.updateString = updateString;
        }
        public void run() {
            tvMain.setText(updateString);
        }
    }

    class MyResultReceiver extends ResultReceiver {
        public MyResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == 100) {
                runOnUiThread(new UpdateUI(resultData.getString("start")));
            } else if (resultCode == 200) {
                runOnUiThread(new UpdateUI(resultData.getString("end")));
            } else {
                runOnUiThread(new UpdateUI("" + resultCode));
            }
        }

    }
}
