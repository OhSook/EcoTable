package com.example.ecotableyolo;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CustomAdapterSave extends RecyclerView.Adapter<CustomAdapterSave.ViewHolder> {

    private ArrayList<String> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView setName;
        private final Button saveBtn1;
        private final TextView saveDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Define click listener for the ViewHolder's View
            setName = itemView.findViewById(R.id.nameSpace);
            saveBtn1 = itemView.findViewById(R.id.saveBtn);
            saveDate = itemView.findViewById(R.id.expDate);
            DataIO dio = new DataIO();

            // 아이템 클릭 시 동작 정의
            // 예: 특정 작업 수행, 다른 화면으로 이동 등
            saveDate.setOnClickListener(view -> {
                openCalender(saveDate);
                saveBtn1.setVisibility(View.VISIBLE);
            });
            saveBtn1.setOnClickListener(view -> {
                // 뷰에서 값 가져오기
                String name = setName.getText().toString();
                String expDate = saveDate.getText().toString();

                // CSV 문자열 생성
                String csvRow = name + "," + expDate + "\n";

                // CSV 파일을 앱의 개인 디렉터리에 저장
                dio.saveToCSV(itemView, csvRow);

                int position = getBindingAdapterPosition();
                localDataSet.remove(position);

                // 어댑터에게 데이터가 변경되었음을 알림
                notifyItemRemoved(position);

                //저장된 데이터 갱신
                File privateDir = view.getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                File csvFile = new File(privateDir, "data.csv");
                ArrayList<String[]> newData = dio.readFromCSV(String.valueOf(csvFile));

                // 어댑터에 새로운 데이터를 설정한다
                CustomAdapterLoad.setDataSet(newData);

                // 리사이클러뷰에 변경 사항을 알린다
                notifyDataSetChanged();

                int pos = getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    // 리스너 객체의 메서드 호출.
                    if (mListener != null) {
                        mListener.onItemClick(view, pos);
                    }
                }
            });
        }
    }


    private void openCalender(final TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int putYear = calendar.get(Calendar.YEAR);
        int putMonth = calendar.get(Calendar.MONTH);
        int putDay = calendar.get(Calendar.DAY_OF_MONTH);
        Context context = textView.getContext();

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (datePicker, year, month, dayOfMonth) -> {
            // 선택된 날짜를 TextView에 설정
            String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%02d", year, month + 1, dayOfMonth);
            textView.setText(selectedDate);
        }, putYear, putMonth, putDay);

        datePickerDialog.show();
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public CustomAdapterSave(ArrayList<String> dataSet) {
        this.localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_save, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        int i = position;
        String text = localDataSet.get(i);
        viewHolder.setName.setText(text);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
    //클릭이벤트 코드
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }
}