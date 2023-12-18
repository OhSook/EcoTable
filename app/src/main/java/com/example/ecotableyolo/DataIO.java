package com.example.ecotableyolo;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DataIO {
    public ArrayList<String[]> readFromCSV(View itemView) {
        File privateDir = itemView.getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File csvFile = new File(privateDir, "data.csv");

        ArrayList<String[]> csvData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // CSV 필드가 쉼표로 구분되어 있다고 가정합니다.
                String[] fields = line.split(",");
                csvData.add(fields);
            }
            Log.d("check", csvData.get(0)[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvData;
    }

    public ArrayList<String[]> readFromCSV(String path) {

        ArrayList<String[]> csvData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                // CSV 필드가 쉼표로 구분되어 있다고 가정합니다.
                String[] fields = line.split(",");
                csvData.add(fields);
            }
            Log.d("check", csvData.get(0)[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvData;
    }

    public void saveToCSV(View itemView, String csvRow) {
        try {
            File privateDir = itemView.getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File csvFile = new File(privateDir, "data.csv");

            if (!csvFile.exists()) {
                csvFile.createNewFile();
                FileWriter writer = new FileWriter(csvFile, true);
                writer.append("이름,만료일\n");
                writer.close();
            }

            FileWriter writer = new FileWriter(csvFile, true);
            writer.append(csvRow);
            writer.close();

            Log.d("CSV", "데이터가 CSV 파일에 저장되었습니다");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CSV", "데이터를 CSV 파일에 저장하는 중 오류가 발생했습니다");
        }
    }
}
