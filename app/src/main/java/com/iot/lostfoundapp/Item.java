package com.iot.lostfoundapp;


/*
 * Item
 *
 * 분실물과 습득물 하나의 정보를 저장하는 데이터 클래스
 *
 * type
 * LOST  = 분실물
 * FOUND = 습득물
 */
public class Item {

    // DB의 고유번호
    private int id;

    // LOST 또는 FOUND
    private String type;

    // 물품명
    private String name;

    // 카테고리
    private String category;

    // 색상
    private String color;

    // 장소
    private String location;

    // 날짜
    private String date;

    // 특징
    private String description;


    /*
     * 기본 생성자
     */
    public Item() {

    }


    /*
     * 모든 값을 한 번에 넣을 수 있는 생성자
     */
    public Item(
            int id,
            String type,
            String name,
            String category,
            String color,
            String location,
            String date,
            String description
    ) {

        this.id = id;
        this.type = type;
        this.name = name;
        this.category = category;
        this.color = color;
        this.location = location;
        this.date = date;
        this.description = description;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}