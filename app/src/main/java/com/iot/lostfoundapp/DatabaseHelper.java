package com.iot.lostfoundapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


/*
 * ============================================================
 * SQLite 데이터베이스 관리 클래스
 * ============================================================
 *
 * 역할
 *
 * 1. 사용자 저장 / 조회
 * 2. 물품 등록 / 조회 / 수정 / 삭제
 * 3. 검색 / 필터
 * 4. 사용자별 물품 조회
 * 5. LOST ↔ FOUND 연결
 * 6. 전달 메모 저장
 * 7. 수령 완료 처리
 * 8. 삭제 실행 취소 복원
 *
 * ============================================================
 */
public class DatabaseHelper
        extends SQLiteOpenHelper
        implements ItemRepository {


    // =========================================================
    // Database
    // =========================================================

    /*
     * 기존 DatabaseHelper에서 다른 이름을 사용하고 있었다면
     * 반드시 기존 이름을 그대로 사용할 것
     */
    private static final String DATABASE_NAME =
            "lost_found.db";


    /*
     * 현재 프로젝트 DB 버전
     */
    private static final int DATABASE_VERSION =
            5;


    // =========================================================
    // Table
    // =========================================================

    private static final String TABLE_USER =
            "app_user";


    private static final String TABLE_ITEM =
            "item";


    // =========================================================
    // User Column
    // =========================================================

    private static final String USER_ID =
            "id";


    private static final String USER_NICKNAME =
            "nickname";


    // =========================================================
    // Item Column
    // =========================================================

    private static final String ITEM_ID =
            "id";


    private static final String ITEM_TYPE =
            "type";


    private static final String ITEM_NAME =
            "name";


    private static final String ITEM_CATEGORY =
            "category";


    private static final String ITEM_COLOR =
            "color";


    private static final String ITEM_LOCATION =
            "location";


    private static final String ITEM_DATE =
            "date";


    private static final String ITEM_DESCRIPTION =
            "description";


    private static final String ITEM_STATUS =
            "status";


    private static final String ITEM_IMAGE_URI =
            "image_uri";


    private static final String ITEM_USER_ID =
            "user_id";


    private static final String ITEM_MATCHED_ID =
            "matched_item_id";


    private static final String ITEM_HANDOFF_NOTE =
            "handoff_note";


    // =========================================================
    // 생성자
    // =========================================================

    public DatabaseHelper(
            Context context
    ) {

        super(
                context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
    }


    // =========================================================
    // DB 최초 생성
    // =========================================================

    @Override
    public void onCreate(
            SQLiteDatabase db
    ) {


        // =====================================================
        // 사용자 테이블
        // =====================================================

        String createUserTable =

                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_USER
                        + " ("

                        + USER_ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                        + USER_NICKNAME
                        + " TEXT NOT NULL COLLATE NOCASE UNIQUE"

                        + ")";


        db.execSQL(
                createUserTable
        );


        // =====================================================
        // 물품 테이블
        // =====================================================

        String createItemTable =

                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_ITEM
                        + " ("

                        + ITEM_ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                        + ITEM_TYPE
                        + " TEXT NOT NULL, "

                        + ITEM_NAME
                        + " TEXT NOT NULL, "

                        + ITEM_CATEGORY
                        + " TEXT NOT NULL, "

                        + ITEM_COLOR
                        + " TEXT, "

                        + ITEM_LOCATION
                        + " TEXT NOT NULL, "

                        + ITEM_DATE
                        + " TEXT NOT NULL, "

                        + ITEM_DESCRIPTION
                        + " TEXT, "

                        + ITEM_STATUS
                        + " TEXT NOT NULL DEFAULT 'SEARCHING', "

                        + ITEM_IMAGE_URI
                        + " TEXT, "

                        + ITEM_USER_ID
                        + " INTEGER, "

                        + ITEM_MATCHED_ID
                        + " INTEGER, "

                        + ITEM_HANDOFF_NOTE
                        + " TEXT"

                        + ")";


        db.execSQL(
                createItemTable
        );
    }


    // =========================================================
    // DB 버전 변경
    // =========================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {


        /*
         * 기존 데이터를 삭제하지 않고
         * 필요한 컬럼만 추가하는 방식
         */


        // =====================================================
        // 사용자 테이블이 없으면 생성
        // =====================================================

        db.execSQL(

                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_USER
                        + " ("
                        + USER_ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + USER_NICKNAME
                        + " TEXT NOT NULL COLLATE NOCASE UNIQUE"
                        + ")"
        );


        // =====================================================
        // item 테이블이 아예 없다면 생성
        // =====================================================

        if (!tableExists(
                db,
                TABLE_ITEM
        )) {


            String createItemTable =

                    "CREATE TABLE "
                            + TABLE_ITEM
                            + " ("

                            + ITEM_ID
                            + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                            + ITEM_TYPE
                            + " TEXT NOT NULL, "

                            + ITEM_NAME
                            + " TEXT NOT NULL, "

                            + ITEM_CATEGORY
                            + " TEXT NOT NULL, "

                            + ITEM_COLOR
                            + " TEXT, "

                            + ITEM_LOCATION
                            + " TEXT NOT NULL, "

                            + ITEM_DATE
                            + " TEXT NOT NULL, "

                            + ITEM_DESCRIPTION
                            + " TEXT, "

                            + ITEM_STATUS
                            + " TEXT NOT NULL DEFAULT 'SEARCHING', "

                            + ITEM_IMAGE_URI
                            + " TEXT, "

                            + ITEM_USER_ID
                            + " INTEGER, "

                            + ITEM_MATCHED_ID
                            + " INTEGER, "

                            + ITEM_HANDOFF_NOTE
                            + " TEXT"

                            + ")";


            db.execSQL(
                    createItemTable
            );


            return;
        }


        // =====================================================
        // 필요한 컬럼만 안전하게 추가
        // =====================================================

        addColumnIfMissing(
                db,
                TABLE_ITEM,
                ITEM_STATUS,
                "TEXT NOT NULL DEFAULT 'SEARCHING'"
        );


        addColumnIfMissing(
                db,
                TABLE_ITEM,
                ITEM_IMAGE_URI,
                "TEXT"
        );


        addColumnIfMissing(
                db,
                TABLE_ITEM,
                ITEM_USER_ID,
                "INTEGER"
        );


        addColumnIfMissing(
                db,
                TABLE_ITEM,
                ITEM_MATCHED_ID,
                "INTEGER"
        );


        addColumnIfMissing(
                db,
                TABLE_ITEM,
                ITEM_HANDOFF_NOTE,
                "TEXT"
        );
    }


    // =========================================================
    // 사용자 등록
    // =========================================================

    public int insertUser(
            String nickname
    ) {


        if (nickname == null
                ||
                nickname.trim().isEmpty()) {


            return -1;
        }


        SQLiteDatabase db =
                getWritableDatabase();


        ContentValues values =
                new ContentValues();


        values.put(
                USER_NICKNAME,
                nickname.trim()
        );


        long result =
                db.insertWithOnConflict(

                        TABLE_USER,

                        null,

                        values,

                        SQLiteDatabase.CONFLICT_IGNORE
                );


        if (result == -1) {

            return -1;
        }


        return (int) result;
    }


    // =========================================================
    // 사용자 등록 별칭 메서드
    // =========================================================

    public int createUser(
            String nickname
    ) {

        return insertUser(
                nickname
        );
    }


    // =========================================================
    // 사용자 전체 조회
    // =========================================================

    public ArrayList<User> getAllUsers() {


        ArrayList<User> users =
                new ArrayList<>();


        SQLiteDatabase db =
                getReadableDatabase();


        Cursor cursor =
                db.query(

                        TABLE_USER,

                        null,

                        null,

                        null,

                        null,

                        null,

                        USER_NICKNAME
                                + " COLLATE NOCASE ASC"
                );


        try {


            while (cursor.moveToNext()) {


                int id =
                        cursor.getInt(

                                cursor.getColumnIndexOrThrow(
                                        USER_ID
                                )
                        );


                String nickname =
                        cursor.getString(

                                cursor.getColumnIndexOrThrow(
                                        USER_NICKNAME
                                )
                        );


                users.add(

                        new User(
                                id,
                                nickname
                        )
                );
            }


        } finally {


            cursor.close();
        }


        return users;
    }


    // =========================================================
    // 사용자 ID 조회
    // =========================================================
// =========================================================
// 닉네임으로 사용자 조회
// =========================================================

    public User getUserByNickname(
            String nickname
    ) {

        // 닉네임이 비어 있으면 조회하지 않음
        if (nickname == null ||
                nickname.trim().isEmpty()) {

            return null;
        }


        SQLiteDatabase db =
                getReadableDatabase();


        Cursor cursor =
                db.query(

                        TABLE_USER,

                        null,

                        USER_NICKNAME + " = ?",

                        new String[]{
                                nickname.trim()
                        },

                        null,

                        null,

                        null
                );


        try {

            // 해당 닉네임 사용자가 없을 경우
            if (!cursor.moveToFirst()) {

                return null;
            }


            int id =
                    cursor.getInt(

                            cursor.getColumnIndexOrThrow(
                                    USER_ID
                            )
                    );


            String savedNickname =
                    cursor.getString(

                            cursor.getColumnIndexOrThrow(
                                    USER_NICKNAME
                            )
                    );


            return new User(
                    id,
                    savedNickname
            );


        } finally {

            cursor.close();
        }
    }
    public User getUserById(
            int userId
    ) {



        SQLiteDatabase db =
                getReadableDatabase();


        Cursor cursor =
                db.query(

                        TABLE_USER,

                        null,

                        USER_ID + " = ?",

                        new String[]{
                                String.valueOf(
                                        userId
                                )
                        },

                        null,

                        null,

                        null
                );


        try {


            if (!cursor.moveToFirst()) {

                return null;
            }


            return new User(

                    cursor.getInt(

                            cursor.getColumnIndexOrThrow(
                                    USER_ID
                            )
                    ),

                    cursor.getString(

                            cursor.getColumnIndexOrThrow(
                                    USER_NICKNAME
                            )
                    )
            );


        } finally {


            cursor.close();
        }
    }


    // =========================================================
    // 사용자 닉네임 조회
    // =========================================================

    public String getUserNickname(
            int userId
    ) {


        User user =
                getUserById(
                        userId
                );


        if (user == null) {

            return "알 수 없음";
        }


        return user.getNickname();
    }


    // =========================================================
    // 물품 등록
    // =========================================================

    public boolean insertItem(
            Item item
    ) {


        if (item == null) {

            return false;
        }


        SQLiteDatabase db =
                getWritableDatabase();


        ContentValues values =
                itemToValues(
                        item,
                        false
                );


        long result =
                db.insert(

                        TABLE_ITEM,

                        null,

                        values
                );


        return result != -1;
    }


    // =========================================================
    // 전체 물품 조회
    // =========================================================

    public ArrayList<Item> getAllItems() {


        return queryItems(

                null,

                null,

                ITEM_DATE
                        + " DESC, "
                        + ITEM_ID
                        + " DESC"
        );
    }


    // =========================================================
    // 물품 종류별 조회
    // =========================================================

    public ArrayList<Item> getItemsByType(
            String type
    ) {


        return queryItems(

                ITEM_TYPE + " = ?",

                new String[]{
                        type
                },

                ITEM_DATE
                        + " DESC, "
                        + ITEM_ID
                        + " DESC"
        );
    }


    // =========================================================
    // 사용자별 등록내역
    // =========================================================

    public ArrayList<Item> getItemsByUserId(
            int userId
    ) {


        return queryItems(

                ITEM_USER_ID + " = ?",

                new String[]{
                        String.valueOf(
                                userId
                        )
                },

                ITEM_DATE
                        + " DESC, "
                        + ITEM_ID
                        + " DESC"
        );
    }


    // =========================================================
    // 단순 검색
    // =========================================================

    public ArrayList<Item> searchItems(
            String keyword
    ) {


        return getFilteredItems(

                keyword,

                "ALL",

                "ALL",

                "LATEST"
        );
    }


    // =========================================================
    // 검색 + 타입 + 카테고리 + 정렬
    // =========================================================

    public ArrayList<Item> getFilteredItems(
            String keyword,
            String type,
            String category,
            String sortOrder
    ) {


        SQLiteDatabase db =
                getReadableDatabase();


        StringBuilder where =
                new StringBuilder(
                        "1 = 1"
                );


        ArrayList<String> args =
                new ArrayList<>();


        // =====================================================
        // 검색어
        // =====================================================

        if (keyword != null
                &&
                !keyword.trim().isEmpty()) {


            where.append(

                    " AND ("

                            + ITEM_NAME
                            + " LIKE ?"

                            + " OR "
                            + ITEM_CATEGORY
                            + " LIKE ?"

                            + " OR "
                            + ITEM_LOCATION
                            + " LIKE ?"

                            + " OR "
                            + ITEM_DESCRIPTION
                            + " LIKE ?"

                            + ")"
            );


            String searchText =
                    "%"
                            + keyword.trim()
                            + "%";


            args.add(
                    searchText
            );

            args.add(
                    searchText
            );

            args.add(
                    searchText
            );

            args.add(
                    searchText
            );
        }


        // =====================================================
        // LOST / FOUND
        // =====================================================

        if (type != null
                &&
                !"ALL".equals(
                        type
                )) {


            where.append(
                    " AND "
                            + ITEM_TYPE
                            + " = ?"
            );


            args.add(
                    type
            );
        }


        // =====================================================
        // 카테고리
        // =====================================================

        if (category != null
                &&
                !"ALL".equals(
                        category
                )) {


            where.append(
                    " AND "
                            + ITEM_CATEGORY
                            + " = ?"
            );


            args.add(
                    category
            );
        }


        // =====================================================
        // 정렬
        // =====================================================

        String orderBy;


        if ("OLDEST".equals(
                sortOrder
        )) {


            orderBy =
                    ITEM_DATE
                            + " ASC, "
                            + ITEM_ID
                            + " ASC";


        } else if ("NAME".equals(
                sortOrder
        )) {


            orderBy =
                    ITEM_NAME
                            + " COLLATE NOCASE ASC";


        } else {


            orderBy =
                    ITEM_DATE
                            + " DESC, "
                            + ITEM_ID
                            + " DESC";
        }


        Cursor cursor =
                db.query(

                        TABLE_ITEM,

                        null,

                        where.toString(),

                        args.toArray(
                                new String[0]
                        ),

                        null,

                        null,

                        orderBy
                );


        ArrayList<Item> result =
                new ArrayList<>();


        try {


            while (cursor.moveToNext()) {


                result.add(
                        cursorToItem(
                                cursor
                        )
                );
            }


        } finally {


            cursor.close();
        }


        return result;
    }


    // =========================================================
    // ID로 물품 조회
    // =========================================================

    public Item getItemById(
            int id
    ) {


        SQLiteDatabase db =
                getReadableDatabase();


        Cursor cursor =
                db.query(

                        TABLE_ITEM,

                        null,

                        ITEM_ID + " = ?",

                        new String[]{
                                String.valueOf(
                                        id
                                )
                        },

                        null,

                        null,

                        null
                );


        try {


            if (!cursor.moveToFirst()) {

                return null;
            }


            return cursorToItem(
                    cursor
            );


        } finally {


            cursor.close();
        }
    }


    // =========================================================
    // getItem 별칭
    // =========================================================

    public Item getItem(
            int id
    ) {

        return getItemById(
                id
        );
    }


    // =========================================================
    // 물품 수정
    // =========================================================

    public boolean updateItem(
            Item item
    ) {


        if (item == null) {

            return false;
        }


        SQLiteDatabase db =
                getWritableDatabase();


        ContentValues values =
                itemToValues(
                        item,
                        false
                );


        int result =
                db.update(

                        TABLE_ITEM,

                        values,

                        ITEM_ID + " = ?",

                        new String[]{
                                String.valueOf(
                                        item.getId()
                                )
                        }
                );


        return result > 0;
    }


    // =========================================================
    // 물품 삭제
    // =========================================================

    public boolean deleteItem(
            int itemId
    ) {


        SQLiteDatabase db =
                getWritableDatabase();


        db.beginTransaction();


        try {


            // =================================================
            // 삭제 전 물품 조회
            // =================================================

            Item item =
                    getItemFromDatabase(

                            db,

                            itemId
                    );


            if (item == null) {

                return false;
            }


            int matchedItemId =
                    item.getMatchedItemId();


            // =================================================
            // 연결된 상대방이 있으면 연결 해제
            // =================================================

            if (matchedItemId != -1) {


                ContentValues matchedValues =
                        new ContentValues();


                matchedValues.put(
                        ITEM_STATUS,
                        "SEARCHING"
                );


                matchedValues.putNull(
                        ITEM_MATCHED_ID
                );


                matchedValues.putNull(
                        ITEM_HANDOFF_NOTE
                );


                db.update(

                        TABLE_ITEM,

                        matchedValues,

                        ITEM_ID + " = ?",

                        new String[]{
                                String.valueOf(
                                        matchedItemId
                                )
                        }
                );
            }


            // =================================================
            // 현재 물품 삭제
            // =================================================

            int deletedRows =
                    db.delete(

                            TABLE_ITEM,

                            ITEM_ID + " = ?",

                            new String[]{
                                    String.valueOf(
                                            itemId
                                    )
                            }
                    );


            if (deletedRows <= 0) {

                return false;
            }


            db.setTransactionSuccessful();


            return true;


        } finally {


            db.endTransaction();
        }
    }


    // =========================================================
    // 연결 가능한 습득물 조회
    // =========================================================

    public ArrayList<Item> getAvailableFoundItems(
            int lostItemId
    ) {


        /*
         * 연결되지 않은 FOUND만 조회
         */
        return queryItems(

                ITEM_TYPE
                        + " = ?"
                        + " AND "
                        + ITEM_STATUS
                        + " = ?"
                        + " AND "
                        + ITEM_MATCHED_ID
                        + " IS NULL",

                new String[]{

                        "FOUND",

                        "SEARCHING"
                },

                ITEM_DATE
                        + " DESC, "
                        + ITEM_ID
                        + " DESC"
        );
    }


    // =========================================================
    // LOST ↔ FOUND 연결
    // =========================================================

    public boolean linkItems(
            int lostItemId,
            int foundItemId
    ) {


        if (lostItemId == foundItemId) {

            return false;
        }


        SQLiteDatabase db =
                getWritableDatabase();


        db.beginTransaction();


        try {


            Item lostItem =
                    getItemFromDatabase(

                            db,

                            lostItemId
                    );


            Item foundItem =
                    getItemFromDatabase(

                            db,

                            foundItemId
                    );


            // =================================================
            // 존재 여부
            // =================================================

            if (lostItem == null
                    ||
                    foundItem == null) {


                return false;
            }


            // =================================================
            // LOST / FOUND 확인
            // =================================================

            if (!"LOST".equals(
                    lostItem.getType()
            )) {


                return false;
            }


            if (!"FOUND".equals(
                    foundItem.getType()
            )) {


                return false;
            }


            // =================================================
            // 같은 사용자가 등록한 물품은 연결하지 않음
            // =================================================

            if (lostItem.getUserId()
                    ==
                    foundItem.getUserId()) {


                return false;
            }


            // =================================================
            // 이미 연결된 물품인지 확인
            // =================================================

            if (lostItem.getMatchedItemId()
                    != -1) {


                return false;
            }


            if (foundItem.getMatchedItemId()
                    != -1) {


                return false;
            }


            // =================================================
            // LOST 업데이트
            // =================================================

            ContentValues lostValues =
                    new ContentValues();


            lostValues.put(
                    ITEM_STATUS,
                    "MATCHED"
            );


            lostValues.put(
                    ITEM_MATCHED_ID,
                    foundItemId
            );


            int lostResult =
                    db.update(

                            TABLE_ITEM,

                            lostValues,

                            ITEM_ID + " = ?",

                            new String[]{
                                    String.valueOf(
                                            lostItemId
                                    )
                            }
                    );


            // =================================================
            // FOUND 업데이트
            // =================================================

            ContentValues foundValues =
                    new ContentValues();


            foundValues.put(
                    ITEM_STATUS,
                    "MATCHED"
            );


            foundValues.put(
                    ITEM_MATCHED_ID,
                    lostItemId
            );


            int foundResult =
                    db.update(

                            TABLE_ITEM,

                            foundValues,

                            ITEM_ID + " = ?",

                            new String[]{
                                    String.valueOf(
                                            foundItemId
                                    )
                            }
                    );


            if (lostResult <= 0
                    ||
                    foundResult <= 0) {


                return false;
            }


            db.setTransactionSuccessful();


            return true;


        } finally {


            db.endTransaction();
        }
    }


    // =========================================================
    // 전달 메모 저장
    // =========================================================

    public boolean saveHandoffNote(
            int itemId,
            String note
    ) {


        SQLiteDatabase db =
                getWritableDatabase();


        Item item =
                getItemFromDatabase(

                        db,

                        itemId
                );


        if (item == null) {

            return false;
        }


        /*
         * 전달 메모는 FOUND에만 저장
         */
        if (!"FOUND".equals(
                item.getType()
        )) {


            return false;
        }


        ContentValues values =
                new ContentValues();


        values.put(
                ITEM_HANDOFF_NOTE,
                note
        );


        int result =
                db.update(

                        TABLE_ITEM,

                        values,

                        ITEM_ID + " = ?",

                        new String[]{
                                String.valueOf(
                                        itemId
                                )
                        }
                );


        return result > 0;
    }


    // =========================================================
    // 수령 완료
    // =========================================================

    public boolean finishAndDeleteMatchedItems(
            int lostItemId
    ) {


        SQLiteDatabase db =
                getWritableDatabase();


        db.beginTransaction();


        try {


            Item lostItem =
                    getItemFromDatabase(

                            db,

                            lostItemId
                    );


            // =================================================
            // LOST 존재 확인
            // =================================================

            if (lostItem == null) {

                return false;
            }


            if (!"LOST".equals(
                    lostItem.getType()
            )) {


                return false;
            }


            int foundItemId =
                    lostItem.getMatchedItemId();


            // =================================================
            // 연결 물품 존재 여부
            // =================================================

            if (foundItemId == -1) {

                return false;
            }


            Item foundItem =
                    getItemFromDatabase(

                            db,

                            foundItemId
                    );


            if (foundItem == null) {

                return false;
            }


            // =================================================
            // LOST 삭제
            // =================================================

            int lostDelete =
                    db.delete(

                            TABLE_ITEM,

                            ITEM_ID + " = ?",

                            new String[]{
                                    String.valueOf(
                                            lostItemId
                                    )
                            }
                    );


            // =================================================
            // FOUND 삭제
            // =================================================

            int foundDelete =
                    db.delete(

                            TABLE_ITEM,

                            ITEM_ID + " = ?",

                            new String[]{
                                    String.valueOf(
                                            foundItemId
                                    )
                            }
                    );


            if (lostDelete <= 0
                    ||
                    foundDelete <= 0) {


                return false;
            }


            db.setTransactionSuccessful();


            return true;


        } finally {


            db.endTransaction();
        }
    }


    // =========================================================
    // 삭제 실행 취소
    // =========================================================

    public boolean restoreDeletedItem(
            Item deletedItem,
            Item matchedItemBeforeDelete
    ) {


        if (deletedItem == null) {

            return false;
        }


        SQLiteDatabase db =
                getWritableDatabase();


        db.beginTransaction();


        try {


            // =================================================
            // 이미 같은 ID가 존재하면 복원 실패
            // =================================================

            Item existing =
                    getItemFromDatabase(

                            db,

                            deletedItem.getId()
                    );


            if (existing != null) {

                return false;
            }


            // =================================================
            // 삭제된 물품 원래 ID 그대로 복원
            // =================================================

            ContentValues values =
                    itemToValues(

                            deletedItem,

                            true
                    );


            long insertResult =
                    db.insert(

                            TABLE_ITEM,

                            null,

                            values
                    );


            if (insertResult == -1) {

                return false;
            }


            // =================================================
            // 연결 상대의 기존 상태 복원
            // =================================================

            if (matchedItemBeforeDelete != null) {


                ContentValues matchedValues =
                        new ContentValues();


                matchedValues.put(
                        ITEM_STATUS,
                        matchedItemBeforeDelete
                                .getStatus()
                );


                if (matchedItemBeforeDelete
                        .getMatchedItemId()
                        == -1) {


                    matchedValues.putNull(
                            ITEM_MATCHED_ID
                    );


                } else {


                    matchedValues.put(

                            ITEM_MATCHED_ID,

                            matchedItemBeforeDelete
                                    .getMatchedItemId()
                    );
                }


                if (matchedItemBeforeDelete
                        .getHandoffNote()
                        == null) {


                    matchedValues.putNull(
                            ITEM_HANDOFF_NOTE
                    );


                } else {


                    matchedValues.put(

                            ITEM_HANDOFF_NOTE,

                            matchedItemBeforeDelete
                                    .getHandoffNote()
                    );
                }


                int updateResult =
                        db.update(

                                TABLE_ITEM,

                                matchedValues,

                                ITEM_ID + " = ?",

                                new String[]{
                                        String.valueOf(

                                                matchedItemBeforeDelete
                                                        .getId()
                                        )
                                }
                        );


                if (updateResult <= 0) {

                    return false;
                }
            }


            db.setTransactionSuccessful();


            return true;


        } finally {


            db.endTransaction();
        }
    }


    // =========================================================
    // Item → ContentValues
    // =========================================================

    private ContentValues itemToValues(
            Item item,
            boolean includeId
    ) {


        ContentValues values =
                new ContentValues();


        // =====================================================
        // 삭제 복원에서는 기존 ID까지 넣음
        // =====================================================

        if (includeId) {


            values.put(
                    ITEM_ID,
                    item.getId()
            );
        }


        values.put(
                ITEM_TYPE,
                item.getType()
        );


        values.put(
                ITEM_NAME,
                item.getName()
        );


        values.put(
                ITEM_CATEGORY,
                item.getCategory()
        );


        values.put(
                ITEM_COLOR,
                item.getColor()
        );


        values.put(
                ITEM_LOCATION,
                item.getLocation()
        );


        values.put(
                ITEM_DATE,
                item.getDate()
        );


        values.put(
                ITEM_DESCRIPTION,
                item.getDescription()
        );


        // =====================================================
        // 상태
        // =====================================================

        String status =
                item.getStatus();


        if (status == null
                ||
                status.trim().isEmpty()) {


            status =
                    "SEARCHING";
        }


        values.put(
                ITEM_STATUS,
                status
        );


        // =====================================================
        // 이미지 URI
        // =====================================================

        if (item.getImageUri() == null) {


            values.putNull(
                    ITEM_IMAGE_URI
            );


        } else {


            values.put(
                    ITEM_IMAGE_URI,
                    item.getImageUri()
            );
        }


        // =====================================================
        // 사용자 ID
        // =====================================================

        if (item.getUserId() == -1) {


            values.putNull(
                    ITEM_USER_ID
            );


        } else {


            values.put(
                    ITEM_USER_ID,
                    item.getUserId()
            );
        }


        // =====================================================
        // 연결된 물품 ID
        // =====================================================

        if (item.getMatchedItemId() == -1) {


            values.putNull(
                    ITEM_MATCHED_ID
            );


        } else {


            values.put(
                    ITEM_MATCHED_ID,
                    item.getMatchedItemId()
            );
        }


        // =====================================================
        // 전달 메모
        // =====================================================

        if (item.getHandoffNote() == null) {


            values.putNull(
                    ITEM_HANDOFF_NOTE
            );


        } else {


            values.put(
                    ITEM_HANDOFF_NOTE,
                    item.getHandoffNote()
            );
        }


        return values;
    }


    // =========================================================
    // 공통 Item 조회
    // =========================================================

    private ArrayList<Item> queryItems(
            String selection,
            String[] selectionArgs,
            String orderBy
    ) {


        ArrayList<Item> items =
                new ArrayList<>();


        SQLiteDatabase db =
                getReadableDatabase();


        Cursor cursor =
                db.query(

                        TABLE_ITEM,

                        null,

                        selection,

                        selectionArgs,

                        null,

                        null,

                        orderBy
                );


        try {


            while (cursor.moveToNext()) {


                items.add(
                        cursorToItem(
                                cursor
                        )
                );
            }


        } finally {


            cursor.close();
        }


        return items;
    }


    // =========================================================
    // 같은 DB Transaction 안에서 Item 조회
    // =========================================================

    private Item getItemFromDatabase(
            SQLiteDatabase db,
            int itemId
    ) {


        Cursor cursor =
                db.query(

                        TABLE_ITEM,

                        null,

                        ITEM_ID + " = ?",

                        new String[]{
                                String.valueOf(
                                        itemId
                                )
                        },

                        null,

                        null,

                        null
                );


        try {


            if (!cursor.moveToFirst()) {

                return null;
            }


            return cursorToItem(
                    cursor
            );


        } finally {


            cursor.close();
        }
    }


    // =========================================================
    // Cursor → Item
    // =========================================================

    private Item cursorToItem(
            Cursor cursor
    ) {


        int id =
                cursor.getInt(

                        cursor.getColumnIndexOrThrow(
                                ITEM_ID
                        )
                );


        String type =
                cursor.getString(

                        cursor.getColumnIndexOrThrow(
                                ITEM_TYPE
                        )
                );


        String name =
                cursor.getString(

                        cursor.getColumnIndexOrThrow(
                                ITEM_NAME
                        )
                );


        String category =
                cursor.getString(

                        cursor.getColumnIndexOrThrow(
                                ITEM_CATEGORY
                        )
                );


        String color =
                cursor.getString(

                        cursor.getColumnIndexOrThrow(
                                ITEM_COLOR
                        )
                );


        String location =
                cursor.getString(

                        cursor.getColumnIndexOrThrow(
                                ITEM_LOCATION
                        )
                );


        String date =
                cursor.getString(

                        cursor.getColumnIndexOrThrow(
                                ITEM_DATE
                        )
                );


        String description =
                cursor.getString(

                        cursor.getColumnIndexOrThrow(
                                ITEM_DESCRIPTION
                        )
                );


        String status =
                cursor.getString(

                        cursor.getColumnIndexOrThrow(
                                ITEM_STATUS
                        )
                );


        String imageUri =
                cursor.getString(

                        cursor.getColumnIndexOrThrow(
                                ITEM_IMAGE_URI
                        )
                );


        // =====================================================
        // user_id
        // =====================================================

        int userIdColumn =
                cursor.getColumnIndexOrThrow(
                        ITEM_USER_ID
                );


        int userId =

                cursor.isNull(
                        userIdColumn
                )

                        ? -1

                        : cursor.getInt(
                        userIdColumn
                );


        // =====================================================
        // matched_item_id
        // =====================================================

        int matchedColumn =
                cursor.getColumnIndexOrThrow(
                        ITEM_MATCHED_ID
                );


        int matchedItemId =

                cursor.isNull(
                        matchedColumn
                )

                        ? -1

                        : cursor.getInt(
                        matchedColumn
                );


        // =====================================================
        // handoff_note
        // =====================================================

        String handoffNote =
                cursor.getString(

                        cursor.getColumnIndexOrThrow(
                                ITEM_HANDOFF_NOTE
                        )
                );


        /*
         * 현재 프로젝트의 Item 전체 필드
         *
         * id
         * type
         * name
         * category
         * color
         * location
         * date
         * description
         * status
         * imageUri
         * userId
         * matchedItemId
         * handoffNote
         */
        return new Item(

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

                handoffNote
        );
    }


    // =========================================================
    // 테이블 존재 여부
    // =========================================================

    private boolean tableExists(
            SQLiteDatabase db,
            String tableName
    ) {


        Cursor cursor =
                db.rawQuery(

                        "SELECT name "
                                +
                                "FROM sqlite_master "
                                +
                                "WHERE type='table' "
                                +
                                "AND name=?",

                        new String[]{
                                tableName
                        }
                );


        try {


            return cursor.moveToFirst();


        } finally {


            cursor.close();
        }
    }


    // =========================================================
    // 컬럼 존재 여부
    // =========================================================

    private boolean columnExists(
            SQLiteDatabase db,
            String tableName,
            String columnName
    ) {


        Cursor cursor =
                db.rawQuery(

                        "PRAGMA table_info("
                                + tableName
                                + ")",

                        null
                );


        try {


            int nameIndex =
                    cursor.getColumnIndex(
                            "name"
                    );


            while (cursor.moveToNext()) {


                String name =
                        cursor.getString(
                                nameIndex
                        );


                if (columnName.equals(
                        name
                )) {


                    return true;
                }
            }


            return false;


        } finally {


            cursor.close();
        }
    }


    // =========================================================
    // 없는 컬럼만 추가
    // =========================================================

    private void addColumnIfMissing(
            SQLiteDatabase db,
            String tableName,
            String columnName,
            String definition
    ) {


        if (columnExists(

                db,

                tableName,

                columnName
        )) {


            return;
        }


        db.execSQL(

                "ALTER TABLE "
                        + tableName
                        + " ADD COLUMN "
                        + columnName
                        + " "
                        + definition
        );
    }
}