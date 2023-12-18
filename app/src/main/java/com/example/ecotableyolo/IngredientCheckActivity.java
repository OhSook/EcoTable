package com.example.ecotableyolo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 요리 추천해주는 액티비티
public class IngredientCheckActivity extends AppCompatActivity {
    private static final String DRIVER = "oracle.jdbc.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@IPv4Address:port:xe";
    private static final String USERNAME = "dbName";
    private static final String PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_check);

        // 보안정책
        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(threadPolicy);

        // Intent로부터 데이터 받기
        Intent intent = getIntent();
        String[] foodNames = intent.getStringArrayExtra("foodNames");
        String query = new String();
        try {
            // JDBC 드라이버 로드
            Class.forName(DRIVER);
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            if (foodNames.length == 5){
                query = "SELECT menu_name, menu_img\n" +
                        "FROM menu\n" +
                        "WHERE menu_ingredient LIKE ? OR menu_ingredient LIKE ? OR menu_ingredient LIKE ? OR menu_ingredient LIKE ? OR menu_ingredient LIKE ?\n" +
                        "ORDER BY \n" +
                        "CASE \n" +
                        "WHEN menu_ingredient LIKE ? AND menu_ingredient LIKE ? AND menu_ingredient LIKE ? AND menu_ingredient LIKE ? AND menu_ingredient LIKE ? THEN 1\n" +
                        "WHEN menu_ingredient LIKE ? OR menu_ingredient LIKE ? OR menu_ingredient LIKE ? OR menu_ingredient LIKE ? OR menu_ingredient LIKE ? THEN 2\n" +
                        "ELSE 3\n" +
                        "END";
            }
            else if (foodNames.length == 4){
                query = "SELECT menu_name, menu_img\n" +
                        "FROM menu\n" +
                        "WHERE menu_ingredient LIKE ? OR menu_ingredient LIKE ? OR menu_ingredient LIKE ? OR menu_ingredient LIKE ?\n" +
                        "ORDER BY \n" +
                        "CASE \n" +
                        "WHEN menu_ingredient LIKE ? AND menu_ingredient LIKE ? AND menu_ingredient LIKE ? AND menu_ingredient LIKE ? THEN 1\n" +
                        "WHEN menu_ingredient LIKE ? OR menu_ingredient LIKE ? OR menu_ingredient LIKE ? OR menu_ingredient LIKE ? THEN 2\n" +
                        "ELSE 3\n" +
                        "END";
            }
            else if(foodNames.length == 3){
                query = "SELECT menu_name, menu_img\n" +
                        "FROM menu\n" +
                        "WHERE menu_ingredient LIKE ? OR menu_ingredient LIKE ? OR menu_ingredient LIKE ?\n" +
                        "ORDER BY \n" +
                        "CASE \n" +
                        "WHEN menu_ingredient LIKE ? AND menu_ingredient LIKE ? AND menu_ingredient LIKE ? THEN 1\n" +
                        "WHEN menu_ingredient LIKE ? OR menu_ingredient LIKE ? OR menu_ingredient LIKE ? THEN 2\n" +
                        "ELSE 3\n" +
                        "END";
            }
            else if(foodNames.length == 2){
                query = "SELECT menu_name, menu_img\n" +
                        "FROM menu\n" +
                        "WHERE menu_ingredient LIKE ? OR menu_ingredient LIKE ?\n" +
                        "ORDER BY \n" +
                        "CASE \n" +
                        "WHEN menu_ingredient LIKE ? AND menu_ingredient LIKE ? THEN 1\n" +
                        "WHEN menu_ingredient LIKE ? OR menu_ingredient LIKE ? THEN 2\n" +
                        "ELSE 3\n" +
                        "END";
            }
            else if (foodNames.length == 1){
                query = "SELECT menu_name, menu_img\n" +
                        "FROM menu\n" +
                        "WHERE menu_ingredient LIKE ?\n" +
                        "ORDER BY \n" +
                        "CASE \n" +
                        "WHEN menu_ingredient LIKE ? THEN 1\n" +
                        "WHEN menu_ingredient LIKE ? THEN 2\n" +
                        "ELSE 3\n" +
                        "END";
            }

            PreparedStatement ps = connection.prepareStatement(query);
            for(int i = 1; i < foodNames.length+1; i++){
                for(int j = 0; j<3; j++){
                    int setIndex = i+j*foodNames.length;
                    String parameterValue = "%" + foodNames[(i - 1) % foodNames.length] + "%";
                    ps.setString(setIndex, parameterValue);
                }
            }
            ResultSet resultSet = ps.executeQuery();

            ArrayList<IngreItem> menuItems = new ArrayList<>();

            while (resultSet.next()) {
                String dishName = resultSet.getString("menu_name");
                String imageUrl = resultSet.getString("menu_img");
                menuItems.add(new IngreItem(dishName,imageUrl));
                Log.d("YourTag", "ArrayList content: " + dishName);
            }

            // RecyclerView 설정 부분
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // RecyclerView 어댑터 설정
            IngreAdapter menuAdapter = new IngreAdapter(menuItems, menuName -> {
                // 클릭된 아이템의 정보를 다음 액티비티로 전달
                Intent intent1 = new Intent(IngredientCheckActivity.this, RecipeActivity.class);
                intent1.putExtra("menuName", menuName);
                // 다른 필요한 정보들도 추가로 넘겨줄 수 있습니다.
                startActivity(intent1);
            });

            recyclerView.setAdapter(menuAdapter);
            // RecyclerView 설정 부분 끝

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
