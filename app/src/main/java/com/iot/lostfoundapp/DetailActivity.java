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

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


/*
 * ============================================================
 * 물품 상세 화면
 * ============================================================
 *
 * 기능
 *
 * 1. 물품 상세정보 조회
 * 2. 물품 사진 표시 / 변경
 * 3. 등록자 확인
 * 4. 등록자만 수정 / 삭제
 * 5. LOST ↔ FOUND 연결
 * 6. 습득자의 전달 메모 작성
 * 7. 분실자의 전달 메모 확인
 * 8. 실제 수령 완료
 * 9. 삭제 Snackbar + 실행 취소
 * 10. 사진이 없을 경우 기본 이미지 표시
 *
 * ============================================================
 */
public class DetailActivity extends AppCompatActivity {


    // =========================================================
    // Intent Key
    // =========================================================

    public static final String EXTRA_ITEM_ID =
            "ITEM_ID";


    // =========================================================
    // 카테고리
    // =========================================================

    private static final String[] CATEGORIES = {

            "전자기기",
            "지갑/카드",
            "의류",
            "가방",
            "문구류",
            "열쇠",
            "기타"
    };


    // =========================================================
    // View
    // =========================================================

    private ImageView imagePreview;

    private Button btnSelectImage;


    private TextView textOwner;

    private TextView textStatus;

    private TextView textMatchInfo;


    private Button btnMatchFound;


    // =========================================================
    // 전달 메모
    // =========================================================

    private TextView textHandoffNoteTitle;

    private TextView textHandoffNote;

    private EditText editHandoffNote;

    private Button btnSaveHandoffNote;


    // =========================================================
    // 수령 완료
    // =========================================================

    private Button btnCompleteReturn;


    // =========================================================
    // 물품 정보
    // =========================================================

    private EditText editName;

    private Spinner spinnerCategory;

    private EditText editColor;

    private EditText editLocation;

    private EditText editDate;

    private EditText editDescription;


    // =========================================================
    // 수정 / 삭제
    // =========================================================

    private Button btnUpdate;

    private Button btnDelete;


    // =========================================================
    // 데이터
    // =========================================================

    private int itemId;

    private int currentUserId;

    private Item currentItem;

    private ItemService itemService;

    private String selectedImageUri;


    // =========================================================
    // 사진 선택
    // =========================================================

    private final ActivityResultLauncher<String[]>
            imagePicker =
            registerForActivityResult(

                    new ActivityResultContracts.OpenDocument(),

                    uri -> {


                        if (uri == null) {

                            return;
                        }


                        selectedImageUri =
                                uri.toString();


                        /*
                         * 앱을 종료한 후에도
                         * 선택한 사진을 읽을 수 있도록
                         * URI 권한 유지
                         */
                        try {

                            getContentResolver()
                                    .takePersistableUriPermission(

                                            uri,

                                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    );

                        } catch (Exception ignored) {

                        }


                        /*
                         * 선택한 사진 바로 표시
                         */
                        try {

                            imagePreview.setImageURI(
                                    uri
                            );

                        } catch (Exception e) {

                            showDefaultImage();
                        }
                    }
            );


    // =========================================================
    // onCreate
    // =========================================================

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


        // =====================================================
        // View 연결
        // =====================================================

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


        // =====================================================
        // 전달 메모
        // =====================================================

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


        // =====================================================
        // 수령 완료
        // =====================================================

        btnCompleteReturn =
                findViewById(
                        R.id.btnCompleteReturn
                );


        // =====================================================
        // 입력 View
        // =====================================================

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


        // =====================================================
        // 수정 / 삭제
        // =====================================================

        btnUpdate =
                findViewById(
                        R.id.btnUpdate
                );


        btnDelete =
                findViewById(
                        R.id.btnDelete
                );


        // =====================================================
        // 카테고리 Spinner
        // =====================================================

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


        // =====================================================
        // Service
        // =====================================================

