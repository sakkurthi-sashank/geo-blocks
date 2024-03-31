package com.ritorno.geoblocks;

import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

/**
 * Created by Lucas on 12/12/2016.
 */
public class MyApp extends Application {

    private static Context context;
    private static BluetoothSocket bluetoothSocket;

    public void onCreate() {
        super.onCreate();
        MyApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApp.context;
    }

    public static BluetoothSocket getBluetoothSocket() {
        return MyApp.bluetoothSocket;
    }

    public static void setBluetoothSocket(BluetoothSocket socket){
        MyApp.bluetoothSocket = socket;
    }
}
