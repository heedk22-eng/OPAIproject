package com.iot.lostfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/*
 * 물품 목록 화면
 *
 * 기능
 * 1. 전체 물품 조회
 * 2. 분실물만 조회
 * 3. 습득물만 조회
 * 4. 내가 등록한 물품만 조회
 * 5. 검색
 * 6. 카테고리 필터
 * 7. 최신순 / 오래된순 / 이름순
 * 8. 상세화면 이동
 */
public class ItemListActivity
        extends AppCompatActivity {


    // =========================================
    // View
    // =========================================

    private EditText editSearch;

    private Button btnSearch;

    private Button btnAll;

    private Button btnLost;

    private Button btnFound;

    private Button btnMyItems;

    private Spinner spinnerCategoryFilter;

    private Spinner spinnerSort;

    private RecyclerView recyclerView;


    // =========================================
    // Adapter / Service
    // =========================================

    private ItemAdapter adapter;

    private ItemService itemService;


    // =========================================
    // 현재 사용자
    // =========================================

    private int currentUserId;


    // =========================================
    // 현재 필터 상태
    // =========================================

    /*
     * ALL
     * LOST
     * FOUND
     */
    private String currentType =
            "ALL";


    /*
     * true
     * → 현재 사용자가 등록한 물품만 표시
     *
     * false
     * → 전체 사용자 물품 표시
     */
    private boolean showMyItemsOnly =
            false;


    // =========================================
    // 카테고리
    // =========================================

    private static final String[] FILTER_CATEGORIES = {

            "전체",

            "전자기기",

            "지갑/카드",

            "의류",

            "가방",

            "문구류",

            "열쇠",

            "기타"
    };


    // =========================================
    // 정렬
    // =========================================

    private static final String[] SORT_OPTIONS = {

            "최신순",

            "오래된순",

            "이름순"
    };


    // =========================================
    // onCreate
    // =========================================

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        /*
         * 반드시 제일 먼저
         */
        super.onCreate(
                savedInstanceState
        );


        /*
         * 반드시 findViewById보다 먼저 실행
         */
        setContentView(
                R.layout.activity_item_list
        );


        // =====================================
        // View 연결
        // =====================================

        editSearch =
                findViewById(
                        R.id.editSearch
                );


        btnSearch =
                findViewById(
                        R.id.btnSearch
                );


        btnAll =
                findViewById(
                        R.id.btnAll
                );


        btnLost =
                findViewById(
                        R.id.btnLost
                );


        btnFound =
                findViewById(
                        R.id.btnFound
                );


        btnMyItems =
                findViewById(
                        R.id.btnMyItems
                );


        spinnerCategoryFilter =
                findViewById(
                        R.id.spinnerCategoryFilter
                );


        spinnerSort =
                findViewById(
                        R.id.spinnerSort
                );


        recyclerView =
                findViewById(
                        R.id.recyclerView
                );


        // =====================================
        // Service
        // =====================================

        itemService =
                new ItemService(this);


        // =====================================
        // 현재 로그인 사용자
        // =====================================

        SessionManager sessionManager =
                new SessionManager(this);


        currentUserId =
                sessionManager.getUserId();


        /*
         * 혹시 사용자 선택 없이 들어온 경우
         */
        if (currentUserId == -1) {

            Intent intent =
                    new Intent(
                            this,
                            NicknameActivity.class
                    );


            startActivity(intent);

            finish();

            return;
        }


        // =====================================
        // 카테고리 Spinner
        // =====================================

        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        FILTER_CATEGORIES
                );


        categoryAdapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item
        );


        spinnerCategoryFilter.setAdapter(
                categoryAdapter
        );


        // =====================================
        // 정렬 Spinner
        // =====================================

        ArrayAdapter<String> sortAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        SORT_OPTIONS
                );


        sortAdapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item
        );


        spinnerSort.setAdapter(
                sortAdapter
        );


        // =====================================
        // RecyclerView
        // =====================================

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );


        adapter =
                new ItemAdapter(

                        this,

                        new ArrayList<>(),

                        /*
                         * 물품 클릭
                         */
                        item ->
                                openDetail(
                                        item.getId()
                                )
                );


        recyclerView.setAdapter(
                adapter
        );


        // =====================================
        // 검색
        // =====================================

        btnSearch.setOnClickListener(
                view ->
                        applyFilters()
        );


        // =====================================
        // 전체
        // =====================================

        btnAll.setOnClickListener(
                view -> {

                    /*
                     * 전체 사용자
                     */
                    showMyItemsOnly =
                            false;


                    /*
                     * 모든 타입
                     */
                    currentType =
                            "ALL";


                    applyFilters();
                }
        );


        // =====================================
        // 분실
        // =====================================

        btnLost.setOnClickListener(
                view -> {

                    showMyItemsOnly =
                            false;


                    currentType =
                            "LOST";


                    applyFilters();
                }
        );


        // =====================================
        // 습득
        // =====================================

        btnFound.setOnClickListener(
                view -> {

                    showMyItemsOnly =
                            false;


                    currentType =
                            "FOUND";


                    applyFilters();
                }
        );


        // =====================================
        // 내 물품
        // =====================================

        btnMyItems.setOnClickListener(
                view -> {

                    /*
                     * 현재 로그인한 사람이 등록한
                     * LOST + FOUND를 모두 표시
                     */
                    showMyItemsOnly =
                            true;


                    currentType =
                            "ALL";


                    applyFilters();
                }
        );


        // =====================================
        // 카테고리 변경
        // =====================================

        spinnerCategoryFilter
                .setOnItemSelectedListener(

                        new AdapterView.OnItemSelectedListener() {


                            @Override
                            public void onItemSelected(
                                    AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id
                            ) {

                                /*
                                 * 카테고리를 바꾸면
                                 * 즉시 다시 조회
                                 */
                                applyFilters();
                            }


                            @Override
                            public void onNothingSelected(
                                    AdapterView<?> parent
                            ) {

                            }
                        }
                );


        // =====================================
        // 정렬 변경
        // =====================================

        spinnerSort
                .setOnItemSelectedListener(

                        new AdapterView.OnItemSelectedListener() {


                            @Override
                            public void onItemSelected(
                                    AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id
                            ) {

                                /*
                                 * 정렬 방식을 바꾸면
                                 * 즉시 다시 조회
                                 */
                                applyFilters();
                            }


                            @Override
                            public void onNothingSelected(
                                    AdapterView<?> parent
                            ) {

                            }
                        }
                );


        /*
         * 처음 화면 표시
         */
        applyFilters();
    }


    // =========================================
    // 필터 적용
    // =========================================

    private void applyFilters() {

        /*
         * 아직 Adapter나 Spinner 초기화가
         * 끝나지 않았다면 실행하지 않음
         */
        if (adapter == null) {

            return;
        }


        if (spinnerCategoryFilter
                .getSelectedItem() == null) {

            return;
        }


        if (spinnerSort
                .getSelectedItem() == null) {

            return;
        }


        // =====================================
        // 검색어
        // =====================================

        String keyword =
                editSearch
                        .getText()
                        .toString()
                        .trim();


        // =====================================
        // 카테고리
        // =====================================

        String category =
                spinnerCategoryFilter
                        .getSelectedItem()
                        .toString();


        /*
         * 화면의 "전체"를
         * DB에서는 "ALL"로 사용
         */
        if ("전체".equals(category)) {

            category =
                    "ALL";
        }


        // =====================================
        // 정렬 방식
        // =====================================

        String selectedSort =
                spinnerSort
                        .getSelectedItem()
                        .toString();


        String sortOrder;


        if ("오래된순".equals(
                selectedSort
        )) {

            sortOrder =
                    "OLDEST";


        } else if ("이름순".equals(
                selectedSort
        )) {

            sortOrder =
                    "NAME";


        } else {

            sortOrder =
                    "LATEST";
        }


        // =====================================
        // 결과 목록
        // =====================================

        ArrayList<Item> items;


        // =====================================
        // 내 물품
        // =====================================

        if (showMyItemsOnly) {

            /*
             * 현재 사용자의 물품 전체를 가져온다.
             */
            ArrayList<Item> myItems =
                    itemService
                            .getItemsByUserId(
                                    currentUserId
                            );


            /*
             * 최종 표시 목록
             */
            items =
                    new ArrayList<>();


            /*
             * 검색어 + 카테고리 검사
             */
            for (Item item : myItems) {


                // -----------------------------
                // 검색어
                // -----------------------------

                boolean keywordMatch =

                        keyword.isEmpty()

                                ||

                                containsIgnoreCase(
                                        item.getName(),
                                        keyword
                                )

                                ||

                                containsIgnoreCase(
                                        item.getCategory(),
                                        keyword
                                )

                                ||

                                containsIgnoreCase(
                                        item.getLocation(),
                                        keyword
                                )

                                ||

                                containsIgnoreCase(
                                        item.getDescription(),
                                        keyword
                                );


                // -----------------------------
                // 카테고리
                // -----------------------------

                boolean categoryMatch =

                        "ALL".equals(
                                category
                        )

                                ||

                                category.equals(
                                        item.getCategory()
                                );


                /*
                 * 두 조건 모두 만족
                 */
                if (keywordMatch &&
                        categoryMatch) {

                    items.add(
                            item
                    );
                }
            }


            /*
             * 내 물품 정렬
             */
            sortMyItems(
                    items,
                    sortOrder
            );


        } else {

            // =================================
            // 전체 / 분실 / 습득
            // =================================

            items =
                    itemService
                            .getFilteredItems(

                                    keyword,

                                    currentType,

                                    category,

                                    sortOrder
                            );
        }


        // =====================================
        // RecyclerView 갱신
        // =====================================

        adapter.updateList(
                items
        );
    }


    // =========================================
    // 내 물품 정렬
    // =========================================

    private void sortMyItems(
            ArrayList<Item> items,
            String sortOrder
    ) {


        /*
         * 이름순
         */
        if ("NAME".equals(
                sortOrder
        )) {

            Collections.sort(
                    items,

                    new Comparator<Item>() {

                        @Override
                        public int compare(
                                Item item1,
                                Item item2
                        ) {

                            String name1 =
                                    item1.getName() == null
                                            ? ""
                                            : item1.getName();


                            String name2 =
                                    item2.getName() == null
                                            ? ""
                                            : item2.getName();


                            return name1
                                    .compareToIgnoreCase(
                                            name2
                                    );
                        }
                    }
            );


            return;
        }


        /*
         * 오래된순
         *
         * 날짜 형식이
         * yyyy-MM-dd 이므로
         * 문자열 비교로 정렬 가능
         */
        if ("OLDEST".equals(
                sortOrder
        )) {

            Collections.sort(
                    items,

                    new Comparator<Item>() {

                        @Override
                        public int compare(
                                Item item1,
                                Item item2
                        ) {

                            String date1 =
                                    item1.getDate() == null
                                            ? ""
                                            : item1.getDate();


                            String date2 =
                                    item2.getDate() == null
                                            ? ""
                                            : item2.getDate();


                            /*
                             * 날짜가 같다면 ID 오름차순
                             */
                            int result =
                                    date1.compareTo(
                                            date2
                                    );


                            if (result == 0) {

                                return Integer.compare(
                                        item1.getId(),
                                        item2.getId()
                                );
                            }


                            return result;
                        }
                    }
            );


            return;
        }


        /*
         * 최신순
         */
        Collections.sort(
                items,

                new Comparator<Item>() {

                    @Override
                    public int compare(
                            Item item1,
                            Item item2
                    ) {

                        String date1 =
                                item1.getDate() == null
                                        ? ""
                                        : item1.getDate();


                        String date2 =
                                item2.getDate() == null
                                        ? ""
                                        : item2.getDate();


                        /*
                         * 날짜 내림차순
                         */
                        int result =
                                date2.compareTo(
                                        date1
                                );


                        /*
                         * 같은 날짜라면
                         * 나중에 등록된 ID를 먼저 표시
                         */
                        if (result == 0) {

                            return Integer.compare(
                                    item2.getId(),
                                    item1.getId()
                            );
                        }


                        return result;
                    }
                }
        );
    }


    // =========================================
    // 대소문자 무시 검색
    // =========================================

    private boolean containsIgnoreCase(
            String text,
            String keyword
    ) {

        /*
         * DB 값이 NULL인 경우 방지
         */
        if (text == null) {

            return false;
        }


        if (keyword == null) {

            return false;
        }


        return text
                .toLowerCase()
                .contains(
                        keyword.toLowerCase()
                );
    }


    // =========================================
    // 상세화면 이동
    // =========================================

    private void openDetail(
            int itemId
    ) {

        Intent intent =
                new Intent(
                        this,
                        DetailActivity.class
                );


        intent.putExtra(
                DetailActivity.EXTRA_ITEM_ID,
                itemId
        );


        startActivity(
                intent
        );
    }


    // =========================================
    // 화면으로 다시 돌아왔을 때
    // =========================================

    @Override
    protected void onResume() {

        super.onResume();


        /*
         * 상세화면에서
         *
         * 수정
         * 삭제
         * 물품 연결
         * 수령 완료
         *
         * 등이 발생했을 수 있으므로
         * 목록을 다시 읽어온다.
         */
        if (adapter != null) {

            applyFilters();
        }
    }
}