package com.example.ecotableyolo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {

    TextView tvCook;
    TextView refrebtn;
    TextView toptv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView)findViewById(R.id.Textview);
        tv.setSelected(true);

       // Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.blink);

        //tv.startAnimation(animation);
    }

    public void onClickedScan(View view){
        tvCook = findViewById(R.id.tvCook);
        Intent intent = new Intent(MainActivity.this,ScanActivity.class);
        startActivity(intent);

    }


    public void onClickedRefri(View view){
        refrebtn = findViewById(R.id.editTextText2);
        Intent intent = new Intent(MainActivity.this,DataInOutActivity.class);
        intent.putExtra("재료", "재료");
        startActivity(intent);

    }
}