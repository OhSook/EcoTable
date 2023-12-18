package com.example.ecotableyolo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RefrigeratorMainActivity extends AppCompatActivity {
    private final int IMAGE_PICK = 100;
    ImageView imageView;
    Bitmap bitmap;
    Yolov5TFLoteDetector yolov5TFLiteDetector;
    Paint boxPaint = new Paint();
    Paint textPain = new Paint();
    TextView labels;

    //    String[] labelss;
//    String label;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refrigerator_main);
        imageView = findViewById(R.id.imageView);
        labels = findViewById(R.id.textView_label);


        yolov5TFLiteDetector = new Yolov5TFLoteDetector();
        yolov5TFLiteDetector.setModelFile("best-fp16_more2.tflite");
        yolov5TFLiteDetector.initialModel(this);

        boxPaint.setStrokeWidth(5);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setColor(Color.RED);

        textPain.setTextSize(50);
        textPain.setColor(Color.GREEN);
        textPain.setStyle(Paint.Style.FILL);

    }

    public void selectImage(View view){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK);
        Button visibleBtn = findViewById(R.id.predict);
        visibleBtn.setVisibility(View.VISIBLE);
    }

    public void predict(View view){
        ArrayList<Recognition> recognitions =  yolov5TFLiteDetector.detect(bitmap);
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);


        // 중복된 레이블을 저장하기 위한 Set
        Set<String> uniqueLabels = new HashSet<>();

        StringBuilder recognitionNames = new StringBuilder();

        for(Recognition recognition: recognitions){
            if(recognition.getConfidence() > 0.4){
                RectF location = recognition.getLocation();
                canvas.drawRect(location, boxPaint);

                String labelName = recognition.getLabelName();

                // 이미 출력한 레이블인지 확인하고 중복되지 않은 경우에만 추가
                if (!uniqueLabels.contains(labelName)) {
                    // 레이블 이름과 신뢰도를 문자열에 추가
                    recognitionNames.append(labelName)
//                            .append(": ")
//                            .append(recognition.getConfidence())
                            .append("\n");

                    uniqueLabels.add(labelName); // 중복 체크를 위해 Set에 레이블 추가
                }

                canvas.drawText(recognition.getLabelName() + ":" + recognition.getConfidence(), location.left, location.top, textPain);
            }
        }

        // Assuming you have a TextView named "textView" in your layout
        TextView textView = findViewById(R.id.textView_label);
        textView.setText(recognitionNames.toString());

        imageView.setImageBitmap(mutableBitmap);
        Button saveBtn = findViewById(R.id.save_data);
        saveBtn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_PICK && data != null){
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onClickedSaveBtn(View view){
        Intent openSavePage = new Intent(this, DataInOutActivity.class);
        TextView textView = findViewById(R.id.textView_label);
        openSavePage.putExtra("재료", textView.getText().toString());
        startActivity(openSavePage);
    }


}
