package com.example.ecotableyolo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class RecipeActivity extends AppCompatActivity {
    private static final String DRIVER = "oracle.jdbc.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@IPv4Address:port:xe";
    private static final String USERNAME = "dbName";
    private static final String PASSWORD = "password";
    private Connection connection;
    String foodNames;
    String durl;
    ImageView imageView;
    ImageView levelImg;
    Bitmap bitmap;
    TextView source_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        //보안정책

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        // Intent로부터 데이터 받기
        Intent intent = getIntent();
        foodNames = intent.getStringExtra("menuName");

        TextView title_tv = findViewById(R.id.titleTv);
        TextView level_tv = findViewById(R.id.dateTv);
        TextView ingre_tv = findViewById(R.id.priceTv);
        TextView content_tv = findViewById(R.id.mainTv);
        source_tv = findViewById(R.id.sourceTv); // 출처

        title_tv.setText(foodNames);
        levelImg = findViewById(R.id.levelImg);
        imageView = findViewById(R.id.urlImg);
        //안드로이드에서 네트워크와 관련된 작업을 할 때,
        //반드시 메인 Thread가 아닌 별도의 작업 Thread를 생성하여 작업해야 한다.

        try {
            Class.forName(DRIVER);
            this.connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            Statement statement = connection.createStatement();
            StringBuffer urlBuffer = new StringBuffer();
            StringBuffer titleBuffer = new StringBuffer();
            StringBuffer levelBuffer = new StringBuffer();
            StringBuffer ingreBuffer = new StringBuffer();
            StringBuffer contentBuffer = new StringBuffer();
            StringBuffer sourceBuffer = new StringBuffer();

            ResultSet resultSet = statement.executeQuery("SELECT menu_name, menu_recipe, menu_level, ingre_real, menu_img, img_source " +
                    "FROM menu " +
                    "WHERE menu_name = '" + foodNames + "'");

            while(resultSet.next()){
                titleBuffer.append(resultSet.getString(1));
                contentBuffer.append(resultSet.getString(2));
                levelBuffer.append(resultSet.getString(3));
                ingreBuffer.append(resultSet.getString(4));
                urlBuffer.append(resultSet.getString(5));
                sourceBuffer.append(resultSet.getString(6));
            }
            title_tv.setText(titleBuffer.toString());
            content_tv.setText(contentBuffer.toString());
            ingre_tv.setText(ingreBuffer.toString());
            source_tv.setText(sourceBuffer.toString());
            durl =  urlBuffer.toString();
            String level = levelBuffer.toString();


            if (level.contains("1")){
                levelImg.setImageResource(R.drawable.level1);
            }else if(level.contains("2")){
                levelImg.setImageResource(R.drawable.level2);
            }else if(level.contains("3")){
                levelImg.setImageResource(R.drawable.level3);
            }

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


        // 새로운 스레드를 생성하고 이미지 url 불러오기
        Thread uThread = new Thread() {
            @Override
            public void run() {
                try {

                    java.net.URL url = new URL(durl);

                    // web에서 이미지를 가져와 ImageView에 저장할 Bitmap을 만든다.
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true); // 서버로부터 응답 수신
                    conn.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);

                }catch (MalformedURLException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        uThread.start();

        try{
            //메인 Thread는 별도의 작업 Thread가 작업을 완료할 때까지 대기해야 한다.
            //join() 호출하여 별도의 작업 Thread가 종료될 때까지 메인 Thread가 기다리도록 한다.
            //join() 메서드는 InterruptedException을 발생시킨다.
            uThread.join();

            //작업 Thread에서 이미지를 불러오는 작업을 완료한 뒤
            //UI 작업을 할 수 있는 메인 Thread에서 ImageView에 이미지 지정
            imageView.setImageBitmap(bitmap);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    public void onClickSource(View view){
        String src = source_tv.getText().toString();
        Intent urlintent = new Intent(Intent.ACTION_VIEW, Uri.parse(src));
        startActivity(urlintent);
    }
}