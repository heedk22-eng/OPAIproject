package com.iot.lostfoundapp;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/*
 * ItemAdapter
 *
 * ArrayList<Item>에 들어있는 데이터를
 * RecyclerView 화면에 출력하는 클래스
 */
public class ItemAdapter
        extends RecyclerView.Adapter<
        ItemAdapter.ItemViewHolder> {


    // Context
    private Context context;


    // RecyclerView에 표시할 물품 목록
    private ArrayList<Item> itemList;


    /*
     * 물품 클릭 이벤트를
     * Activity로 전달하기 위한 Listener
     */
    private OnItemClickListener listener;


    /*
     * 클릭 이벤트 인터페이스
     */
    public interface OnItemClickListener {

        /*
         * 사용자가 특정 물품을 클릭하면 실행
         */
        void onItemClick(Item item);
    }


    /*
     * 생성자
     */
    public ItemAdapter(
            Context context,
            ArrayList<Item> itemList,
            OnItemClickListener listener
    ) {

        this.context = context;

        this.itemList = itemList;

        this.listener = listener;
    }


    /*
     * RecyclerView의 한 줄 화면을 생성한다.
     */
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        /*
         * item_row.xml을 실제 View 객체로 변환한다.
         */
        View view =
                LayoutInflater
                        .from(context)
                        .inflate(
                                R.layout.item_row,
                                parent,
                                false
                        );


        return new ItemViewHolder(view);
    }


    /*
     * 현재 위치(position)의 Item 데이터를
     * 화면에 표시한다.
     */
    @Override
    public void onBindViewHolder(
            @NonNull ItemViewHolder holder,
            int position
    ) {

        // 현재 위치의 Item 가져오기
        Item item =
                itemList.get(position);


        /*
         * LOST / FOUND를
         * 사용자에게 한글로 보여준다.
         */
        if ("LOST".equals(item.getType())) {

            holder.textType.setText("분실");

        } else {

            holder.textType.setText("습득");
        }


        // 물품명
        holder.textName.setText(
                item.getName()
        );


        // 카테고리
        holder.textCategory.setText(
                "카테고리 : "
                        + item.getCategory()
        );


        // 장소
        holder.textLocation.setText(
                "장소 : "
                        + item.getLocation()
        );


        // 날짜
        holder.textDate.setText(
                "날짜 : "
                        + item.getDate()
        );


        /*
         * 목록의 물품 하나를 클릭한 경우
         */
        holder.itemView.setOnClickListener(
                view -> {

                    /*
                     * Listener가 존재한다면
                     * 클릭된 Item을 Activity로 전달
                     */
                    if (listener != null) {

                        listener.onItemClick(item);
                    }
                }
        );
    }


    /*
     * RecyclerView에 표시할 데이터 개수
     */
    @Override
    public int getItemCount() {

        return itemList.size();
    }


    /*
     * RecyclerView 목록을 새로운 데이터로 갱신
     */
    public void updateList(
            ArrayList<Item> newList
    ) {

        /*
         * 기존 데이터 제거
         */
        itemList.clear();


        /*
         * 새로운 데이터 추가
         */
        itemList.addAll(newList);


        /*
         * RecyclerView에게
         * 데이터가 변경됐다고 알려준다.
         */
        notifyDataSetChanged();
    }


    /*
     * RecyclerView 한 줄의 View들을
     * 보관하는 ViewHolder
     */
    public static class ItemViewHolder
            extends RecyclerView.ViewHolder {


        TextView textType;

        TextView textName;

        TextView textCategory;

        TextView textLocation;

        TextView textDate;


        public ItemViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);


            /*
             * item_row.xml의 TextView 연결
             */

            textType =
                    itemView.findViewById(
                            R.id.textType
                    );


            textName =
                    itemView.findViewById(
                            R.id.textName
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