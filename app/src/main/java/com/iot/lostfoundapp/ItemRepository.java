package com.iot.lostfoundapp;

import java.util.ArrayList;


/*
 * ItemRepository
 *
 * 물품 데이터를 저장하고 조회하기 위해 필요한
 * 데이터베이스 기능을 정의한다.
 *
 * 실제 기능은 DatabaseHelper에서 구현한다.
 */
public interface ItemRepository {


    /*
     * 물품 등록
     *
     * 성공하면 새 행의 id
     * 실패하면 -1
     */
    long insertItem(Item item);


    /*
     * 모든 물품 조회
     */
    ArrayList<Item> getAllItems();


    /*
     * LOST 또는 FOUND로 구분 조회
     */
    ArrayList<Item> getItemsByType(String type);


    /*
     * 물품 검색
     */
    ArrayList<Item> searchItems(String keyword);


    /*
     * ID로 물품 한 개 조회
     */
    Item getItemById(int id);


    /*
     * 물품정보 수정
     *
     * 수정된 행의 개수 반환
     */
    int updateItem(Item item);


    /*
     * 물품 삭제
     *
     * 삭제된 행의 개수 반환
     */
    int deleteItem(int id);
}