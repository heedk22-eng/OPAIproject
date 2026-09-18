package com.iot.lostfoundapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class DetailActivity
        extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID =
            "ITEM_ID";


    private static final String[] CATEGORIES = {
            "전자기기",
            "지갑/카드",
            "의류",
            "가방",
            "문구류",
            "열쇠",
            "기타"
    };


    private ImageView imagePreview;

    private Button btnSelectImage;


    private TextView textOwner;

    private TextView textStatus;

    private TextView textMatchInfo;


    private Button btnMatchFound;


    private TextView textHandoffNoteTitle;

    private TextView textHandoffNote;

    private EditText editHandoffNote;

    private Button btnSaveHandoffNote;


    private Button btnCompleteReturn;


    private EditText editName;

    private Spinner spinnerCategory;

    private EditText editColor;

    private EditText editLocation;

    private EditText editDate;

    private EditText editDescription;


    private Button btnUpdate;

    private Button btnDelete;


    private int itemId;

    private int currentUserId;

    private Item currentItem;

    private ItemService itemService;

    private String selectedImageUri;


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
                R.layout.activity_detail
        );


        imagePreview =
                findViewById(
                        R.id.imagePreview
                );

        btnSelectImage =
                findViewById(
                        R.id.btnSelectImage
                );


        textOwner =
                findViewById(
                        R.id.textOwner
                );

        textStatus =
                findViewById(
                        R.id.textStatus
                );

        textMatchInfo =
                findViewById(
                        R.id.textMatchInfo
                );


        btnMatchFound =
                findViewById(
                        R.id.btnMatchFound
                );


        textHandoffNoteTitle =
                findViewById(
                        R.id.textHandoffNoteTitle
                );

        textHandoffNote =
                findViewById(
                        R.id.textHandoffNote
                );

        editHandoffNote =
                findViewById(
                        R.id.editHandoffNote
                );

        btnSaveHandoffNote =
                findViewById(
                        R.id.btnSaveHandoffNote
                );


        btnCompleteReturn =
                findViewById(
                        R.id.btnCompleteReturn
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


        btnUpdate =
                findViewById(
                        R.id.btnUpdate
                );

        btnDelete =
                findViewById(
                        R.id.btnDelete
                );


        /*
         * 카테고리
         */
        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        CATEGORIES
                );


        categoryAdapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item
        );


        spinnerCategory.setAdapter(
                categoryAdapter
        );


        itemService =
                new ItemService(this);


        SessionManager sessionManager =
                new SessionManager(this);


        currentUserId =
                sessionManager.getUserId();


        itemId =
                getIntent()
                        .getIntExtra(
                                EXTRA_ITEM_ID,
                                -1
                        );


        if (itemId == -1) {

            Toast.makeText(
                    this,
                    "잘못된 물품 정보입니다.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        loadItemDetail();


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


        btnMatchFound.setOnClickListener(
                view ->
                        showFoundItemDialog()
        );


        btnSaveHandoffNote.setOnClickListener(
                view ->
                        saveHandoffNote()
        );


        btnCompleteReturn.setOnClickListener(
                view ->
                        showCompleteDialog()
        );


        btnUpdate.setOnClickListener(
                view ->
                        updateItem()
        );


        btnDelete.setOnClickListener(
                view ->
                        showDeleteDialog()
        );
    }


    /*
     * 상세정보 조회
     */
    private void loadItemDetail() {

        currentItem =
                itemService.getItem(
                        itemId
                );


        if (currentItem == null) {

            Toast.makeText(
                    this,
                    "물품 정보를 찾을 수 없습니다.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        editName.setText(
                currentItem.getName()
        );


        for (int i = 0;
             i < CATEGORIES.length;
             i++) {

            if (CATEGORIES[i]
                    .equals(
                            currentItem.getCategory()
                    )) {

                spinnerCategory
                        .setSelection(i);

                break;
            }
        }


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


        /*
         * 사진
         */
        selectedImageUri =
                currentItem.getImageUri();


        if (selectedImageUri != null &&
                !selectedImageUri.isEmpty()) {

            try {

                imagePreview.setImageURI(
                        Uri.parse(
                                selectedImageUri
                        )
                );

            } catch (Exception e) {

                imagePreview.setImageResource(
                        android.R.drawable
                                .ic_menu_gallery
                );
            }

        } else {

            imagePreview.setImageResource(
                    android.R.drawable
                            .ic_menu_gallery
            );
        }


        updateMatchUI();
    }


    /*
     * 상태 / 연결 / 메모 UI
     */
    private void updateMatchUI() {

        String ownerNickname =
                itemService
                        .getUserNickname(
                                currentItem.getUserId()
                        );


        textOwner.setText(
                "등록자 : " +
                        ownerNickname
        );


        /*
         * 상태
         */
        if ("MATCHED".equals(
                currentItem.getStatus()
        )) {

            if ("LOST".equals(
                    currentItem.getType()
            )) {

                textStatus.setText(
                        "상태 : 습득물 확인"
                );

            } else {

                textStatus.setText(
                        "상태 : 분실자 확인"
                );
            }

        } else {

            if ("LOST".equals(
                    currentItem.getType()
            )) {

                textStatus.setText(
                        "상태 : 찾는 중"
                );

            } else {

                textStatus.setText(
                        "상태 : 보관 중"
                );
            }
        }


        /*
         * 일단 관련 UI 숨김
         */
        btnMatchFound.setVisibility(
                View.GONE
        );

        btnCompleteReturn.setVisibility(
                View.GONE
        );

        textHandoffNoteTitle.setVisibility(
                View.GONE
        );

        textHandoffNote.setVisibility(
                View.GONE
        );

        editHandoffNote.setVisibility(
                View.GONE
        );

        btnSaveHandoffNote.setVisibility(
                View.GONE
        );


        boolean isOwner =
                currentItem.getUserId()
                        == currentUserId;


        /*
         * 자기 물품만 수정/삭제
         */
        setEditPermission(
                isOwner
        );


        /*
         * 연결 상대 확인
         */
        Item matchedItem = null;


        if (currentItem.getMatchedItemId()
                != -1) {

            matchedItem =
                    itemService.getItem(
                            currentItem
                                    .getMatchedItemId()
                    );
        }


        /*
         * 연결 없음
         */
        if (matchedItem == null) {

            textMatchInfo.setText(
                    "연결된 물품 없음"
            );


            /*
             * 자기 분실물인 경우에만
             * 습득물 연결 가능
             */
            if (isOwner &&
                    "LOST".equals(
                            currentItem.getType()
                    )) {

                btnMatchFound.setVisibility(
                        View.VISIBLE
                );
            }


            return;
        }


        /*
         * 연결정보
         */
        String matchedOwner =
                itemService
                        .getUserNickname(
                                matchedItem.getUserId()
                        );


        textMatchInfo.setText(
                "연결된 물품 : " +
                        matchedItem.getName() +

                        "\n등록자 : " +
                        matchedOwner
        );


        /*
         * 현재 화면이 LOST
         */
        if ("LOST".equals(
                currentItem.getType()
        )) {

            /*
             * 연결된 FOUND에서 전달메모 읽기
             */
            String note =
                    matchedItem
                            .getHandoffNote();


            textHandoffNoteTitle
                    .setVisibility(
                            View.VISIBLE
                    );

            textHandoffNote
                    .setVisibility(
                            View.VISIBLE
                    );


            if (note == null ||
                    note.trim().isEmpty()) {

                textHandoffNote.setText(
                        "아직 습득자가 전달 메모를 남기지 않았습니다."
                );

            } else {

                textHandoffNote.setText(
                        note
                );
            }


            /*
             * 분실물 등록자만 최종 수령 가능
             */
            if (isOwner) {

                btnCompleteReturn
                        .setVisibility(
                                View.VISIBLE
                        );
            }
        }


        /*
         * 현재 화면이 FOUND
         */
        if ("FOUND".equals(
                currentItem.getType()
        )) {

            /*
             * 습득물 등록자만 메모 작성
             */
            if (isOwner) {

                textHandoffNoteTitle
                        .setVisibility(
                                View.VISIBLE
                        );

                editHandoffNote
                        .setVisibility(
                                View.VISIBLE
                        );

                btnSaveHandoffNote
                        .setVisibility(
                                View.VISIBLE
                        );


                String note =
                        currentItem
                                .getHandoffNote();


                if (note != null) {

                    editHandoffNote.setText(
                            note
                    );
                }
            }
        }
    }


    /*
     * 등록자 여부에 따른 수정 권한
     */
    private void setEditPermission(
            boolean isOwner
    ) {

        editName.setEnabled(
                isOwner
        );

        spinnerCategory.setEnabled(
                isOwner
        );

        editColor.setEnabled(
                isOwner
        );

        editLocation.setEnabled(
                isOwner
        );

        editDate.setEnabled(
                isOwner
        );

        editDescription.setEnabled(
                isOwner
        );


        btnSelectImage.setVisibility(
                isOwner
                        ? View.VISIBLE
                        : View.GONE
        );


        btnUpdate.setVisibility(
                isOwner
                        ? View.VISIBLE
                        : View.GONE
        );


        btnDelete.setVisibility(
                isOwner
                        ? View.VISIBLE
                        : View.GONE
        );
    }


    /*
     * 전달 메모 저장
     */
    /*
     * 전달 메모 저장 전 확인창
     */
    private void saveHandoffNote() {

        // 습득물에서만 사용 가능
        if (!"FOUND".equals(currentItem.getType())) {
            return;
        }


        // 습득물 등록자 본인만 작성 가능
        if (currentItem.getUserId() != currentUserId) {

            Toast.makeText(
                    this,
                    "습득물 등록자만 메모를 작성할 수 있습니다.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // 분실물과 연결된 경우에만 작성 가능
        if (currentItem.getMatchedItemId() == -1) {

            Toast.makeText(
                    this,
                    "아직 분실물과 연결되지 않았습니다.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // 입력한 메모 가져오기
        String note =
                editHandoffNote
                        .getText()
                        .toString()
                        .trim();


        // 빈 메모 방지
        if (note.isEmpty()) {

            editHandoffNote.setError(
                    "전달 방법이나 장소를 간단히 적어주세요."
            );

            return;
        }


        /*
         * 바로 저장하지 않고
         * 확인창을 먼저 보여준다.
         */
        new AlertDialog.Builder(this)

                .setTitle(
                        "전달 메모 저장"
                )

                .setMessage(
                        "아래 내용으로 저장하시겠습니까?\n\n"
                                + note
                )

                // 확인
                .setPositiveButton(
                        "확인",

                        (dialog, which) -> {

                            saveHandoffNoteToDatabase(
                                    note
                            );
                        }
                )

                // 취소
                .setNegativeButton(
                        "취소",
                        null
                )

                .show();
    }
    /*
     * 확인을 눌렀을 때
     * 실제 DB에 전달 메모 저장
     */
    private void saveHandoffNoteToDatabase(
            String note
    ) {

        boolean success =
                itemService.saveHandoffNote(
                        currentItem.getId(),
                        note
                );


        if (success) {

            Toast.makeText(
                    this,
                    "전달 메모가 저장되었습니다.",
                    Toast.LENGTH_SHORT
            ).show();


            // 최신 데이터 다시 불러오기
            loadItemDetail();

        } else {

            Toast.makeText(
                    this,
                    "전달 메모 저장에 실패했습니다.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    /*
     * 습득물 연결
     *
     * 1개만 있으면 바로 정보 확인
     * 여러 개 있으면 목록에서 선택
     */
    private void showFoundItemDialog() {

        // 분실물에서만 사용
        if (!"LOST".equals(currentItem.getType())) {
            return;
        }

        // 분실물 등록자 본인만 사용
        if (currentItem.getUserId() != currentUserId) {
            return;
        }


        // 연결 가능한 습득물 가져오기
        ArrayList<Item> allItems =
                itemService.getAvailableFoundItems(
                        currentItem.getId()
                );


        ArrayList<Item> candidates =
                new ArrayList<>();


        // 내가 등록한 습득물은 제외
        for (Item item : allItems) {

            if (item.getUserId() != currentUserId) {

                candidates.add(item);
            }
        }


        // 연결 가능한 습득물이 없음
        if (candidates.isEmpty()) {

            Toast.makeText(
                    this,
                    "연결할 수 있는 습득물이 없습니다.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ======================================================
        // 연결 가능한 습득물이 1개인 경우
        // ======================================================

        if (candidates.size() == 1) {

            Item foundItem =
                    candidates.get(0);


            String nickname =
                    itemService.getUserNickname(
                            foundItem.getUserId()
                    );


            String message =
                    "물품명 : " + foundItem.getName()

                            + "\n\n카테고리 : "
                            + foundItem.getCategory()

                            + "\n색상 : "
                            + foundItem.getColor()

                            + "\n장소 : "
                            + foundItem.getLocation()

                            + "\n날짜 : "
                            + foundItem.getDate()

                            + "\n등록자 : "
                            + nickname

                            + "\n\n이 습득물이 맞습니까?";


            new AlertDialog.Builder(this)

                    .setTitle("습득물 확인")

                    .setMessage(message)

                    .setPositiveButton(
                            "확인",

                            (dialog, which) -> {

                                connectFoundItem(
                                        foundItem
                                );
                            }
                    )

                    .setNegativeButton(
                            "취소",
                            null
                    )

                    .show();


            return;
        }


        // ======================================================
        // 연결 가능한 습득물이 2개 이상인 경우
        // ======================================================

        String[] labels =
                new String[candidates.size()];


        for (int i = 0;
             i < candidates.size();
             i++) {

            Item item =
                    candidates.get(i);


            String nickname =
                    itemService.getUserNickname(
                            item.getUserId()
                    );


            labels[i] =
                    item.getName()

                            + "\n카테고리 : "
                            + item.getCategory()

                            + " / 색상 : "
                            + item.getColor()

                            + "\n장소 : "
                            + item.getLocation()

                            + "\n날짜 : "
                            + item.getDate()

                            + "\n등록자 : "
                            + nickname;
        }


        /*
         * 선택된 습득물 번호
         *
         * -1 = 아직 선택 안 함
         */
        final int[] selectedPosition = {-1};


        AlertDialog dialog =
                new AlertDialog.Builder(this)

                        .setTitle(
                                "연결할 습득물 선택"
                        )

                        /*
                         * 중요:
                         * 여기에는 setMessage()를 넣지 않는다.
                         *
                         * 그래야 습득물 선택 목록이 보인다.
                         */
                        .setSingleChoiceItems(
                                labels,
                                -1,

                                (dialogInterface, which) -> {

                                    selectedPosition[0] =
                                            which;
                                }
                        )

                        .setPositiveButton(
                                "확인",
                                null
                        )

                        .setNegativeButton(
                                "취소",
                                null
                        )

                        .create();


        /*
         * 확인 버튼 처리
         */
        dialog.setOnShowListener(
                dialogInterface -> {

                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener(
                            view -> {

                                // 아무것도 선택하지 않은 경우
                                if (selectedPosition[0] == -1) {

                                    Toast.makeText(
                                            this,
                                            "연결할 습득물을 선택해주세요.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                Item selectedFound =
                                        candidates.get(
                                                selectedPosition[0]
                                        );


                                /*
                                 * 선택한 습득물 연결
                                 */
                                connectFoundItem(
                                        selectedFound
                                );


                                dialog.dismiss();
                            }
                    );
                }
        );


        dialog.show();
    }
    /*
     * 실제 분실물 ↔ 습득물 연결
     */
    private void connectFoundItem(
            Item foundItem
    ) {

        boolean success =
                itemService.linkItems(
                        currentItem.getId(),
                        foundItem.getId()
                );


        if (success) {

            Toast.makeText(
                    this,
                    "분실물과 습득물이 연결되었습니다.",
                    Toast.LENGTH_SHORT
            ).show();


            // 상세화면 새로고침
            loadItemDetail();

        } else {

            Toast.makeText(
                    this,
                    "물품 연결에 실패했습니다.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    /*
     * 실제 수령 확인
     */
    private void showCompleteDialog() {

        new AlertDialog.Builder(this)

                .setTitle(
                        "물품 수령 완료"
                )

                .setMessage(
                        "분실했던 물품을 실제로 돌려받았습니까?\n\n" +
                                "확인하면 연결된 분실물과 습득물 정보가    모두 삭제됩니다."
                )

                .setPositiveButton(
                        "확인",

                        (dialog, which) ->
                                finishReceive()
                )

                .setNegativeButton(
                        "취소",
                        null
                )

                .show();
    }


    /*
     * 수령 완료 → 두 데이터 삭제
     */
    private void finishReceive() {

        if (!"LOST".equals(
                currentItem.getType()
        )) {

            return;
        }


        if (currentItem.getUserId()
                != currentUserId) {

            Toast.makeText(
                    this,
                    "분실물 등록자만 수령 완료할 수 있습니다.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (currentItem.getMatchedItemId()
                == -1) {

            Toast.makeText(
                    this,
                    "연결된 습득물이 없습니다.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        boolean success =
                itemService
                        .finishAndDeleteMatchedItems(
                                currentItem.getId()
                        );


        if (success) {

            Toast.makeText(
                    this,
                    "물품 수령이 완료되었습니다.\n분실물과 습득물 정보가 삭제되었습니다.",
                    Toast.LENGTH_LONG
            ).show();


            finish();

        } else {

            Toast.makeText(
                    this,
                    "수령 완료 처리에 실패했습니다.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    /*
     * DatePicker
     */
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
     * 입력검사
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
     * 수정
     */
    private void updateItem() {

        if (currentItem.getUserId()
                != currentUserId) {

            return;
        }


        if (!validateInput()) {

            return;
        }


        currentItem.setName(
                editName
                        .getText()
                        .toString()
                        .trim()
        );


        currentItem.setCategory(
                spinnerCategory
                        .getSelectedItem()
                        .toString()
        );


        currentItem.setColor(
                editColor
                        .getText()
                        .toString()
                        .trim()
        );


        currentItem.setLocation(
                editLocation
                        .getText()
                        .toString()
                        .trim()
        );


        currentItem.setDate(
                editDate
                        .getText()
                        .toString()
                        .trim()
        );


        currentItem.setDescription(
                editDescription
                        .getText()
                        .toString()
                        .trim()
        );


        currentItem.setImageUri(
                selectedImageUri
        );


        boolean success =
                itemService
                        .updateItem(
                                currentItem
                        );


        if (success) {

            Toast.makeText(
                    this,
                    "물품 정보가 수정되었습니다.",
                    Toast.LENGTH_SHORT
            ).show();


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
     * 삭제 확인
     */
    private void showDeleteDialog() {

        new AlertDialog.Builder(this)

                .setTitle(
                        "물품 삭제"
                )

                .setMessage(
                        "이 물품 정보를 삭제하시겠습니까?\n\n" +
                                "연결된 물품이 있다면 연결도 자동으로 해제됩니다."
                )

                .setPositiveButton(
                        "삭제",

                        (dialog, which) ->
                                deleteItem()
                )

                .setNegativeButton(
                        "취소",
                        null
                )

                .show();
    }


    /*
     * 일반 삭제
     */
    private void deleteItem() {

        if (currentItem.getUserId()
                != currentUserId) {

            return;
        }


        boolean success =
                itemService
                        .deleteItem(
                                itemId
                        );


        if (success) {

            Toast.makeText(
                    this,
                    "물품이 삭제되었습니다.",
                    Toast.LENGTH_SHORT
            ).show();


            finish();

        } else {

            Toast.makeText(
                    this,
                    "삭제에 실패했습니다.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
    @Override
    protected void onResume() {

        super.onResume();

        /*
         * 다른 화면에서 사용자 변경,
         * 물품 연결, 전달 메모 저장 등이 이루어진 경우
         * 최신 DB 내용을 다시 불러온다.
         */
        if (itemService != null && itemId != -1) {

            loadItemDetail();
        }
    }
}