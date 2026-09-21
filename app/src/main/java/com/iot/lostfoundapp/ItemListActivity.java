package com.iot.lostfoundapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;


/*
 * 물품 목록 화면
 *
 * 기능
 *
 * 1. 전체 / 분실 / 습득 조회
 * 2. 내 등록내역 조회
 * 3. 검색
 * 4. 검색어 X 버튼
 * 5. 카테고리 필터
 * 6. 정렬
 * 7. 필터 초기화
 * 8. 선택된 필터 표시
 * 9. 등록 건수 표시
 * 10. 검색 결과 0건 화면
 */
public class ItemListActivity extends AppCompatActivity {


    // =================================================
    // View
    // =================================================

    private EditText editSearch;

    private MaterialButton btnSearch;
    private MaterialButton btnClearSearch;

    private MaterialButton btnAll;
    private MaterialButton btnLost;
    private MaterialButton btnFound;
    private MaterialButton btnMyItems;

    private MaterialButton btnResetFilters;

    private Spinner spinnerCategoryFilter;
    private Spinner spinnerSort;

    private RecyclerView recyclerView;

    private TextView textTotalCount;
    private TextView textResultCount;

    private LinearLayout emptyView;

    private TextView textEmptyTitle;
    private TextView textEmptyMessage;


    // =================================================
    // Adapter / Service
    // =================================================

    private ItemAdapter adapter;

    private ItemService itemService;


    // =================================================
    // 현재 사용자
    // =================================================

    private int currentUserId;


    // =================================================
    // 현재 필터 상태
    // =================================================

    /*
     * ALL
     * LOST
     * FOUND
     */
    private String currentType = "ALL";


    /*
     * true
     * → 현재 로그인한 사용자가 등록한 글만 표시
     */
    private boolean showMyItemsOnly = false;


    // =================================================
    // Spinner 데이터
    // =================================================

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


    private static final String[] SORT_OPTIONS = {

            "최신순",
            "오래된순",
            "이름순"
    };


