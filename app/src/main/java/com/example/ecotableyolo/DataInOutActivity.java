package com.example.ecotableyolo;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class DataInOutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_in_out);
        //저장할 데이터
        Intent getData = getIntent();
        String scanData = getData.getStringExtra("재료");
        String[] split = scanData.split("\n");
        ArrayList<String> list = new ArrayList<>(Arrays.asList(split));
        inPutDataToRecyclerView1(list);

        //저장된 데이터
        File privateDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File csvFile = new File(privateDir, "data.csv");
        DataIO dio = new DataIO();
        inPutDataToRecyclerView2(dio.readFromCSV(String.valueOf(csvFile)));

    }

    private void inPutDataToRecyclerView1(ArrayList<String> searchDataSet){
        RecyclerView rvSR = findViewById(R.id.saveReady);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DataInOutActivity.this);
        rvSR.setLayoutManager(linearLayoutManager);
        CustomAdapterSave customAdapter = new CustomAdapterSave(searchDataSet);
        rvSR.setAdapter(customAdapter); // 어댑터 설정
    }

    private void inPutDataToRecyclerView2(ArrayList<String[]> searchDataSet){
        RecyclerView rvSR = findViewById(R.id.readData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DataInOutActivity.this);
        rvSR.setLayoutManager(linearLayoutManager);
        CustomAdapterLoad customAdapter = new CustomAdapterLoad(searchDataSet);
        //삭제버튼
        TextView deleteBtn = findViewById(R.id.deleteData);
        deleteBtn.setOnClickListener(view -> {
            customAdapter.deleteCheckedItems(this);
        });

        //레시피로 넘어가기
        TextView goRecipe = findViewById(R.id.searchRecipe);
        goRecipe.setOnClickListener(view -> {
            String[] checkedItems = customAdapter.getCheckedLoadNames();
            for (String data : checkedItems) {
                Log.d("check", "Array Data: " + data);
            }
            Intent intent = new Intent(getApplicationContext(), IngredientCheckActivity.class);
            intent.putExtra("foodNames", checkedItems);
            startActivity(intent);
        });
        rvSR.setAdapter(customAdapter); // 어댑터 설정
    }

    public void onClickedAdd(View view){
        Intent intent = new Intent(this, RefrigeratorMainActivity.class);
        startActivity(intent);
        }
    }

