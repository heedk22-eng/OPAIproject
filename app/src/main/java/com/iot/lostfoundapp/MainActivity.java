package com.iot.lostfoundapp;

// 다른 Activity로 화면을 이동하기 위해 사용하는 클래스
import android.content.Intent;

// Activity가 생성될 때 전달되는 데이터를 처리하기 위한 클래스
import android.os.Bundle;

// 버튼 클래스
import android.widget.Button;

// AndroidX의 기본 Activity 클래스
import androidx.appcompat.app.AppCompatActivity;


/*
 * MainActivity
 *
 * 앱을 실행했을 때 처음 나타나는 메인 화면이다.
 *
 * 주요 기능
 * 1. 분실물 등록 화면 이동
 * 2. 습득물 등록 화면 이동
 * 3. 물품 목록 화면 이동
 */
public class MainActivity extends AppCompatActivity {

    // 분실물 등록 버튼
    private Button btnLostRegister;

    // 습득물 등록 버튼
    private Button btnFoundRegister;

    // 전체 물품 목록 버튼
    private Button btnItemList;


    /*
     * Activity가 처음 생성될 때 실행되는 메소드
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 부모 Activity의 onCreate() 실행
        super.onCreate(savedInstanceState);

        // MainActivity에서 사용할 XML 화면 연결
        setContentView(R.layout.activity_main);


        /*
         * activity_main.xml에 있는 버튼들을
         * Java 코드의 변수와 연결한다.
         */

        // 분실물 등록 버튼 연결
        btnLostRegister = findViewById(R.id.btnLostRegister);

        // 습득물 등록 버튼 연결
        btnFoundRegister = findViewById(R.id.btnFoundRegister);

        // 물품 목록 버튼 연결
        btnItemList = findViewById(R.id.btnItemList);


        /*
         * 분실물 등록 버튼 클릭 이벤트
         */
        btnLostRegister.setOnClickListener(view -> {

            // 분실물 등록 화면으로 이동
            openLostRegister();

        });


        /*
         * 습득물 등록 버튼 클릭 이벤트
         */
        btnFoundRegister.setOnClickListener(view -> {

            // 습득물 등록 화면으로 이동
            openFoundRegister();

        });


        /*
         * 물품 목록 버튼 클릭 이벤트
         */
        btnItemList.setOnClickListener(view -> {

            // 전체 물품 목록 화면으로 이동
            openItemList();

        });
    }


    /*
     * 분실물 등록 화면을 실행하는 메소드
     */
    private void openLostRegister() {

        // 현재 MainActivity에서 RegisterActivity로 이동할 Intent 생성
        Intent intent =
                new Intent(MainActivity.this, RegisterActivity.class);

        /*
         * RegisterActivity에게
         * "지금 등록하려는 물품은 분실물이다."
         * 라는 정보를 전달한다.
         *
         * LOST = 분실물
         */
        intent.putExtra("ITEM_TYPE", "LOST");

        // RegisterActivity 실행
        startActivity(intent);
    }


    /*
     * 습득물 등록 화면을 실행하는 메소드
     */
    private void openFoundRegister() {

        // RegisterActivity로 이동할 Intent 생성
        Intent intent =
                new Intent(MainActivity.this, RegisterActivity.class);

        /*
         * FOUND = 습득물
         */
        intent.putExtra("ITEM_TYPE", "FOUND");

        // RegisterActivity 실행
        startActivity(intent);
    }


    /*
     * 등록된 물품 목록 화면으로 이동하는 메소드
     */
    private void openItemList() {

        // ItemListActivity로 이동할 Intent 생성
        Intent intent =
                new Intent(MainActivity.this, ItemListActivity.class);

        // ItemListActivity 실행
        startActivity(intent);
    }
}