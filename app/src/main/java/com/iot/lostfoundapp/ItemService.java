package com.iot.lostfoundapp;

import android.content.Context;

import java.util.ArrayList;


/*
 * Activity와 DatabaseHelper 사이에서
 * 기능을 처리하는 Service 클래스
 */
public class ItemService {

    private final ItemRepository repository;

    private final DatabaseHelper databaseHelper;


    public ItemService(Context context) {

        databaseHelper =
                new DatabaseHelper(
                        context.getApplicationContext()
                );

        repository =
                databaseHelper;
    }


    /*
     * 필수값 검사
     */
    public boolean validateItem(
            Item item
    ) {

        if (item == null) {
            return false;
        }


        if (item.getName() == null ||
                item.getName()
                        .trim()
                        .isEmpty()) {

            return false;
        }


        if (item.getCategory() == null ||
                item.getCategory()
                        .trim()
                        .isEmpty()) {

            return false;
        }


        if (item.getLocation() == null ||
                item.getLocation()
                        .trim()
                        .isEmpty()) {

            return false;
        }


        if (item.getDate() == null ||
                item.getDate()
                        .trim()
                        .isEmpty()) {

            return false;
        }


        return true;
    }


    /*
     * 등록
     */
    public boolean saveItem(
            Item item
    ) {

        if (!validateItem(item)) {
            return false;
        }


        return repository
                .insertItem(item) != -1;
    }


    public ArrayList<Item> getAllItems() {

        return repository
                .getAllItems();
    }


    public ArrayList<Item> getItemsByType(
            String type
    ) {

        return repository
                .getItemsByType(type);
    }


    public ArrayList<Item> searchItems(
            String keyword
    ) {

        return repository
                .searchItems(keyword);
    }


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


    /*
     * 상세조회
     */
    public Item getItem(
            int id
    ) {

        return repository
                .getItemById(id);
    }


    /*
     * 수정
     */
    public boolean updateItem(
            Item item
    ) {

        if (!validateItem(item)) {
            return false;
        }


        return repository
                .updateItem(item) > 0;
    }


    /*
     * 일반 삭제
     */
    public boolean deleteItem(
            int id
    ) {

        return repository
                .deleteItem(id) > 0;
    }


    /*
     * 연결 가능한 습득물
     */
    public ArrayList<Item> getAvailableFoundItems(
            int lostItemId
    ) {

        return repository
                .getAvailableFoundItems(
                        lostItemId
                );
    }


    /*
     * 분실물 ↔ 습득물 연결
     */
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


    /*
     * 전달 메모 저장
     */
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


    /*
     * 물품 수령 완료
     */
    public boolean finishAndDeleteMatchedItems(
            int lostItemId
    ) {

        return repository
                .finishAndDeleteMatchedItems(
                        lostItemId
                );
    }


    /*
     * 사용자 ID → 닉네임
     */
    public String getUserNickname(
            int userId
    ) {

        return databaseHelper
                .getNicknameByUserId(
                        userId
                );
    }
    /*
     * 현재 사용자가 등록한 물품만 가져오기
     */
    public ArrayList<Item> getItemsByUserId(
            int userId
    ) {

        return repository
                .getItemsByUserId(
                        userId
                );
    }
}