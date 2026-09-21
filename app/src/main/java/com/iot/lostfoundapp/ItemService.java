package com.iot.lostfoundapp;

import android.content.Context;

import java.util.ArrayList;


/*
 * ============================================================
 * ItemService
 * ============================================================
 *
 * Activity와 Repository 사이에서
 * 물품 관련 기능을 처리하는 Service 클래스
 *
 * Activity
 *    ↓
 * ItemService
 *    ↓
 * ItemRepository
 *    ↓
 * DatabaseHelper
 *
 * ============================================================
 */
public class ItemService {


    private final ItemRepository repository;

    private final DatabaseHelper databaseHelper;


    // =========================================================
    // 생성자
    // =========================================================

    public ItemService(
            Context context
    ) {


        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );


        repository =
                databaseHelper;
    }


    // =========================================================
    // 필수 입력값 검사
    // =========================================================

    public boolean validateItem(
            Item item
    ) {


        if (item == null) {

            return false;
        }


        // 물품명
        if (item.getName() == null
                ||
                item.getName()
                        .trim()
                        .isEmpty()) {


            return false;
        }


        // 카테고리
        if (item.getCategory() == null
                ||
                item.getCategory()
                        .trim()
                        .isEmpty()) {


            return false;
        }


        // 장소
        if (item.getLocation() == null
                ||
                item.getLocation()
                        .trim()
                        .isEmpty()) {


            return false;
        }


        // 날짜
        if (item.getDate() == null
                ||
                item.getDate()
                        .trim()
                        .isEmpty()) {


            return false;
        }


        return true;
    }


    // =========================================================
    // 등록
    // =========================================================

    public boolean saveItem(
            Item item
    ) {


        if (!validateItem(
                item
        )) {


            return false;
        }


        /*
         * repository.insertItem() 자체가
         * boolean을 반환하므로
         *
         * != -1
         *
         * 같은 비교를 하지 않는다.
         */
        return repository.insertItem(
                item
        );
    }


    // =========================================================
    // 전체 물품
    // =========================================================

    public ArrayList<Item> getAllItems() {


        return repository
                .getAllItems();
    }


    // =========================================================
    // LOST / FOUND
    // =========================================================

    public ArrayList<Item> getItemsByType(
            String type
    ) {


        return repository
                .getItemsByType(
                        type
                );
    }


    // =========================================================
    // 검색
    // =========================================================

    public ArrayList<Item> searchItems(
            String keyword
    ) {


        return repository
                .searchItems(
                        keyword
                );
    }


    // =========================================================
    // 검색 + 필터 + 정렬
    // =========================================================

    public ArrayList<Item> getFilteredItems(
            String keyword,
            String type,
            String category,
            String sortOrder
    ) {


        return repository
                .getFilteredItems(

                        keyword,

                        type,

                        category,

                        sortOrder
                );
    }


    // =========================================================
    // 상세조회
    // =========================================================

    public Item getItem(
            int id
    ) {


        return repository
                .getItemById(
                        id
                );
    }


    // =========================================================
    // 수정
    // =========================================================

    public boolean updateItem(
            Item item
    ) {


        if (!validateItem(
                item
        )) {


            return false;
        }


        /*
         * updateItem()도 boolean
         */
        return repository.updateItem(
                item
        );
    }


    // =========================================================
    // 일반 삭제
    // =========================================================

    public boolean deleteItem(
            int id
    ) {


        /*
         * deleteItem()도 boolean
         */
        return repository.deleteItem(
                id
        );
    }


    // =========================================================
    // 연결 가능한 습득물
    // =========================================================

    public ArrayList<Item> getAvailableFoundItems(
            int lostItemId
    ) {


        return repository
                .getAvailableFoundItems(
                        lostItemId
                );
    }


    // =========================================================
    // LOST ↔ FOUND 연결
    // =========================================================

    public boolean linkItems(
            int lostItemId,
            int foundItemId
    ) {


        return repository
                .linkItems(

                        lostItemId,

                        foundItemId
                );
    }


    // =========================================================
    // 전달 메모
    // =========================================================

    public boolean saveHandoffNote(
            int foundItemId,
            String note
    ) {


        return repository
                .saveHandoffNote(

                        foundItemId,

                        note
                );
    }


    // =========================================================
    // 실제 수령 완료
    // =========================================================

    public boolean finishAndDeleteMatchedItems(
            int lostItemId
    ) {


        return repository
                .finishAndDeleteMatchedItems(
                        lostItemId
                );
    }


    // =========================================================
    // 삭제 실행 취소
    // =========================================================

    public boolean restoreDeletedItem(
            Item deletedItem,
            Item matchedItemBeforeDelete
    ) {


        return repository
                .restoreDeletedItem(

                        deletedItem,

                        matchedItemBeforeDelete
                );
    }


    // =========================================================
    // 사용자 ID → 닉네임
    // =========================================================

    public String getUserNickname(
            int userId
    ) {


        return databaseHelper
                .getUserNickname(
                        userId
                );
    }


    // =========================================================
    // 현재 사용자가 등록한 물품
    // =========================================================

    public ArrayList<Item> getItemsByUserId(
            int userId
    ) {


        return repository
                .getItemsByUserId(
                        userId
                );
    }
}