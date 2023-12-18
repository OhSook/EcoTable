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

public class ScanActivity extends AppCompatActivity {
    private final int IMAGE_PICK = 100;
    ImageView imageView;
    Bitmap bitmap;
    Yolov5TFLoteDetector yolov5TFLiteDetector;
    Paint boxPaint = new Paint();
    Paint textPaint = new Paint();
    Button capbtn;
    Button checkbtn;
    Button gallarybtn;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //permission
        getPermission();

        imageView = findViewById(R.id.foodView);

        yolov5TFLiteDetector = new Yolov5TFLoteDetector();
        yolov5TFLiteDetector.setModelFile("best-fp16_more2.tflite");
        yolov5TFLiteDetector.initialModel(this);

        boxPaint.setStrokeWidth(5);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setColor(Color.RED);

        textPaint.setTextSize(50);
        textPaint.setColor(Color.GREEN);
        textPaint.setStyle(Paint.Style.FILL);

        capbtn = findViewById(R.id.btn);
        //checkbtn = findViewById(R.id.ingreBtn);

        capbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageSourceDialog();
            }


        });

    }
    private void showImageSourceDialog() {
        // 다이얼로그를 통해 갤러리 또는 카메라 선택
        new AlertDialog.Builder(this)
                .setTitle("Import Image")
                .setMessage("Select how to import the image")
                .setPositiveButton("Photo Album", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 갤러리로 이동
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, IMAGE_PICK);
                    }
                })
                .setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 카메라로 이동
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 12);
                    }
                })
                .show();
    }

    void getPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.CAMERA}, 11);
        }
    }

    public void selectImage(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK);
    }

    public void predict(View view) {
        ArrayList<Recognition> recognitions = yolov5TFLiteDetector.detect(bitmap);
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        // 중복된 레이블을 저장하기 위한 Set
        Set<String> uniqueLabels = new HashSet<>();

        for (Recognition recognition : recognitions) {
            if (recognition.getConfidence() > 0.4) {
                String labelName = recognition.getLabelName();

                // 이미 출력한 레이블인지 확인하고 중복되지 않은 경우에만 추가
                if (!uniqueLabels.contains(labelName)) {
                    uniqueLabels.add(labelName); // 중복 체크를 위해 Set에 레이블 추가

                    RectF location = recognition.getLocation();
                    canvas.drawRect(location, boxPaint);
                    canvas.drawText(recognition.getLabelName() + ":" + recognition.getConfidence(), location.left, location.top, textPaint);
                }
            }
        }

        // 문자열 배열로 변환
        String[] foodNames = uniqueLabels.toArray(new String[0]);

        // 팝업 창 생성
        new AlertDialog.Builder(this)
                .setTitle("Check the menu")
                .setMessage("Recognized food: " + Arrays.toString(foodNames) + "\ncontinue")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // "네"를 눌렀을 때 다음 액티비티로 이동
                        Intent intent = new Intent(ScanActivity.this, IngredientCheckActivity.class);
                        intent.putExtra("foodNames", foodNames);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // "아니오"를 눌렀을 때 원하는 동작 수행
                        // 여기에 추가적인 동작을 작성하세요.
                        dialog.dismiss();
                    }
                })
                .show();

        // 이미지뷰에 결과 표시
        imageView.setImageBitmap(mutableBitmap);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 12 && resultCode == RESULT_OK && data != null) {
            // 카메라로 촬영한 경우
            Bundle extras = data.getExtras();
            if (extras != null) {
                bitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(bitmap);
            }
        } else if (requestCode == IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            // 갤러리에서 선택한 경우
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void onClickedRefrig(View view){
        Intent intent = new Intent(ScanActivity.this,DataInOutActivity.class);
        intent.putExtra("재료", "재료");
        startActivity(intent);

    }
}

