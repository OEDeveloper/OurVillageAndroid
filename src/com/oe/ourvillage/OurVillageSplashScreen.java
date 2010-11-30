package com.oe.ourvillage;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;


public class OurVillageSplashScreen extends Activity {

    AlertDialog alert;

    AlertDialog.Builder builder;
    private final int SPLASH_DISPLAY_TIME = 3500;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        AlertDialog.Builder builder = new AlertDialog.Builder(OurVillageSplashScreen.this);
        builder.setMessage("Press OK To Continue").setCancelable(false)
               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                       // TODO Auto-generated method stub
                       OurVillageSplashScreen.this.finish();
                   }
               });
        alert = builder.create();

        // handler of display
        new Handler().postDelayed(new Runnable() {
            public void run() {

                alert.show();
            }
        }, SPLASH_DISPLAY_TIME);

    }
}
