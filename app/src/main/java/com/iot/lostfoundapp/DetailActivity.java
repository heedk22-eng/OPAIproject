package com.iot.lostfoundapp;

// 삭제 확인창을 위한 Dialog
import android.app.AlertDialog;

import android.os.Bundle;

import android.widget.Button;

import android.widget.EditText;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


/*
 * DetailActivity
 *
 * 선택한 물품의 상세정보를 조회하고
 * 수정 또는 삭제하는 화면이다.
 *
 * REQ-006 상세정보 조회
 * REQ-007 물품정보 수정
 * REQ-008 물품정보 삭제
 * REQ-010 입력값 확인
 */
public class DetailActivity extends AppCompatActivity {

    /*
     * ItemListActivity에서 전달하는
     * 물품 ID의 Key
     */
    public static final String EXTRA_ITEM_ID = "ITEM_ID";


    // 입력 및 표시 영역
    private EditText editName;
    private EditText editCategory;
    private EditText editColor;
    private EditText editLocation;
    private EditText editDate;
    private EditText editDescription;


    // 수정 버튼
    private Button btnUpdate;

    // 삭제 버튼
    private Button btnDelete;


    /*
     * 현재 상세화면에서 보고 있는 물품의 ID
     */
    private int itemId;


    /*
     * 현재 조회한 Item 객체
     */
    private Item currentItem;


    /*
     * 기능 처리를 담당할 Service
     */
    private ItemService itemService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // 상세화면 XML 연결
        setContentView(R.layout.activity_detail);


        /*
         * XML View 연결
         */

        editName =
                findViewById(R.id.editName);

        editCategory =
                findViewById(R.id.editCategory);

        editColor =
                findViewById(R.id.editColor);

        editLocation =
                findViewById(R.id.editLocation);

        editDate =
                findViewById(R.id.editDate);

        editDescription =
                findViewById(R.id.editDescription);

        btnUpdate =
                findViewById(R.id.btnUpdate);

        btnDelete =
                findViewById(R.id.btnDelete);


        // ItemService 생성
        itemService = new ItemService(this);


        /*
         * ItemListActivity에서 전달한
         * ITEM_ID를 가져온다.
         *
         * 없으면 기본값 -1
         */
        itemId =
                getIntent().getIntExtra(
                        EXTRA_ITEM_ID,
                        -1
                );


        /*
         * 정상적인 ID가 전달되지 않은 경우
         */
        if (itemId == -1) {

            Toast.makeText(
                    this,
                    "잘못된 물품 정보입니다.",
                    Toast.LENGTH_SHORT
            ).show();

            // 현재 화면 종료
            finish();

            return;
        }


        /*
         * 해당 ID의 상세정보 불러오기
         */
        loadItemDetail();


        /*
         * 수정 버튼 클릭
         */
        btnUpdate.setOnClickListener(view -> {

            updateItem();
        });


