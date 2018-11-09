package com.example.consultants.services.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MyNormalService extends Service {
    public static final String TAG = MyNormalService.class.getSimpleName() + "_TAG";

    int count = 0;
    Timer timer = new Timer();
    MyTimerTask timerTask;
    ResultReceiver resultReceiver;

    public MyNormalService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: " + Thread.currentThread().getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: "
                + Thread.currentThread().getName() + " "
                + this.toString()
                + ": " + super.onStartCommand(intent, flags, startId));

        //operations go here
        resultReceiver = intent.getParcelableExtra("receiver");

        timerTask = new MyTimerTask();
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
        //complete
        //stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: " + Thread.currentThread().getName());
        timer.cancel();
        Bundle bundle = new Bundle();
        bundle.putString("end", "Timer Stopped....");
        resultReceiver.send(200, bundle);
    }

    class MyTimerTask extends TimerTask
    {
        public MyTimerTask() {
            Bundle bundle = new Bundle();
            bundle.putString("start", "Timer Started....");
            resultReceiver.send(100, bundle);
        }
        @Override
        public void run() {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("s");
//            resultReceiver.send(Integer.parseInt(dateFormat.format(System.currentTimeMillis())), null);
                count += 1;
                resultReceiver.send(count, null);

        }
    }
}
