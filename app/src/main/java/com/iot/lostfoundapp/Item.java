package com.iot.lostfoundapp;


/*
 * ============================================================
 * Item
 * ============================================================
 *
 * 분실물 / 습득물 데이터를 저장하는 Model 클래스
 *
 * 필드
 *
 * id             : 물품 번호
 * type           : LOST / FOUND
 * name           : 물품명
 * category       : 카테고리
 * color          : 색상
 * location       : 장소
 * date           : 날짜
 * description    : 특징 / 설명
 * status         : SEARCHING / MATCHED
 * imageUri       : 사진 URI
 * userId         : 등록한 사용자 ID
 * matchedItemId  : 연결된 LOST / FOUND ID
 * handoffNote    : 전달 메모
 *
 * ============================================================
 */
public class Item {


    // =========================================================
    // Field
    // =========================================================

    private int id;

    private String type;

    private String name;

    private String category;

    private String color;

    private String location;

    private String date;

    private String description;

    private String status;

    private String imageUri;

    private int userId;

    private int matchedItemId;

    private String handoffNote;


    // =========================================================
    // 기본 생성자
    // =========================================================

    public Item() {


        /*
         * 새 물품의 기본값
         */
        this.id = -1;

        this.status = "SEARCHING";

        this.userId = -1;

        this.matchedItemId = -1;

        this.handoffNote = null;
    }


    // =========================================================
    // DB 조회용 전체 생성자
    //
    // DatabaseHelper의 cursorToItem()에서 사용
    // =========================================================

    public Item(
            int id,
            String type,
            String name,
            String category,
            String color,
            String location,
            String date,
            String description,
            String status,
            String imageUri,
            int userId,
            int matchedItemId,
            String handoffNote
    ) {


        this.id = id;

        this.type = type;

        this.name = name;

        this.category = category;

        this.color = color;

        this.location = location;

        this.date = date;

        this.description = description;

        this.status =
                status == null
                        ? "SEARCHING"
                        : status;

        this.imageUri = imageUri;

        this.userId = userId;

        this.matchedItemId = matchedItemId;

        this.handoffNote = handoffNote;
    }


    // =========================================================
    // 기존 코드 호환용 생성자
    //
    // handoffNote가 없는 이전 형태
    // =========================================================

    public Item(
            int id,
            String type,
            String name,
            String category,
            String color,
            String location,
            String date,
            String description,
            String status,
            String imageUri,
            int userId,
            int matchedItemId
    ) {


        this(
                id,
                type,
                name,
                category,
                color,
                location,
                date,
                description,
                status,
                imageUri,
                userId,
                matchedItemId,
                null
        );
    }


    // =========================================================
    // 새 물품 등록용 생성자
    //
    // ID는 SQLite AUTOINCREMENT이므로 아직 없음
    // =========================================================

    public Item(
            String type,
            String name,
            String category,
            String color,
            String location,
            String date,
            String description,
            String status,
            String imageUri,
            int userId
    ) {


        this(
                -1,
                type,
                name,
                category,
                color,
                location,
                date,
                description,
                status,
                imageUri,
                userId,
                -1,
                null
        );
    }


    // =========================================================
    // 새 물품 등록용 간단 생성자
    //
    // status는 자동으로 SEARCHING
    // =========================================================

    public Item(
            String type,
            String name,
            String category,
            String color,
            String location,
            String date,
            String description,
            String imageUri,
            int userId
    ) {


        this(
                -1,
                type,
                name,
                category,
                color,
                location,
                date,
                description,
                "SEARCHING",
                imageUri,
                userId,
                -1,
                null
        );
    }


    // =========================================================
    // Getter / Setter
    // =========================================================


    // ---------------------------------------------------------
    // ID
    // ---------------------------------------------------------

    public int getId() {

        return id;
    }


    public void setId(
            int id
    ) {

        this.id = id;
    }


    // ---------------------------------------------------------
    // TYPE
    // ---------------------------------------------------------

    public String getType() {

        return type;
    }


    public void setType(
            String type
    ) {

        this.type = type;
    }


    // ---------------------------------------------------------
    // NAME
    // ---------------------------------------------------------

    public String getName() {

        return name;
    }


    public void setName(
            String name
    ) {

        this.name = name;
    }


    // ---------------------------------------------------------
    // CATEGORY
    // ---------------------------------------------------------

    public String getCategory() {

        return category;
    }


    public void setCategory(
            String category
    ) {

        this.category = category;
    }


    // ---------------------------------------------------------
    // COLOR
    // ---------------------------------------------------------

    public String getColor() {

        return color;
    }


    public void setColor(
            String color
    ) {

        this.color = color;
    }


    // ---------------------------------------------------------
    // LOCATION
    // ---------------------------------------------------------

    public String getLocation() {

        return location;
    }


    public void setLocation(
            String location
    ) {

        this.location = location;
    }


    // ---------------------------------------------------------
    // DATE
    // ---------------------------------------------------------

    public String getDate() {

        return date;
    }


    public void setDate(
            String date
    ) {

        this.date = date;
    }


    // ---------------------------------------------------------
    // DESCRIPTION
    // ---------------------------------------------------------

    public String getDescription() {

        return description;
    }


    public void setDescription(
            String description
    ) {

        this.description = description;
    }


    // ---------------------------------------------------------
    // STATUS
    // ---------------------------------------------------------

    public String getStatus() {

        return status;
    }


    public void setStatus(
            String status
    ) {

        this.status = status;
    }


    // ---------------------------------------------------------
    // IMAGE URI
    // ---------------------------------------------------------

    public String getImageUri() {

        return imageUri;
    }


    public void setImageUri(
            String imageUri
    ) {

        this.imageUri = imageUri;
    }


    // ---------------------------------------------------------
    // USER ID
    // ---------------------------------------------------------

    public int getUserId() {

        return userId;
    }


    public void setUserId(
            int userId
    ) {

        this.userId = userId;
    }


    // ---------------------------------------------------------
    // MATCHED ITEM ID
    // ---------------------------------------------------------

    public int getMatchedItemId() {

        return matchedItemId;
    }


    public void setMatchedItemId(
            int matchedItemId
    ) {

        this.matchedItemId = matchedItemId;
    }


    // ---------------------------------------------------------
    // HANDOFF NOTE
    // ---------------------------------------------------------

    public String getHandoffNote() {

        return handoffNote;
    }


    public void setHandoffNote(
            String handoffNote
    ) {

        this.handoffNote = handoffNote;
    }
}