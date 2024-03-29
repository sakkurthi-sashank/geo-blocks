package com.ritorno.geoblocks;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class SplashActivity extends Activity {

    private final int LOCATION_PERMISSION = 1;
    boolean permission_local = false;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);
        else
            OpenNextScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    protected void OpenNextScreen(){
        try {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                        Intent i = new Intent(getBaseContext(), MapActivity.class);
                        startActivity(i);
                        finish();
                }
            }, 1500);

        }catch (Exception e){
            Log.e("SPLASH","ERRO: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    permission_local=true;
                    OpenNextScreen();
                }
                else {
                    Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
                    permission_local=true;
                    OpenNextScreen();
                }
            }
        }
    }
}
