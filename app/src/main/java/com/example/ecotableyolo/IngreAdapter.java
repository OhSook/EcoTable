package com.example.ecotableyolo;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
// IngreAdapter.java
public class IngreAdapter extends RecyclerView.Adapter<IngreAdapter.ViewHolder> {
    private List<IngreItem> menuItems;
    private OnItemClickListener listener;

    // 생성자
    public IngreAdapter(List<IngreItem> menuItems, OnItemClickListener listener) {
        this.menuItems = menuItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingre_for_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IngreItem menuItem = menuItems.get(position);
        holder.itemTextView.setText("\n"+menuItem.getFoodName());

        // Glide를 사용하여 이미지 로드
        Glide.with(holder.itemView.getContext())
                .load(menuItem.getImageUrl())
                .into(holder.imageView);

        // 클릭 이벤트 설정
        holder.itemView.setOnClickListener(v -> {
            // 클릭된 아이템의 정보를 다음 액티비티로 전달
            if (listener != null) {
                listener.onItemClick(menuItem.getFoodName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    // 뷰 홀더 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            imageView = itemView.findViewById(R.id.dishImg);
        }
    }

    // 아이템 클릭 리스너 인터페이스
    public interface OnItemClickListener {
        void onItemClick(String menuName);
    }
}




