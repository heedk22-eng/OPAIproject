package com.iot.lostfoundapp;

import java.util.ArrayList;

/*
 * 물품 DB 기능을 정의하는 인터페이스
 *
 * 실제 구현은 DatabaseHelper가 한다.
 */
public interface ItemRepository {
    ArrayList<Item> getItemsByUserId(int userId);

    // 물품 등록
    long insertItem(Item item);

    // 전체 물품
    ArrayList<Item> getAllItems();

    // LOST / FOUND 조회
    ArrayList<Item> getItemsByType(String type);

    // 검색
    ArrayList<Item> searchItems(String keyword);

    // 검색 + 필터 + 정렬
    ArrayList<Item> getFilteredItems(
            String keyword,
            String type,
            String category,
            String sortOrder
    );

    // 상세조회
    Item getItemById(int id);

    // 수정
    int updateItem(Item item);

    // 삭제
    int deleteItem(int id);

    // 연결 가능한 습득물 조회
    ArrayList<Item> getAvailableFoundItems(
            int lostItemId
    );

    // 분실물 ↔ 습득물 연결
    boolean linkItems(
            int lostItemId,
            int foundItemId
    );

    // 습득자의 전달 메모 저장
    boolean saveHandoffNote(
            int foundItemId,
            String note
    );

    // 실제 수령 완료 후 연결된 두 데이터 삭제
    boolean finishAndDeleteMatchedItems(
            int lostItemId
    );
}