        /*
         * 삭제 버튼 클릭
         */
        btnDelete.setOnClickListener(view -> {

            showDeleteDialog();
        });
    }


    /*
     * itemId에 해당하는 물품정보를 조회해서
     * 화면에 표시한다.
     */
    private void loadItemDetail() {

        /*
         * Service에게 ID로 물품 조회 요청
         */
        currentItem =
                itemService.getItem(itemId);


        /*
         * 해당 ID의 데이터가 존재하지 않는 경우
         */
        if (currentItem == null) {

            Toast.makeText(
                    this,
                    "물품 정보를 찾을 수 없습니다.",
                    Toast.LENGTH_SHORT
            ).show();


            finish();

            return;
        }


        /*
         * DB에서 가져온 Item 정보를
         * EditText에 표시한다.
         */

        editName.setText(
                currentItem.getName()
        );

        editCategory.setText(
                currentItem.getCategory()
        );

        editColor.setText(
                currentItem.getColor()
        );

        editLocation.setText(
                currentItem.getLocation()
        );

        editDate.setText(
                currentItem.getDate()
        );

        editDescription.setText(
                currentItem.getDescription()
        );
    }


    /*
     * 수정 전에 필수 입력값을 검사한다.
     */
    private boolean validateInput() {

        String name =
                editName.getText().toString().trim();

        String category =
                editCategory.getText().toString().trim();

        String location =
                editLocation.getText().toString().trim();

        String date =
                editDate.getText().toString().trim();


        // 물품명 검사
        if (name.isEmpty()) {

            editName.setError(
                    "물품명을 입력해주세요."
            );

            editName.requestFocus();

            return false;
        }


        // 카테고리 검사
        if (category.isEmpty()) {

            editCategory.setError(
                    "카테고리를 입력해주세요."
            );

            editCategory.requestFocus();

            return false;
        }


        // 장소 검사
        if (location.isEmpty()) {

            editLocation.setError(
                    "장소를 입력해주세요."
            );

            editLocation.requestFocus();

            return false;
        }


        // 날짜 검사
        if (date.isEmpty()) {

            editDate.setError(
                    "날짜를 입력해주세요."
            );

            editDate.requestFocus();

            return false;
        }


        // 모든 필수값 정상
        return true;
    }


    /*
     * 물품 수정
     */
    private void updateItem() {

        /*
         * 필수 입력값 확인
         */
        if (!validateInput()) {

            return;
        }


        /*
         * 기존 Item 객체의 내용을
         * 사용자가 수정한 값으로 변경한다.
         *
         * id와 type은 그대로 유지한다.
         */

        currentItem.setName(
                editName.getText().toString().trim()
        );

        currentItem.setCategory(
                editCategory.getText().toString().trim()
        );

        currentItem.setColor(
                editColor.getText().toString().trim()
        );

        currentItem.setLocation(
                editLocation.getText().toString().trim()
        );

        currentItem.setDate(
                editDate.getText().toString().trim()
        );

        currentItem.setDescription(
                editDescription.getText().toString().trim()
        );


        /*
         * Service에게 DB UPDATE 요청
         */
        boolean success =
                itemService.updateItem(currentItem);


        if (success) {

            Toast.makeText(
                    this,
                    "물품 정보가 수정되었습니다.",
                    Toast.LENGTH_SHORT
            ).show();


            /*
             * DB의 최신 데이터를 다시 불러온다.
             */
            loadItemDetail();

        } else {

            Toast.makeText(
                    this,
                    "수정에 실패했습니다.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    /*
     * 삭제 확인 Dialog를 표시한다.
     */
    private void showDeleteDialog() {

        /*
         * 사용자가 실수로 삭제하는 것을 막기 위해
         * 삭제 전에 확인창을 보여준다.
         */
        new AlertDialog.Builder(this)

                // Dialog 제목
                .setTitle("물품 삭제")

                // 안내문
                .setMessage(
                        "이 물품을 삭제하시겠습니까?"
                )

                /*
                 * 확인 버튼
                 */
                .setPositiveButton(
                        "삭제",
                        (dialog, which) -> {

                            deleteItem();
                        }
                )

                /*
                 * 취소 버튼
                 *
                 * null을 사용하면
                 * 아무 작업 없이 Dialog만 닫힌다.
                 */
                .setNegativeButton(
                        "취소",
                        null
                )

                // Dialog 표시
                .show();
    }


    /*
     * 물품 삭제
     */
    private void deleteItem() {

        /*
         * Service에게 DELETE 요청
         */
        boolean success =
                itemService.deleteItem(itemId);


        if (success) {

            Toast.makeText(
                    this,
                    "물품이 삭제되었습니다.",
                    Toast.LENGTH_SHORT
            ).show();


            /*
             * DetailActivity 종료
             *
             * 이전 ItemListActivity로 돌아간다.
             */
            finish();

        } else {

            Toast.makeText(
                    this,
                    "삭제에 실패했습니다.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}