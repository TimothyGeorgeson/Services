package com.example.consultants.services.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MyBoundService extends Service {

    String boundData = "Default data";

    public MyBoundService() {

    }

    public IBinder iBinder = new MyBinder();

    //create the binder to return the service instance
    public class MyBinder extends Binder {
        public MyBoundService getService(){
            return MyBoundService.this;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    public void updateData(String boundData){
        this.boundData = boundData;
    }

    public String getBoundData(){
        return boundData;
    }
}
