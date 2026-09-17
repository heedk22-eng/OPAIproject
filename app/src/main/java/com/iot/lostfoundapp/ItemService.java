package com.iot.lostfoundapp;

import android.content.Context;

import java.util.ArrayList;


/*
 * ItemService
 *
 * Activity와 Repository 사이에서
 * 물품 관련 기능을 처리한다.
 */
public class ItemService {


    /*
     * 인터페이스 타입으로 선언
     *
     * 실제 객체는 DatabaseHelper이다.
     */
    private ItemRepository repository;


    /*
     * 생성자
     */
    public ItemService(Context context) {

        /*
         * DatabaseHelper가
         * ItemRepository를 구현하고 있기 때문에
         * 이런 형태로 사용할 수 있다.
         */
        repository =
                new DatabaseHelper(
                        context.getApplicationContext()
                );
    }


    /*
     * Item이 정상적인 데이터인지 확인
     */
    public boolean validateItem(Item item) {

        // Item 자체가 없는 경우
        if (item == null) {

            return false;
        }


        // 물품명 확인
        if (item.getName() == null
                || item.getName().trim().isEmpty()) {

            return false;
        }


        // 카테고리 확인
        if (item.getCategory() == null
                || item.getCategory()
                .trim()
                .isEmpty()) {

            return false;
        }


        // 장소 확인
        if (item.getLocation() == null
                || item.getLocation()
                .trim()
                .isEmpty()) {

            return false;
        }


        // 날짜 확인
        if (item.getDate() == null
                || item.getDate()
                .trim()
                .isEmpty()) {

            return false;
        }


        return true;
    }


    /*
     * 저장
     */
    public boolean saveItem(Item item) {

        // 잘못된 데이터라면 저장하지 않음
        if (!validateItem(item)) {

            return false;
        }


        /*
         * insertItem()
         *
         * 성공 → 0 이상의 ID
         * 실패 → -1
         */
        long result =
                repository.insertItem(item);


        return result != -1;
    }


    /*
     * 전체 조회
     */
    public ArrayList<Item> getAllItems() {

        return repository.getAllItems();
    }


    /*
     * LOST / FOUND 조회
     */
    public ArrayList<Item> getItemsByType(
            String type
    ) {

        return repository.getItemsByType(type);
    }


    /*
     * 검색
     */
    public ArrayList<Item> searchItems(
            String keyword
    ) {

        return repository.searchItems(keyword);
    }


    /*
     * 물품 하나 조회
     */
    public Item getItem(int id) {

        return repository.getItemById(id);
    }


    /*
     * 수정
     */
    public boolean updateItem(Item item) {

        // 필수값 검사
        if (!validateItem(item)) {

            return false;
        }


        /*
         * 수정된 행이 1개 이상이면 성공
         */
        return repository.updateItem(item) > 0;
    }


    /*
     * 삭제
     */
    public boolean deleteItem(int id) {

        /*
         * 삭제된 행이 1개 이상이면 성공
         */
        return repository.deleteItem(id) > 0;
    }
}