    // =================================================
    // onCreate
    // =================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_item_list
        );


        // =================================================
        // View 연결
        // =================================================

        editSearch =
                findViewById(
                        R.id.editSearch
                );


        btnSearch =
                findViewById(
                        R.id.btnSearch
                );


        btnClearSearch =
                findViewById(
                        R.id.btnClearSearch
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


        btnResetFilters =
                findViewById(
                        R.id.btnResetFilters
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


        textTotalCount =
                findViewById(
                        R.id.textTotalCount
                );


        textResultCount =
                findViewById(
                        R.id.textResultCount
                );


        emptyView =
                findViewById(
                        R.id.emptyView
                );


        textEmptyTitle =
                findViewById(
                        R.id.textEmptyTitle
                );


        textEmptyMessage =
                findViewById(
                        R.id.textEmptyMessage
                );


        // =================================================
        // Service
        // =================================================

        itemService =
                new ItemService(this);


        // =================================================
        // 현재 사용자
        // =================================================

        SessionManager sessionManager =
                new SessionManager(this);


        currentUserId =
                sessionManager.getUserId();


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


        // =================================================
        // 카테고리 Spinner
        // =================================================

        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        FILTER_CATEGORIES
                );


        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );


        spinnerCategoryFilter.setAdapter(
                categoryAdapter
        );


        // =================================================
        // 정렬 Spinner
        // =================================================

        ArrayAdapter<String> sortAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        SORT_OPTIONS
                );


        sortAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );


        spinnerSort.setAdapter(
                sortAdapter
        );


        // =================================================
        // RecyclerView
        // =================================================

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );


        adapter =
                new ItemAdapter(

                        this,

                        new ArrayList<>(),

                        item ->
                                openDetail(
                                        item.getId()
                                )
                );


        recyclerView.setAdapter(
                adapter
        );


        // =================================================
        // 검색
        // =================================================

        btnSearch.setOnClickListener(
                view ->
                        applyFilters()
        );


        // =================================================
        // 검색어 X 버튼
        // =================================================

        btnClearSearch.setOnClickListener(
                view -> {

                    editSearch.setText("");

                    applyFilters();
                }
        );


        /*
         * 검색어가 있을 때만 X 버튼 표시
         */
        editSearch.addTextChangedListener(

                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {

                    }


                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        if (s.length() > 0) {

                            btnClearSearch.setVisibility(
                                    View.VISIBLE
                            );

                        } else {

                            btnClearSearch.setVisibility(
                                    View.GONE
                            );
                        }
                    }


                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {

                    }
                }
        );


        // =================================================
        // 전체
        // =================================================

        btnAll.setOnClickListener(
                view -> {

                    showMyItemsOnly =
                            false;

                    currentType =
                            "ALL";

                    applyFilters();
                }
        );


        // =================================================
        // 분실
        // =================================================

        btnLost.setOnClickListener(
                view -> {

                    showMyItemsOnly =
                            false;

                    currentType =
                            "LOST";

                    applyFilters();
                }
        );


        // =================================================
        // 습득
        // =================================================

        btnFound.setOnClickListener(
                view -> {

                    showMyItemsOnly =
                            false;

                    currentType =
                            "FOUND";

                    applyFilters();
                }
        );


        // =================================================
        // 내 등록내역
        // =================================================

        btnMyItems.setOnClickListener(
                view -> {

                    showMyItemsOnly =
                            true;

                    currentType =
                            "ALL";

                    applyFilters();
                }
        );


        // =================================================
        // 필터 초기화
        // =================================================

        btnResetFilters.setOnClickListener(
                view ->
                        resetFilters()
        );


        // =================================================
        // 카테고리 변경
        // =================================================

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

                                applyFilters();
                            }


                            @Override
                            public void onNothingSelected(
                                    AdapterView<?> parent
                            ) {

                            }
                        }
                );


        // =================================================
        // 정렬 변경
        // =================================================

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

                                applyFilters();
                            }


                            @Override
                            public void onNothingSelected(
                                    AdapterView<?> parent
                            ) {

                            }
                        }
                );


        // =================================================
        // 처음 화면
        // =================================================

        applyFilters();
    }


    // =================================================
    // 필터 초기화
    // =================================================

    private void resetFilters() {

        /*
         * 검색어 제거
         */
        editSearch.setText("");


        /*
         * 전체 보기
         */
        currentType =
                "ALL";


        /*
         * 내 등록내역 해제
         */
        showMyItemsOnly =
                false;


        /*
         * 카테고리 → 전체
         */
        spinnerCategoryFilter.setSelection(
                0,
                false
        );


        /*
         * 정렬 → 최신순
         */
        spinnerSort.setSelection(
                0,
                false
        );


        applyFilters();
    }


    // =================================================
    // 필터 적용
    // =================================================

    private void applyFilters() {


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


        // =================================================
        // 검색어
        // =================================================

        String keyword =
                editSearch
                        .getText()
                        .toString()
                        .trim();


        // =================================================
        // 카테고리
        // =================================================

        String category =
                spinnerCategoryFilter
                        .getSelectedItem()
                        .toString();


        if ("전체".equals(category)) {

            category =
                    "ALL";
        }


        // =================================================
        // 정렬
        // =================================================

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


        // =================================================
        // 결과 목록
        // =================================================

        ArrayList<Item> items;


        // =================================================
        // 내 등록내역
        // =================================================

        if (showMyItemsOnly) {


            ArrayList<Item> myItems =
                    itemService
                            .getItemsByUserId(
                                    currentUserId
                            );


            items =
                    new ArrayList<>();


            for (Item item : myItems) {


                // -----------------------------------------
                // 검색어 검사
                // -----------------------------------------

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


                // -----------------------------------------
                // 카테고리 검사
                // -----------------------------------------

                boolean categoryMatch =

                        "ALL".equals(
                                category
                        )

                                ||

                                category.equals(
                                        item.getCategory()
                                );


                if (keywordMatch &&
                        categoryMatch) {

                    items.add(
                            item
                    );
                }
            }


            sortMyItems(
                    items,
                    sortOrder
            );


        } else {


            items =
                    itemService
                            .getFilteredItems(

                                    keyword,

                                    currentType,

                                    category,

                                    sortOrder
                            );
        }


        // =================================================
        // RecyclerView 갱신
        // =================================================

        adapter.updateList(
                items
        );


        // =================================================
        // 화면 추가 정보 갱신
        // =================================================

        updateFilterButtonStyles();

        updateRegistrationCounts();

        updateResultView(
                items,
                keyword,
                category
        );
    }


    // =================================================
    // 선택된 필터 버튼 색상
    // =================================================

    private void updateFilterButtonStyles() {


        setFilterButtonStyle(

                btnAll,

                !showMyItemsOnly &&
                        "ALL".equals(
                                currentType
                        )
        );


        setFilterButtonStyle(

                btnLost,

                !showMyItemsOnly &&
                        "LOST".equals(
                                currentType
                        )
        );


        setFilterButtonStyle(

                btnFound,

                !showMyItemsOnly &&
                        "FOUND".equals(
                                currentType
                        )
        );


        setFilterButtonStyle(

                btnMyItems,

                showMyItemsOnly
        );
    }


    /*
     * selected == true
     * → 진한 파란색
     *
     * false
     * → 연한 파란색
     */
    private void setFilterButtonStyle(
            MaterialButton button,
            boolean selected
    ) {


        if (selected) {

            button.setBackgroundTintList(

                    ContextCompat.getColorStateList(
                            this,
                            R.color.button_primary_background
                    )
            );


            button.setTextColor(
                    Color.WHITE
            );


        } else {

            button.setBackgroundTintList(

                    ContextCompat.getColorStateList(
                            this,
                            R.color.button_secondary_background
                    )
            );


            button.setTextColor(
                    Color.parseColor(
                            "#25345F"
                    )
            );
        }
    }


    // =================================================
    // 전체 / 분실 / 습득 등록 건수
    // =================================================

    private void updateRegistrationCounts() {


        ArrayList<Item> allItems =
                itemService.getAllItems();


        int totalCount = 0;

        int lostCount = 0;

        int foundCount = 0;


        if (allItems != null) {


            totalCount =
                    allItems.size();


            for (Item item : allItems) {


                if ("LOST".equals(
                        item.getType()
                )) {

                    lostCount++;


                } else if ("FOUND".equals(
                        item.getType()
                )) {

                    foundCount++;
                }
            }
        }


        String countText =

                "전체 " + totalCount + "건" +

                        " · 분실 " + lostCount + "건" +

                        " · 습득 " + foundCount + "건";


        textTotalCount.setText(
                countText
        );
    }


    // =================================================
    // 검색 결과 / 빈 화면
    // =================================================

    private void updateResultView(
            ArrayList<Item> items,
            String keyword,
            String category
    ) {


        int count =
                items == null
                        ? 0
                        : items.size();


        textResultCount.setText(
                "현재 결과 " + count + "건"
        );


        if (count > 0) {


            recyclerView.setVisibility(
                    View.VISIBLE
            );


            emptyView.setVisibility(
                    View.GONE
            );


        } else {


            recyclerView.setVisibility(
                    View.GONE
            );


            emptyView.setVisibility(
                    View.VISIBLE
            );


            /*
             * 아무것도 등록되지 않은 경우
             */
            if (keyword.isEmpty()
                    &&
                    "ALL".equals(category)
                    &&
                    "ALL".equals(currentType)
                    &&
                    !showMyItemsOnly) {


                textEmptyTitle.setText(
                        "아직 등록된 물품이 없습니다"
                );


                textEmptyMessage.setText(
                        "분실물 또는 습득물을 등록해보세요."
                );


            } else {


                textEmptyTitle.setText(
                        "검색 결과가 없습니다"
                );


                textEmptyMessage.setText(
                        "검색어나 필터 조건을 변경해보세요."
                );
            }
        }
    }


    // =================================================
    // 내 등록내역 정렬
    // =================================================

    private void sortMyItems(
            ArrayList<Item> items,
            String sortOrder
    ) {


        // =================================================
        // 이름순
        // =================================================

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


                            return name1.compareToIgnoreCase(
                                    name2
                            );
                        }
                    }
            );


            return;
        }


        // =================================================
        // 오래된순
        // =================================================

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


        // =================================================
        // 최신순
        // =================================================

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


                        int result =
                                date2.compareTo(
                                        date1
                                );


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


    // =================================================
    // 대소문자 무시 검색
    // =================================================

    private boolean containsIgnoreCase(
            String text,
            String keyword
    ) {


        if (text == null ||
                keyword == null) {

            return false;
        }


        return text
                .toLowerCase(
                        Locale.ROOT
                )
                .contains(

                        keyword.toLowerCase(
                                Locale.ROOT
                        )
                );
    }


    // =================================================
    // 상세화면
    // =================================================

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


    // =================================================
    // 상세화면에서 다시 돌아왔을 때
    // =================================================

    @Override
    protected void onResume() {

        super.onResume();


        if (adapter != null) {

            applyFilters();
        }
    }
}