package com.example.ecotableyolo;


import android.os.Environment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CustomAdapterLoad extends RecyclerView.Adapter<CustomAdapterLoad.ViewHolder> {

    private static ArrayList<String[]> localDataSet;
    private static SparseBooleanArray checkedItems = new SparseBooleanArray();
    public CustomAdapterLoad(ArrayList<String[]> dataSet) {
        this.localDataSet = dataSet;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView loadName;
        private final TextView loadDate;
        private final CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Define click listener for the ViewHolder's View
            loadName = itemView.findViewById(R.id.nameLoad);
            loadDate = itemView.findViewById(R.id.dateLoad);
            checkBox = itemView.findViewById(R.id.checkBox);

            checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                int position = getBindingAdapterPosition();
                checkedItems.put(position, isChecked);
            });
        }
    }

    public static void setDataSet(ArrayList<String[]> newData) {
        localDataSet = newData;
    }

    public void deleteCheckedItems(DataInOutActivity dataIOActivity) {
        for (int i = localDataSet.size() - 1; i >= 0; i--) {
            if (checkedItems.get(i)) {
                localDataSet.remove(i+1);
            }
        }
        // 체크박스 상태 초기화
        checkedItems.clear();
        notifyDataSetChanged();
        // CSV 파일에서도 해당 데이터를 삭제하는 로직 추가
        File csvFile = new File(dataIOActivity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "data.csv");
        saveToCSV(csvFile, localDataSet);
    }
    public String[] getCheckedLoadNames() {
        ArrayList<String> checkedLoadNames = new ArrayList<>();
        for (int i = 0; i < localDataSet.size()-1; i++) {
            if (checkedItems.get(i)) {
                checkedLoadNames.add(localDataSet.get(i+1)[0]); // loadName이 인덱스 0에 있다고 가정합니다.
            }
        }
        return checkedLoadNames.toArray(new String[0]);
    }

    private void saveToCSV(File csvFile, ArrayList<String[]> data) {
        // 업데이트된 데이터를 CSV 파일에 쓰는 로직을 구현한다
        // ...

        // 예시: CSV 파일에 데이터를 쓰는 코드
        try (FileWriter writer = new FileWriter(csvFile)) {
            for (String[] row : data) {
                writer.write(String.join(",", row) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_read, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        int i = position+1;
        viewHolder.loadName.setText(localDataSet.get(i)[0]);
        viewHolder.loadDate.setText("EXP "+localDataSet.get(i)[1]);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size()-1;
    }
}
