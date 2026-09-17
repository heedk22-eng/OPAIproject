package com.iot.lostfoundapp;

import android.content.Intent;

import android.os.Bundle;

import android.widget.Button;

import android.widget.EditText;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/*
 * ItemListActivity
 *
 * 등록되어 있는 물품을 목록으로 보여주는 화면이다.
 *
 * 주요 기능
 *
 * REQ-003 전체 목록 조회
 * REQ-004 분실/습득 구분 조회
 * REQ-005 검색
 * REQ-006 상세정보 화면 이동
 */
public class ItemListActivity extends AppCompatActivity {

    // 검색어 입력창
    private EditText editSearch;


    // 검색 버튼
    private Button btnSearch;

    // 전체 목록 버튼
    private Button btnAll;

    // 분실물만 보기 버튼
    private Button btnLost;

    // 습득물만 보기 버튼
    private Button btnFound;


    /*
     * 물품 목록을 출력할 RecyclerView
     */
    private RecyclerView recyclerView;


    /*
     * RecyclerView와 Item 데이터를 연결해주는 Adapter
     */
    private ItemAdapter adapter;


    /*
     * 기능을 처리할 ItemService
     */
    private ItemService itemService;


    /*
     * 현재 화면에 표시할 Item 목록
     */
    private ArrayList<Item> itemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // XML 화면 연결
        setContentView(R.layout.activity_item_list);


        /*
         * XML View 연결
         */

        editSearch =
                findViewById(R.id.editSearch);

        btnSearch =
                findViewById(R.id.btnSearch);

        btnAll =
                findViewById(R.id.btnAll);

        btnLost =
                findViewById(R.id.btnLost);

        btnFound =
                findViewById(R.id.btnFound);

        recyclerView =
                findViewById(R.id.recyclerView);


        /*
         * ItemService 생성
         */
        itemService = new ItemService(this);


        /*
         * 처음에는 빈 ArrayList 생성
         */
        itemList = new ArrayList<>();


        /*
         * RecyclerView의 배치 형태 설정
         *
         * LinearLayoutManager
         * → 위에서 아래로 한 줄씩 목록을 표시한다.
         */
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );


        /*
         * ItemAdapter 생성
         *
         * 여기에서 물품을 클릭하면
         * openDetail() 메소드를 실행하도록 한다.
         *
         * ItemAdapter는 다음 단계에서
         * 이 구조에 맞춰서 만들어주면 된다.
         */
        adapter = new ItemAdapter(

                this,

                itemList,

                item -> {

                    // 클릭한 Item의 상세화면으로 이동
                    openDetail(item.getId());
                }
        );


        /*
         * RecyclerView에 Adapter 연결
         */
        recyclerView.setAdapter(adapter);


        /*
         * 처음 화면을 열었을 때
         * 전체 물품 목록 조회
         */
        loadItems();


        /*
         * 전체 버튼
         */
        btnAll.setOnClickListener(view -> {

            loadItems();
        });


        /*
         * 분실물 버튼
         */
        btnLost.setOnClickListener(view -> {

            loadLostItems();
        });


        /*
         * 습득물 버튼
         */
        btnFound.setOnClickListener(view -> {

            loadFoundItems();
        });


        /*
         * 검색 버튼
         */
        btnSearch.setOnClickListener(view -> {

            searchItems();
        });
    }


    /*
     * 전체 물품 조회
     */
    private void loadItems() {

        // DB에 저장된 모든 Item 가져오기
        ArrayList<Item> items =
                itemService.getAllItems();


        /*
         * Adapter의 목록 갱신
         */
        adapter.updateList(items);


        /*
         * 데이터가 하나도 없는 경우
         */
        if (items.isEmpty()) {

            Toast.makeText(
                    this,
                    "등록된 물품이 없습니다.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    /*
     * 분실물만 조회
     */
    private void loadLostItems() {

        /*
         * type = LOST인 Item만 가져온다.
         */
        ArrayList<Item> items =
                itemService.getItemsByType("LOST");


        // RecyclerView 갱신
        adapter.updateList(items);


        // 결과가 없는 경우
        if (items.isEmpty()) {

            Toast.makeText(
                    this,
                    "등록된 분실물이 없습니다.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    /*
     * 습득물만 조회
     */
    private void loadFoundItems() {

        /*
         * type = FOUND인 Item만 가져온다.
         */
        ArrayList<Item> items =
                itemService.getItemsByType("FOUND");


        // 목록 갱신
        adapter.updateList(items);


        // 결과가 없는 경우
        if (items.isEmpty()) {

            Toast.makeText(
                    this,
                    "등록된 습득물이 없습니다.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    /*
     * 검색 기능
     */
    private void searchItems() {

        // 사용자가 입력한 검색어
        String keyword =
                editSearch.getText().toString().trim();


        /*
         * 검색어가 없는 경우
         * 전체 목록을 다시 보여준다.
         */
        if (keyword.isEmpty()) {

            loadItems();

            return;
        }


        /*
         * 검색어에 해당하는 Item 조회
         */
        ArrayList<Item> items =
                itemService.searchItems(keyword);


        // 검색결과 목록 갱신
        adapter.updateList(items);


        /*
         * 검색 결과가 없는 경우
         */
        if (items.isEmpty()) {

            Toast.makeText(
                    this,
                    "검색 결과가 없습니다.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    /*
     * 상세정보 화면으로 이동
     */
    private void openDetail(int itemId) {

        /*
         * DetailActivity로 이동할 Intent 생성
         */
        Intent intent =
                new Intent(
                        ItemListActivity.this,
                        DetailActivity.class
                );


        /*
         * 선택한 물품의 ID를 전달한다.
         */
        intent.putExtra(
                "ITEM_ID",
                itemId
        );


        // DetailActivity 실행
        startActivity(intent);
    }


    /*
     * DetailActivity에서 수정 또는 삭제 후
     * ItemListActivity로 다시 돌아왔을 때 실행된다.
     */
    @Override
    protected void onResume() {

        super.onResume();


        /*
         * 목록을 다시 불러와서
         * 수정/삭제 내용을 반영한다.
         *
         * 단, adapter가 생성된 이후에만 실행
         */
        if (adapter != null) {

            loadItems();
        }
    }
}