package com.iot.lostfoundapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ItemAdapter
        extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {


    private final Context context;

    private ArrayList<Item> itemList;

    private final OnItemClickListener listener;


    // =================================================
    // 클릭 인터페이스
    // =================================================

    public interface OnItemClickListener {

        void onItemClick(
                Item item
        );
    }


    // =================================================
    // 생성자
    // =================================================

    public ItemAdapter(
            Context context,
            ArrayList<Item> itemList,
            OnItemClickListener listener
    ) {

        this.context =
                context;

        this.itemList =
                itemList;

        this.listener =
                listener;
    }


    // =================================================
    // ViewHolder 생성
    // =================================================

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {


        View view =
                LayoutInflater
                        .from(context)
                        .inflate(
                                R.layout.item_row,
                                parent,
                                false
                        );


        return new ItemViewHolder(
                view
        );
    }


    // =================================================
    // 데이터 표시
    // =================================================

    @Override
    public void onBindViewHolder(
            @NonNull ItemViewHolder holder,
            int position
    ) {


        Item item =
                itemList.get(
                        position
                );


        // =================================================
        // 사진
        // =================================================

        /*
         * RecyclerView 재사용 때문에
         * 먼저 무조건 기본 이미지 설정
         */
        holder.imageItem.setImageResource(
                R.drawable.ic_item_placeholder
        );


        String imageUri =
                item.getImageUri();


        if (imageUri != null &&
                !imageUri.trim().isEmpty()) {


            try {

                holder.imageItem.setPadding(
                        0,
                        0,
                        0,
                        0
                );


                holder.imageItem.setImageURI(
                        Uri.parse(
                                imageUri
                        )
                );


            } catch (Exception e) {


                showDefaultImage(
                        holder
                );
            }


        } else {


            showDefaultImage(
                    holder
            );
        }


        // =================================================
        // 분실 / 습득
        // =================================================

        if ("LOST".equals(
                item.getType()
        )) {

            holder.textType.setText(
                    "분실"
            );

        } else {

            holder.textType.setText(
                    "습득"
            );
        }


        // =================================================
        // 물품명
        // =================================================

        holder.textName.setText(
                item.getName()
        );


        // =================================================
        // 상태
        // =================================================

        holder.textStatus.setText(
                getStatusText(
                        item
                )
        );


        // =================================================
        // 기타 정보
        // =================================================

        holder.textCategory.setText(
                "카테고리 · " +
                        safeText(
                                item.getCategory()
                        )
        );


        holder.textLocation.setText(
                "장소 · " +
                        safeText(
                                item.getLocation()
                        )
        );


        holder.textDate.setText(
                safeText(
                        item.getDate()
                )
        );


        // =================================================
        // 클릭
        // =================================================

        holder.itemView.setOnClickListener(
                view -> {

                    if (listener != null) {

                        listener.onItemClick(
                                item
                        );
                    }
                }
        );
    }


    // =================================================
    // 기본 이미지
    // =================================================

    private void showDefaultImage(
            ItemViewHolder holder
    ) {


        holder.imageItem.setPadding(
                15,
                15,
                15,
                15
        );


        holder.imageItem.setImageResource(
                R.drawable.ic_item_placeholder
        );
    }


    // =================================================
    // 상태 한글 표시
    // =================================================

    private String getStatusText(
            Item item
    ) {


        boolean matched =
                "MATCHED".equals(
                        item.getStatus()
                );


        if ("LOST".equals(
                item.getType()
        )) {


            if (matched) {

                return "습득물 확인";
            }


            return "찾는 중";


        } else {


            if (matched) {

                return "분실자 확인";
            }


            return "보관 중";
        }
    }


    // =================================================
    // NULL 처리
    // =================================================

    private String safeText(
            String text
    ) {


        return text == null
                ? ""
                : text;
    }


    // =================================================
    // 목록 갱신
    // =================================================

    public void updateList(
            ArrayList<Item> newList
    ) {


        if (newList == null) {

            itemList =
                    new ArrayList<>();

        } else {

            itemList =
                    newList;
        }


        notifyDataSetChanged();
    }


    // =================================================
    // 개수
    // =================================================

    @Override
    public int getItemCount() {

        return itemList == null
                ? 0
                : itemList.size();
    }


    // =================================================
    // ViewHolder
    // =================================================

    static class ItemViewHolder
            extends RecyclerView.ViewHolder {


        ImageView imageItem;

        TextView textType;
        TextView textName;
        TextView textStatus;
        TextView textCategory;
        TextView textLocation;
        TextView textDate;


        public ItemViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);


            imageItem =
                    itemView.findViewById(
                            R.id.imageItem
                    );


            textType =
                    itemView.findViewById(
                            R.id.textType
                    );


            textName =
                    itemView.findViewById(
                            R.id.textName
                    );


            textStatus =
                    itemView.findViewById(
                            R.id.textStatus
                    );


            textCategory =
                    itemView.findViewById(
                            R.id.textCategory
                    );


            textLocation =
                    itemView.findViewById(
                            R.id.textLocation
                    );


            textDate =
                    itemView.findViewById(
                            R.id.textDate
                    );
        }
    }
}