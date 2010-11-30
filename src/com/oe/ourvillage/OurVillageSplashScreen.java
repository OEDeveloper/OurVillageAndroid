package com.oe.ourvillage;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;


public class OurVillageSplashScreen extends Activity {

    private final int SPLASH_DISPLAY_TIME = 3500;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // handler of display
        new Handler().postDelayed(new Runnable() {
            public void run() {
                OurVillageSplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_TIME);

    }
}