        itemService =
                new ItemService(this);


        // =====================================================
        // 현재 사용자
        // =====================================================

        SessionManager sessionManager =
                new SessionManager(this);


        currentUserId =
                sessionManager.getUserId();


        // =====================================================
        // Item ID
        // =====================================================

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


        // =====================================================
        // 상세정보 불러오기
        // =====================================================

        loadItemDetail();


        // =====================================================
        // 사진 선택
        // =====================================================

        btnSelectImage.setOnClickListener(

                view ->

                        imagePicker.launch(

                                new String[]{
                                        "image/*"
                                }
                        )
        );


        // =====================================================
        // 날짜 선택
        // =====================================================

        editDate.setOnClickListener(

                view ->
                        showDatePicker()
        );


        // =====================================================
        // 습득물 연결
        // =====================================================

        btnMatchFound.setOnClickListener(

                view ->
                        showFoundItemDialog()
        );


        // =====================================================
        // 전달 메모 저장
        // =====================================================

        btnSaveHandoffNote.setOnClickListener(

                view ->
                        saveHandoffNote()
        );


        // =====================================================
        // 수령 완료
        // =====================================================

        btnCompleteReturn.setOnClickListener(

                view ->
                        showCompleteDialog()
        );


        // =====================================================
        // 수정
        // =====================================================

        btnUpdate.setOnClickListener(

                view ->
                        updateItem()
        );


        // =====================================================
        // 삭제
        // =====================================================

