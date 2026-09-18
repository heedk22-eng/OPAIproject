package com.iot.lostfoundapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;


public class RegisterActivity
        extends AppCompatActivity {

    public static final String EXTRA_ITEM_TYPE =
            "ITEM_TYPE";


    private static final String[] CATEGORIES = {
            "선택하세요",
            "전자기기",
            "지갑/카드",
            "의류",
            "가방",
            "문구류",
            "열쇠",
            "기타"
    };


    private TextView textTitle;

    private ImageView imagePreview;

    private Button btnSelectImage;

    private EditText editName;

    private Spinner spinnerCategory;

    private EditText editColor;

    private EditText editLocation;

    private EditText editDate;

    private EditText editDescription;

    private Button btnSave;


    private String itemType;

    private String selectedImageUri;

    private int currentUserId;

    private ItemService itemService;


    /*
     * 사진 선택
     */
    private final ActivityResultLauncher<String[]>
            imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.OpenDocument(),

                    uri -> {

                        if (uri != null) {

                            selectedImageUri =
                                    uri.toString();


                            try {

                                getContentResolver()
                                        .takePersistableUriPermission(
                                                uri,
                                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        );

                            } catch (Exception ignored) {
                            }


                            imagePreview.setImageURI(
                                    uri
                            );
                        }
                    }
            );


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );


        setContentView(
                R.layout.activity_register
        );


        textTitle =
                findViewById(
                        R.id.textTitle
                );

        imagePreview =
                findViewById(
                        R.id.imagePreview
                );

        btnSelectImage =
                findViewById(
                        R.id.btnSelectImage
                );

        editName =
                findViewById(
                        R.id.editName
                );

        spinnerCategory =
                findViewById(
                        R.id.spinnerCategory
                );

        editColor =
                findViewById(
                        R.id.editColor
                );

        editLocation =
                findViewById(
                        R.id.editLocation
                );

        editDate =
                findViewById(
                        R.id.editDate
                );

        editDescription =
                findViewById(
                        R.id.editDescription
                );

        btnSave =
                findViewById(
                        R.id.btnSave
                );


        itemService =
                new ItemService(this);


        /*
         * 현재 사용자
         */
        SessionManager sessionManager =
                new SessionManager(this);


        currentUserId =
                sessionManager.getUserId();


        if (currentUserId == -1) {

            Toast.makeText(
                    this,
                    "사용자를 먼저 선택해주세요.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        /*
         * Spinner
         */
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        CATEGORIES
                );


        adapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item
        );


        spinnerCategory.setAdapter(
                adapter
        );


        /*
         * LOST / FOUND
         */
        itemType =
                getIntent()
                        .getStringExtra(
                                EXTRA_ITEM_TYPE
                        );


        if (itemType == null) {

            itemType =
                    "LOST";
        }


        if ("LOST".equals(itemType)) {

            textTitle.setText(
                    "분실물 등록"
            );

        } else {

            textTitle.setText(
                    "습득물 등록"
            );
        }


        btnSelectImage.setOnClickListener(
                view ->
                        imagePicker.launch(
                                new String[]{
                                        "image/*"
                                }
                        )
        );


        editDate.setOnClickListener(
                view ->
                        showDatePicker()
        );


        btnSave.setOnClickListener(
                view ->
                        saveItem()
        );
    }


    private void showDatePicker() {

        Calendar calendar =
                Calendar.getInstance();


        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,

                        (view,
                         year,
                         month,
                         day) -> {

                            String date =
                                    String.format(
                                            Locale.KOREA,
                                            "%04d-%02d-%02d",
                                            year,
                                            month + 1,
                                            day
                                    );


                            editDate.setText(
                                    date
                            );
                        },

                        calendar.get(
                                Calendar.YEAR
                        ),

                        calendar.get(
                                Calendar.MONTH
                        ),

                        calendar.get(
                                Calendar.DAY_OF_MONTH
                        )
                );


        dialog.show();
    }


    /*
     * 입력값 검사
     */
    private boolean validateInput() {

        if (editName
                .getText()
                .toString()
                .trim()
                .isEmpty()) {

            editName.setError(
                    "물품명을 입력해주세요."
            );

            return false;
        }


        if (spinnerCategory
                .getSelectedItemPosition()
                == 0) {

            Toast.makeText(
                    this,
                    "카테고리를 선택해주세요.",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }


        if (editLocation
                .getText()
                .toString()
                .trim()
                .isEmpty()) {

            editLocation.setError(
                    "장소를 입력해주세요."
            );

            return false;
        }


        if (editDate
                .getText()
                .toString()
                .trim()
                .isEmpty()) {

            Toast.makeText(
                    this,
                    "날짜를 선택해주세요.",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }


        return true;
    }


    /*
     * 입력값 → Item
     */
    private Item createItem() {

        Item item =
                new Item();


        item.setType(
                itemType
        );


        item.setName(
                editName
                        .getText()
                        .toString()
                        .trim()
        );


        item.setCategory(
                spinnerCategory
                        .getSelectedItem()
                        .toString()
        );


        item.setColor(
                editColor
                        .getText()
                        .toString()
                        .trim()
        );


        item.setLocation(
                editLocation
                        .getText()
                        .toString()
                        .trim()
        );


        item.setDate(
                editDate
                        .getText()
                        .toString()
                        .trim()
        );


        item.setDescription(
                editDescription
                        .getText()
                        .toString()
                        .trim()
        );


        item.setImageUri(
                selectedImageUri
        );


        /*
         * 새 물품 기본 상태
         */
        item.setStatus(
                "SEARCHING"
        );


        item.setUserId(
                currentUserId
        );


        item.setMatchedItemId(
                -1
        );


        item.setHandoffNote(
                ""
        );


        return item;
    }


    private void saveItem() {

        if (!validateInput()) {
            return;
        }


        Item item =
                createItem();


        boolean success =
                itemService
                        .saveItem(item);


        if (success) {

            Toast.makeText(
                    this,
                    "물품이 등록되었습니다.",
                    Toast.LENGTH_SHORT
            ).show();


            finish();

        } else {

            Toast.makeText(
                    this,
                    "등록에 실패했습니다.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}