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
        extends RecyclerView.Adapter<
        ItemAdapter.ItemViewHolder> {

    private final Context context;

    private final ArrayList<Item> itemList;

    private final OnItemClickListener listener;


    public interface OnItemClickListener {

        void onItemClick(Item item);
    }


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


    @Override
    public void onBindViewHolder(
            @NonNull ItemViewHolder holder,
            int position
    ) {

        Item item =
                itemList.get(
                        position
                );


        /*
         * 분실 / 습득
         */
        if ("LOST".equals(
                item.getType()
        )) {

            holder.textType
                    .setText(
                            "분실"
                    );

        } else {

            holder.textType
                    .setText(
                            "습득"
                    );
        }


        holder.textName
                .setText(
                        item.getName()
                );


        /*
         * 상태
         */
        String statusText;


        if ("MATCHED".equals(
                item.getStatus()
        )) {

            if ("LOST".equals(
                    item.getType()
            )) {

                statusText =
                        "습득물 확인";

            } else {

                statusText =
                        "분실자 확인";
            }

        } else {

            if ("LOST".equals(
                    item.getType()
            )) {

                statusText =
                        "찾는 중";

            } else {

                statusText =
                        "보관 중";
            }
        }


        holder.textStatus
                .setText(
                        "상태 : " +
                                statusText
                );


        holder.textCategory
                .setText(
                        "카테고리 : " +
                                item.getCategory()
                );


        holder.textLocation
                .setText(
                        "장소 : " +
                                item.getLocation()
                );


        holder.textDate
                .setText(
                        "날짜 : " +
                                item.getDate()
                );


        /*
         * 사진
         */
        String imageUri =
                item.getImageUri();


        if (imageUri != null &&
                !imageUri.isEmpty()) {

            try {

                holder.imageItem
                        .setImageURI(
                                Uri.parse(
                                        imageUri
                                )
                        );

            } catch (Exception e) {

                holder.imageItem
                        .setImageResource(
                                android.R.drawable
                                        .ic_menu_gallery
                        );
            }

        } else {

            holder.imageItem
                    .setImageResource(
                            android.R.drawable
                                    .ic_menu_gallery
                    );
        }


        /*
         * 클릭 → 상세
         */
        holder.itemView
                .setOnClickListener(
                        view -> {

                            if (listener != null) {

                                listener
                                        .onItemClick(
                                                item
                                        );
                            }
                        }
                );
    }


    @Override
    public int getItemCount() {

        return itemList.size();
    }


    /*
     * 목록 갱신
     */
    public void updateList(
            ArrayList<Item> newList
    ) {

        itemList.clear();

        itemList.addAll(
                newList
        );

        notifyDataSetChanged();
    }


    public static class ItemViewHolder
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