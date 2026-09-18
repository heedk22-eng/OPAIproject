package com.iot.lostfoundapp;

/*
 * 분실물 또는 습득물 하나의 정보를 저장하는 데이터 클래스
 */
public class Item {

    // DB 고유번호
    private int id;

    // LOST = 분실물 / FOUND = 습득물
    private String type;

    // 물품명
    private String name;

    // 카테고리
    private String category;

    // 색상
    private String color;

    // 분실/습득 장소
    private String location;

    // 날짜
    private String date;

    // 특징
    private String description;

    /*
     * SEARCHING = 아직 연결되지 않음
     * MATCHED   = 분실물과 습득물이 연결됨
     */
    private String status = "SEARCHING";

    // 갤러리에서 선택한 사진 URI
    private String imageUri;

    // 이 물품을 등록한 사용자 ID
    private int userId = -1;

    // 연결된 상대 물품 ID
    private int matchedItemId = -1;

    // 습득자가 남기는 전달 관련 메모
    private String handoffNote = "";


    public Item() {
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


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public int getMatchedItemId() {
        return matchedItemId;
    }

    public void setMatchedItemId(int matchedItemId) {
        this.matchedItemId = matchedItemId;
    }


    public String getHandoffNote() {
        return handoffNote;
    }

    public void setHandoffNote(String handoffNote) {
        this.handoffNote = handoffNote;
    }
}