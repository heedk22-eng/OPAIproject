package com.iot.lostfoundapp;

// Activity 생성 시 사용하는 클래스
import android.os.Bundle;

// 입력창
import android.widget.EditText;

// 버튼
import android.widget.Button;

// 제목을 표시할 TextView
import android.widget.TextView;

// 간단한 안내 메시지를 화면에 표시하는 Toast
import android.widget.Toast;

// Android 기본 Activity
import androidx.appcompat.app.AppCompatActivity;


/*
 * RegisterActivity
 *
 * 분실물 또는 습득물을 등록하는 화면이다.
 *
 * MainActivity에서 전달받은
 * ITEM_TYPE 값에 따라
 *
 * LOST  = 분실물
 * FOUND = 습득물
 *
 * 로 구분한다.
 */
public class RegisterActivity extends AppCompatActivity {

    /*
     * MainActivity에서 전달할 Intent Key
     *
     * 문자열을 직접 여러 번 사용하는 것을 방지하기 위해
     * 상수로 만들어 둔다.
     */
    public static final String EXTRA_ITEM_TYPE = "ITEM_TYPE";


    // 화면 제목
    private TextView textTitle;


    // 물품명 입력창
    private EditText editName;

    // 카테고리 입력창
    private EditText editCategory;

    // 색상 입력창
    private EditText editColor;

    // 장소 입력창
    private EditText editLocation;

    // 날짜 입력창
    private EditText editDate;

    // 특징 입력창
    private EditText editDescription;


    // 등록 버튼
    private Button btnSave;


    /*
     * 현재 등록하려는 물품의 종류
     *
     * LOST 또는 FOUND가 저장된다.
     */
    private String itemType;


    /*
     * 물품 등록 기능을 처리할 Service
     */
    private ItemService itemService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // 등록 화면 XML 연결
        setContentView(R.layout.activity_register);


        /*
         * XML의 View들을 Java 변수와 연결
         */

        textTitle = findViewById(R.id.textTitle);

        editName = findViewById(R.id.editName);

        editCategory = findViewById(R.id.editCategory);

        editColor = findViewById(R.id.editColor);

        editLocation = findViewById(R.id.editLocation);

        editDate = findViewById(R.id.editDate);

        editDescription = findViewById(R.id.editDescription);

        btnSave = findViewById(R.id.btnSave);


        /*
         * ItemService 객체 생성
         *
         * ItemService는 이후
         * DatabaseHelper와 연결되어
         * 물품 저장 기능을 처리한다.
         */
        itemService = new ItemService(this);


        /*
         * MainActivity에서 전달한 ITEM_TYPE을 가져온다.
         *
         * LOST 또는 FOUND
         */
        itemType = getIntent().getStringExtra(EXTRA_ITEM_TYPE);


        /*
         * ITEM_TYPE 값이 전달되지 않은 경우
         * 기본값으로 LOST를 사용한다.
         */
        if (itemType == null) {

            itemType = "LOST";
        }


        /*
         * 분실물 / 습득물에 따라 화면 제목 변경
         */
        if (itemType.equals("LOST")) {

            textTitle.setText("분실물 등록");

        } else {

            textTitle.setText("습득물 등록");
        }


        /*
         * 등록 버튼 클릭 이벤트
         */
        btnSave.setOnClickListener(view -> {

            // 물품 저장 처리
            saveItem();
        });
    }


    /*
     * 필수 입력값이 제대로 입력되었는지 검사한다.
     *
     * 필수항목
     * - 물품명
     * - 카테고리
     * - 장소
     * - 날짜
     *
     * 모두 정상 → true
     * 하나라도 누락 → false
     */
    private boolean validateInput() {

        /*
         * getText()
         * → EditText에 입력된 내용을 가져온다.
         *
         * toString()
         * → 문자열로 변환한다.
         *
         * trim()
         * → 문자열 앞뒤의 공백을 제거한다.
         */

        String name =
                editName.getText().toString().trim();

        String category =
                editCategory.getText().toString().trim();

        String location =
                editLocation.getText().toString().trim();

        String date =
                editDate.getText().toString().trim();


        /*
         * 물품명이 입력되지 않은 경우
         */
        if (name.isEmpty()) {

            editName.setError("물품명을 입력해주세요.");

            // 해당 입력창으로 커서 이동
            editName.requestFocus();

            return false;
        }


        /*
         * 카테고리가 입력되지 않은 경우
         */
        if (category.isEmpty()) {

            editCategory.setError("카테고리를 입력해주세요.");

            editCategory.requestFocus();

            return false;
        }


        /*
         * 장소가 입력되지 않은 경우
         */
        if (location.isEmpty()) {

            editLocation.setError("장소를 입력해주세요.");

            editLocation.requestFocus();

            return false;
        }


        /*
         * 날짜가 입력되지 않은 경우
         */
        if (date.isEmpty()) {

            editDate.setError("날짜를 입력해주세요.");

            editDate.requestFocus();

            return false;
        }


        /*
         * 모든 필수 입력값이 정상
         */
        return true;
    }


    /*
     * 사용자가 입력한 정보를 이용해
     * Item 객체를 생성한다.
     */
    private Item createItem() {

        // 새로운 Item 객체 생성
        Item item = new Item();


        /*
         * LOST 또는 FOUND 저장
         */
        item.setType(itemType);


        /*
         * 각 EditText의 입력값을 Item 객체에 저장
         */

        item.setName(
                editName.getText().toString().trim()
        );

        item.setCategory(
                editCategory.getText().toString().trim()
        );

        item.setColor(
                editColor.getText().toString().trim()
        );

        item.setLocation(
                editLocation.getText().toString().trim()
        );

        item.setDate(
                editDate.getText().toString().trim()
        );

        item.setDescription(
                editDescription.getText().toString().trim()
        );


        // 완성된 Item 객체 반환
        return item;
    }


    /*
     * 물품 저장 메소드
     */
    private void saveItem() {

        /*
         * 먼저 필수 입력값을 검사한다.
         */
        if (!validateInput()) {

            // 입력값이 잘못되어 있으면 여기서 함수 종료
            return;
        }


        /*
         * 사용자 입력값으로 Item 객체 생성
         */
        Item item = createItem();


        /*
         * ItemService에게 저장 요청
         *
         * true  = 저장 성공
         * false = 저장 실패
         */
        boolean success =
                itemService.saveItem(item);


        /*
         * 저장 성공
         */
        if (success) {

            Toast.makeText(
                    this,
                    "물품이 등록되었습니다.",
                    Toast.LENGTH_SHORT
            ).show();


            /*
             * 현재 RegisterActivity 종료
             *
             * 이전 화면(MainActivity)으로 돌아간다.
             */
            finish();

        } else {

            /*
             * 저장 실패
             */
            Toast.makeText(
                    this,
                    "물품 등록에 실패했습니다.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}