        btnDelete.setOnClickListener(

                view ->
                        showDeleteDialog()
        );
    }


    // =========================================================
    // 상세정보 조회
    // =========================================================

    private void loadItemDetail() {


        currentItem =
                itemService.getItem(
                        itemId
                );


        // =====================================================
        // 존재하지 않는 물품
        // =====================================================

        if (currentItem == null) {


            Toast.makeText(

                    this,

                    "물품 정보를 찾을 수 없습니다.",

                    Toast.LENGTH_SHORT

            ).show();


            finish();

            return;
        }


        // =====================================================
        // 물품명
        // =====================================================

        editName.setText(
                currentItem.getName()
        );


        // =====================================================
        // 카테고리
        // =====================================================

        for (int i = 0;
             i < CATEGORIES.length;
             i++) {


            if (CATEGORIES[i]
                    .equals(
                            currentItem.getCategory()
                    )) {


                spinnerCategory.setSelection(
                        i
                );


                break;
            }
        }


        // =====================================================
        // 색상
        // =====================================================

        editColor.setText(
                currentItem.getColor()
        );


        // =====================================================
        // 장소
        // =====================================================

        editLocation.setText(
                currentItem.getLocation()
        );


        // =====================================================
        // 날짜
        // =====================================================

        editDate.setText(
                currentItem.getDate()
        );


        // =====================================================
        // 특징
        // =====================================================

        editDescription.setText(
                currentItem.getDescription()
        );


        // =====================================================
        // 사진
        // =====================================================

        selectedImageUri =
                currentItem.getImageUri();


        /*
         * 우선 기본 이미지를 표시한다.
         *
         * RecyclerView와 마찬가지로
         * 사진이 없거나 URI 오류가 발생해도
         * 빈 화면이 나오지 않도록 한다.
         */
        showDefaultImage();


        if (selectedImageUri != null
                &&
                !selectedImageUri.trim().isEmpty()) {


            try {


                imagePreview.setImageURI(

                        Uri.parse(
                                selectedImageUri
                        )
                );


            } catch (Exception e) {


                showDefaultImage();
            }
        }


        // =====================================================
        // 상태 / 매칭 / 메모 UI
        // =====================================================

        updateMatchUI();
    }


    // =========================================================
    // 기본 이미지
    // =========================================================

    private void showDefaultImage() {


        imagePreview.setImageResource(
                R.drawable.ic_item_placeholder
        );
    }


    // =========================================================
    // 상태 / 연결 / 전달 메모 UI
    // =========================================================

    private void updateMatchUI() {


        // =====================================================
        // 등록자
        // =====================================================

        String ownerNickname =
                itemService
                        .getUserNickname(
                                currentItem.getUserId()
                        );


        textOwner.setText(

                "등록자 : " +
                        safeText(
                                ownerNickname
                        )
        );


        // =====================================================
        // 상태
        // =====================================================

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


        // =====================================================
        // 관련 기능 일단 숨김
        // =====================================================

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


        // =====================================================
        // 등록자인지 확인
        // =====================================================

        boolean isOwner =

                currentItem.getUserId()
                        == currentUserId;


        // =====================================================
        // 수정 / 삭제 권한
        // =====================================================

        setEditPermission(
                isOwner
        );


        // =====================================================
        // 연결 상대 물품 조회
        // =====================================================

        Item matchedItem =
                null;


        if (currentItem.getMatchedItemId()
                != -1) {


            matchedItem =
                    itemService.getItem(

                            currentItem
                                    .getMatchedItemId()
                    );
        }


        // =====================================================
        // 연결 상대가 없는 경우
        // =====================================================

        if (matchedItem == null) {


            textMatchInfo.setText(
                    "연결된 물품 없음"
            );


            /*
             * 자기 분실물인 경우에만
             * 습득물 연결 버튼 표시
             */
            if (isOwner
                    &&
                    "LOST".equals(
                            currentItem.getType()
                    )) {


                btnMatchFound.setVisibility(
                        View.VISIBLE
                );
            }


            return;
        }


        // =====================================================
        // 연결 상대 정보
        // =====================================================

        String matchedOwner =
                itemService
                        .getUserNickname(
                                matchedItem.getUserId()
                        );


        textMatchInfo.setText(

                "연결된 물품 : "
                        + safeText(
                        matchedItem.getName()
                )

                        + "\n등록자 : "
                        + safeText(
                        matchedOwner
                )
        );


        // =====================================================
        // 현재 물품이 LOST
        // =====================================================

        if ("LOST".equals(
                currentItem.getType()
        )) {


            /*
             * 습득물에 작성된 전달 메모를
             * 분실자가 읽는다.
             */
            String note =
                    matchedItem.getHandoffNote();


            textHandoffNoteTitle.setVisibility(
                    View.VISIBLE
            );


            textHandoffNote.setVisibility(
                    View.VISIBLE
            );


            if (note == null
                    ||
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
             * 분실물 등록자만
             * 수령 완료 버튼 사용 가능
             */
            if (isOwner) {


                btnCompleteReturn.setVisibility(
                        View.VISIBLE
                );
            }
        }


        // =====================================================
        // 현재 물품이 FOUND
        // =====================================================

        if ("FOUND".equals(
                currentItem.getType()
        )) {


            /*
             * 습득물 등록자만
             * 전달 메모 작성 가능
             */
            if (isOwner) {


                textHandoffNoteTitle.setVisibility(
                        View.VISIBLE
                );


                editHandoffNote.setVisibility(
                        View.VISIBLE
                );


                btnSaveHandoffNote.setVisibility(
                        View.VISIBLE
                );


                String note =
                        currentItem.getHandoffNote();


                if (note != null) {


                    editHandoffNote.setText(
                            note
                    );


                } else {


                    editHandoffNote.setText(
                            ""
                    );
                }
            }
        }
    }


    // =========================================================
    // 등록자 수정 권한
    // =========================================================

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


    // =========================================================
    // 전달 메모 저장
    // =========================================================

    private void saveHandoffNote() {


        // =====================================================
        // FOUND만 가능
        // =====================================================

        if (!"FOUND".equals(
                currentItem.getType()
        )) {


            return;
        }


        // =====================================================
        // 작성자 본인만 가능
        // =====================================================

        if (currentItem.getUserId()
                != currentUserId) {


            return;
        }


        // =====================================================
        // 입력값
        // =====================================================

        String note =
                editHandoffNote
                        .getText()
                        .toString()
                        .trim();


        if (note.isEmpty()) {


            editHandoffNote.setError(

                    "전달 방법이나 장소를 간단히 적어주세요."
            );


            return;
        }


        // =====================================================
        // 저장 확인
        // =====================================================

        new AlertDialog.Builder(this)

                .setTitle(
                        "전달 메모 저장"
                )

                .setMessage(

                        "아래 내용으로 전달 메모를 저장하시겠습니까?\n\n"
                                + note
                )

                .setPositiveButton(

                        "확인",

                        (dialog, which) ->
                                saveHandoffNoteToDatabase(
                                        note
                                )
                )

                .setNegativeButton(
                        "취소",
                        null
                )

                .show();
    }


    // =========================================================
    // 실제 전달 메모 DB 저장
    // =========================================================

    private void saveHandoffNoteToDatabase(
            String note
    ) {


        boolean success =
                itemService
                        .saveHandoffNote(

                                currentItem.getId(),

                                note
                        );


        if (success) {


            Toast.makeText(

                    this,

                    "전달 메모가 저장되었습니다.",

                    Toast.LENGTH_SHORT

            ).show();


            loadItemDetail();


        } else {


            Toast.makeText(

                    this,

                    "메모 저장에 실패했습니다.",

                    Toast.LENGTH_SHORT

            ).show();
        }
    }


    // =========================================================
    // 연결할 습득물 조회
    // =========================================================

    private void showFoundItemDialog() {


        // =====================================================
        // LOST 물품만 가능
        // =====================================================

        if (!"LOST".equals(
                currentItem.getType()
        )) {


            return;
        }


        // =====================================================
        // 분실물 등록자만 가능
        // =====================================================

        if (currentItem.getUserId()
                != currentUserId) {


            return;
        }


        // =====================================================
        // 연결 가능한 FOUND 조회
        // =====================================================

        ArrayList<Item> allItems =
                itemService
                        .getAvailableFoundItems(

                                currentItem.getId()
                        );


        ArrayList<Item> candidates =
                new ArrayList<>();


        /*
         * 자기 자신이 등록한 습득물은 제외한다.
         */
        for (Item item : allItems) {


            if (item.getUserId()
                    != currentUserId) {


                candidates.add(
                        item
                );
            }
        }


        // =====================================================
        // 후보 없음
        // =====================================================

        if (candidates.isEmpty()) {


            Toast.makeText(

                    this,

                    "연결할 수 있는 습득물이 없습니다.",

                    Toast.LENGTH_SHORT

            ).show();


            return;
        }


        // =====================================================
        // 후보가 1개뿐인 경우
        // =====================================================

        if (candidates.size() == 1) {


            showSingleFoundConfirmation(

                    candidates.get(
                            0
                    )
            );


            return;
        }


        // =====================================================
        // 후보가 여러 개인 경우
        // =====================================================

        showMultipleFoundDialog(
                candidates
        );
    }


    // =========================================================
    // 후보가 1개인 경우 바로 상세 확인
    // =========================================================

    private void showSingleFoundConfirmation(
            Item foundItem
    ) {


        String nickname =
                itemService
                        .getUserNickname(
                                foundItem.getUserId()
                        );


        String message =

                "물품명 : "
                        + safeText(
                        foundItem.getName()
                )

                        + "\n\n카테고리 : "
                        + safeText(
                        foundItem.getCategory()
                )

                        + "\n색상 : "
                        + safeText(
                        foundItem.getColor()
                )

                        + "\n장소 : "
                        + safeText(
                        foundItem.getLocation()
                )

                        + "\n날짜 : "
                        + safeText(
                        foundItem.getDate()
                )

                        + "\n등록자 : "
                        + safeText(
                        nickname
                )

                        + "\n\n이 습득물이 맞습니까?";


        new AlertDialog.Builder(this)

                .setTitle(
                        "습득물 확인"
                )

                .setMessage(
                        message
                )

                .setPositiveButton(

                        "확인",

                        (dialog, which) ->

                                connectFoundItem(
                                        foundItem
                                )
                )

                .setNegativeButton(
                        "취소",
                        null
                )

                .show();
    }


    // =========================================================
    // 후보가 여러 개인 경우 선택 Dialog
    // =========================================================

    private void showMultipleFoundDialog(
            ArrayList<Item> candidates
    ) {


        String[] labels =
                new String[
                        candidates.size()
                        ];


        // =====================================================
        // 후보 화면 표시 내용
        // =====================================================

        for (int i = 0;
             i < candidates.size();
             i++) {


            Item item =
                    candidates.get(
                            i
                    );


            String nickname =
                    itemService
                            .getUserNickname(
                                    item.getUserId()
                            );


            labels[i] =

                    safeText(
                            item.getName()
                    )

                            + "\n카테고리 : "
                            + safeText(
                            item.getCategory()
                    )

                            + "\n색상 : "
                            + safeText(
                            item.getColor()
                    )

                            + "\n장소 : "
                            + safeText(
                            item.getLocation()
                    )

                            + "\n날짜 : "
                            + safeText(
                            item.getDate()
                    )

                            + "\n등록자 : "
                            + safeText(
                            nickname
                    );
        }


        /*
         * 선택된 번호
         *
         * -1 = 아직 아무것도 선택하지 않음
         */
        final int[] selectedIndex = {
                -1
        };


        AlertDialog dialog =
                new AlertDialog.Builder(this)

                        .setTitle(
                                "연결할 습득물 선택"
                        )

                        .setSingleChoiceItems(

                                labels,

                                -1,

                                (dialogInterface, which) ->

                                        selectedIndex[0] =
                                                which
                        )

                        /*
                         * 아래에서 직접 클릭 처리를 하기 때문에
                         * 여기서는 null로 설정
                         */
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
         * 선택하지 않은 상태에서 확인을 눌렀을 때
         * Dialog가 닫히지 않도록 직접 처리
         */
        dialog.setOnShowListener(

                dialogInterface -> {


                    dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            )
                            .setOnClickListener(

                                    view -> {


                                        if (selectedIndex[0]
                                                == -1) {


                                            Toast.makeText(

                                                    this,

                                                    "연결할 습득물을 선택해주세요.",

                                                    Toast.LENGTH_SHORT

                                            ).show();


                                            return;
                                        }


                                        Item selectedFound =
                                                candidates.get(

                                                        selectedIndex[0]
                                                );


                                        dialog.dismiss();


                                        connectFoundItem(
                                                selectedFound
                                        );
                                    }
                            );
                }
        );


        dialog.show();
    }


    // =========================================================
    // LOST ↔ FOUND 실제 연결
    // =========================================================

    private void connectFoundItem(
            Item foundItem
    ) {


        boolean success =
                itemService
                        .linkItems(

                                currentItem.getId(),

                                foundItem.getId()
                        );


        if (success) {


            Toast.makeText(

                    this,

                    "분실물과 습득물이 연결되었습니다.",

                    Toast.LENGTH_SHORT

            ).show();


            loadItemDetail();


        } else {


            Toast.makeText(

                    this,

                    "물품 연결에 실패했습니다.",

                    Toast.LENGTH_SHORT

            ).show();
        }
    }


    // =========================================================
    // 실제 수령 확인 Dialog
    // =========================================================

    private void showCompleteDialog() {


        new AlertDialog.Builder(this)

                .setTitle(
                        "물품 수령 완료"
                )

                .setMessage(

                        "분실했던 물품을 실제로 돌려받았습니까?\n\n"
                                +
                                "확인하면 연결된 분실물과 습득물 정보가 모두 삭제됩니다."
                )

                .setPositiveButton(

                        "물품을 받았습니다",

                        (dialog, which) ->
                                finishReceive()
                )

                .setNegativeButton(
                        "취소",
                        null
                )

                .show();
    }


    // =========================================================
    // 수령 완료
    // =========================================================

    private void finishReceive() {


        // =====================================================
        // LOST만 가능
        // =====================================================

        if (!"LOST".equals(
                currentItem.getType()
        )) {


            return;
        }


        // =====================================================
        // 분실물 등록자만 가능
        // =====================================================

        if (currentItem.getUserId()
                != currentUserId) {


            Toast.makeText(

                    this,

                    "분실물 등록자만 수령 완료할 수 있습니다.",

                    Toast.LENGTH_SHORT

            ).show();


            return;
        }


        // =====================================================
        // 연결 확인
        // =====================================================

        if (currentItem.getMatchedItemId()
                == -1) {


            Toast.makeText(

                    this,

                    "연결된 습득물이 없습니다.",

                    Toast.LENGTH_SHORT

            ).show();


            return;
        }


        // =====================================================
        // LOST + FOUND 모두 삭제
        // =====================================================

        boolean success =
                itemService
                        .finishAndDeleteMatchedItems(

                                currentItem.getId()
                        );


        if (success) {


            Toast.makeText(

                    this,

                    "물품 수령이 완료되었습니다.\n"
                            +
                            "분실물과 습득물 정보가 삭제되었습니다.",

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


    // =========================================================
    // 날짜 선택
    // =========================================================

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


    // =========================================================
    // 입력값 검사
    // =========================================================

    private boolean validateInput() {


        // =====================================================
        // 물품명
        // =====================================================

        if (editName
                .getText()
                .toString()
                .trim()
                .isEmpty()) {


            editName.setError(
                    "물품명을 입력해주세요."
            );


            editName.requestFocus();


            return false;
        }


        // =====================================================
        // 장소
        // =====================================================

        if (editLocation
                .getText()
                .toString()
                .trim()
                .isEmpty()) {


            editLocation.setError(
                    "장소를 입력해주세요."
            );


            editLocation.requestFocus();


            return false;
        }


        // =====================================================
        // 날짜
        // =====================================================

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


    // =========================================================
    // 물품 수정
    // =========================================================

    private void updateItem() {


        // =====================================================
        // 본인 물품인지 확인
        // =====================================================

        if (currentItem.getUserId()
                != currentUserId) {


            return;
        }


        // =====================================================
        // 입력 검사
        // =====================================================

        if (!validateInput()) {


            return;
        }


        // =====================================================
        // 변경 값 Item에 반영
        // =====================================================

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


        // =====================================================
        // DB UPDATE
        // =====================================================

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


    // =========================================================
    // 삭제 확인
    // =========================================================

    private void showDeleteDialog() {


        new AlertDialog.Builder(this)

                .setTitle(
                        "물품 삭제"
                )

                .setMessage(

                        "이 물품 정보를 삭제하시겠습니까?\n\n"
                                +
                                "연결된 물품이 있다면 연결도 자동으로 해제됩니다.\n"
                                +
                                "삭제 후 잠시 동안 실행 취소할 수 있습니다."
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


    // =========================================================
    // 물품 삭제
    // =========================================================

    private void deleteItem() {


        // =====================================================
        // 본인의 물품만 삭제 가능
        // =====================================================

        if (currentItem.getUserId()
                != currentUserId) {


            return;
        }


        // =====================================================
        // 실행 취소를 위해 삭제 전 상태 저장
        // =====================================================

        /*
         * 현재 Item 객체 보관
         */
        final Item deletedItem =
                currentItem;


        /*
         * 연결 상대방 Item도 보관한다.
         *
         * deleteItem()은 연결 상대방의
         *
         * matchedItemId
         * status
         * handoffNote
         *
         * 를 초기화하므로 실행 취소 시
         * 상대방 상태도 복구해야 한다.
         */
        Item matchedItemSnapshot =
                null;


        if (currentItem.getMatchedItemId()
                != -1) {


            matchedItemSnapshot =
                    itemService.getItem(

                            currentItem
                                    .getMatchedItemId()
                    );
        }


        final Item finalMatchedItemSnapshot =
                matchedItemSnapshot;


        // =====================================================
        // DB DELETE
        // =====================================================

        boolean success =
                itemService
                        .deleteItem(
                                itemId
                        );


        if (!success) {


            Toast.makeText(

                    this,

                    "삭제에 실패했습니다.",

                    Toast.LENGTH_SHORT

            ).show();


            return;
        }


        // =====================================================
        // 삭제 상태에서 다른 버튼 사용 방지
        // =====================================================

        setDeletePending(
                true
        );


        // =====================================================
        // Snackbar
        // =====================================================

        Snackbar snackbar =
                Snackbar.make(

                        findViewById(
                                android.R.id.content
                        ),

                        "물품이 삭제되었습니다.",

                        Snackbar.LENGTH_LONG
                );


        // =====================================================
        // 실행 취소
        // =====================================================

        snackbar.setAction(

                "실행 취소",

                view -> {


                    boolean restored =
                            itemService
                                    .restoreDeletedItem(

                                            deletedItem,

                                            finalMatchedItemSnapshot
                                    );


                    // =========================================
                    // 복구 성공
                    // =========================================

                    if (restored) {


                        Toast.makeText(

                                this,

                                "삭제가 취소되었습니다.",

                                Toast.LENGTH_SHORT

                        ).show();


                        /*
                         * 버튼 다시 활성화
                         */
                        setDeletePending(
                                false
                        );


                        /*
                         * 복원된 데이터를 DB에서 다시 조회
                         */
                        loadItemDetail();


                    } else {


                        // =====================================
                        // 복구 실패
                        // =====================================

                        Toast.makeText(

                                this,

                                "삭제 복원에 실패했습니다.",

                                Toast.LENGTH_SHORT

                        ).show();


                        finish();
                    }
                }
        );


        // =====================================================
        // Snackbar 종료
        // =====================================================

        snackbar.addCallback(

                new Snackbar.Callback() {


                    @Override
                    public void onDismissed(
                            Snackbar transientBottomBar,
                            int event
                    ) {


                        /*
                         * 사용자가 실행 취소 버튼을 누른 경우
                         *
                         * 물품이 복원됐으므로
                         * 상세화면을 유지한다.
                         */
                        if (event
                                ==
                                Snackbar.Callback
                                        .DISMISS_EVENT_ACTION) {


                            return;
                        }


                        /*
                         * Snackbar 시간이 끝났는데
                         * 실행 취소하지 않았다면
                         *
                         * 삭제 확정 → 상세화면 종료
                         */
                        finish();
                    }
                }
        );


        snackbar.show();
    }


    // =========================================================
    // 삭제 대기 중 버튼 잠금
    // =========================================================

    private void setDeletePending(
            boolean pending
    ) {


        boolean enabled =
                !pending;


        btnDelete.setEnabled(
                enabled
        );


        btnUpdate.setEnabled(
                enabled
        );


        btnSelectImage.setEnabled(
                enabled
        );


        btnMatchFound.setEnabled(
                enabled
        );


        btnSaveHandoffNote.setEnabled(
                enabled
        );


        btnCompleteReturn.setEnabled(
                enabled
        );


        editName.setEnabled(
                enabled
        );


        spinnerCategory.setEnabled(
                enabled
        );


        editColor.setEnabled(
                enabled
        );


        editLocation.setEnabled(
                enabled
        );


        editDate.setEnabled(
                enabled
        );


        editDescription.setEnabled(
                enabled
        );


        editHandoffNote.setEnabled(
                enabled
        );
    }


    // =========================================================
    // NULL 문자열 처리
    // =========================================================

    private String safeText(
            String value
    ) {


        if (value == null) {

            return "";
        }


        return value;
    }
}