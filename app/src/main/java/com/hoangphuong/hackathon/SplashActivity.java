package com.hoangphuong.hackathon;

/**
 * Created by HoangPhuong on 11/16/2017.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;



public class SplashActivity extends Activity {
    ImageView imgvLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        CountDownTimer count = new CountDownTimer(2000, 2000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO: Nothing
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(SplashActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        count.start();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME) {

        }
        return false;
    }

}

