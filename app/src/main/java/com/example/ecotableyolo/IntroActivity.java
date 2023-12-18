package com.example.ecotableyolo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        TextView title = findViewById(R.id.title);
        TextView title2 = findViewById(R.id.title2);
        ImageView img = findViewById(R.id.imageView3);
        ImageView img2 = findViewById(R.id.imageView5);
        ImageView img3 = findViewById(R.id.imageView6);

        Handler handler = new Handler();
        handler.postDelayed(new SplashHandler(),4000);
// Java 코드에서 애니메이션 로드
        Context context;
        Animation animation = AnimationUtils.loadAnimation(IntroActivity.this, R.anim.blink);
        Animation animation2 = AnimationUtils.loadAnimation(IntroActivity.this, R.anim.fadein);


// 뷰에 애니메이션 설정
        title.startAnimation(animation2);
        title2.startAnimation(animation);
        img.startAnimation(animation2);
        img2.startAnimation(animation2);
        img3.startAnimation(animation2);
    }

    public class SplashHandler implements Runnable {

        public void run(){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}