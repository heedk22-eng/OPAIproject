package com.iot.lostfoundapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


/*
 * SQLite 실제 처리 클래스
 */
public class DatabaseHelper
        extends SQLiteOpenHelper
        implements ItemRepository {

    // SQLite DB 파일명
    private static final String DB_NAME =
            "lostfound.db";

    /*
     * 최종 DB 버전
     *
     * 개발 중 구조가 여러 번 변경되었으므로 5 사용
     */
    private static final int DB_VERSION = 5;

    // 물품 테이블
    private static final String TABLE_ITEM =
            "item";

    // 사용자 테이블
    private static final String TABLE_USER =
            "app_user";


    public DatabaseHelper(Context context) {

        super(
                context,
                DB_NAME,
                null,
                DB_VERSION
        );
    }


    /*
     * DB가 처음 생성될 때 실행
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        /*
         * 사용자 테이블
         *
         * UNIQUE + COLLATE NOCASE
         * → 같은 기기에서 닉네임 중복 방지
         * → abc와 ABC도 같은 닉네임 취급
         */
        String userSql =
                "CREATE TABLE " + TABLE_USER + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nickname TEXT NOT NULL COLLATE NOCASE UNIQUE" +
                        ")";

        db.execSQL(userSql);


        /*
         * 물품 테이블
         */
        String itemSql =
                "CREATE TABLE " + TABLE_ITEM + " (" +

                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        "type TEXT NOT NULL, " +

                        "name TEXT NOT NULL, " +

                        "category TEXT NOT NULL, " +

                        "color TEXT, " +

                        "location TEXT NOT NULL, " +

                        "date TEXT NOT NULL, " +

                        "description TEXT, " +

                        "status TEXT NOT NULL DEFAULT 'SEARCHING', " +

                        "image_uri TEXT, " +

                        "user_id INTEGER, " +

                        "matched_item_id INTEGER, " +

                        "handoff_note TEXT" +

                        ")";

        db.execSQL(itemSql);
    }


    /*
     * 기존 DB 버전 업그레이드
     */
    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        /*
         * 1 → 2
         */
        if (oldVersion < 2) {

            db.execSQL(
                    "ALTER TABLE " + TABLE_ITEM +
                            " ADD COLUMN status TEXT NOT NULL DEFAULT 'SEARCHING'"
            );

            db.execSQL(
                    "ALTER TABLE " + TABLE_ITEM +
                            " ADD COLUMN image_uri TEXT"
            );
        }


        /*
         * 2 → 3
         */
        if (oldVersion < 3) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS " +
                            TABLE_USER + " (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "nickname TEXT NOT NULL COLLATE NOCASE UNIQUE" +
                            ")"
            );

            db.execSQL(
                    "ALTER TABLE " + TABLE_ITEM +
                            " ADD COLUMN user_id INTEGER"
            );
        }


        /*
         * 3 → 4
         */
        if (oldVersion < 4) {

            db.execSQL(
                    "ALTER TABLE " + TABLE_ITEM +
                            " ADD COLUMN matched_item_id INTEGER"
            );
        }


        /*
         * 4 → 5
         */
        if (oldVersion < 5) {

            db.execSQL(
                    "ALTER TABLE " + TABLE_ITEM +
                            " ADD COLUMN handoff_note TEXT"
            );
        }
    }


    // =========================================================
    // 사용자
    // =========================================================

    /*
     * 새 사용자 생성
     */
    public long createUser(String nickname) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "nickname",
                nickname
        );

        return db.insert(
                TABLE_USER,
                null,
                values
        );
    }


    /*
     * 닉네임으로 사용자 조회
     */
    public User getUserByNickname(
            String nickname
    ) {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM " +
                                TABLE_USER +
                                " WHERE nickname = ? COLLATE NOCASE",

                        new String[]{
                                nickname
                        }
                );


        User user = null;


        if (cursor.moveToFirst()) {

            user =
                    new User(
                            cursor.getInt(
                                    cursor.getColumnIndexOrThrow("id")
                            ),

                            cursor.getString(
                                    cursor.getColumnIndexOrThrow("nickname")
                            )
                    );
        }


        cursor.close();

        return user;
    }


    /*
     * 모든 사용자 조회
     */
    public ArrayList<User> getAllUsers() {

        ArrayList<User> users =
                new ArrayList<>();

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM " +
                                TABLE_USER +
                                " ORDER BY nickname ASC",
                        null
                );


        while (cursor.moveToNext()) {

            users.add(
                    new User(
                            cursor.getInt(
                                    cursor.getColumnIndexOrThrow("id")
                            ),

                            cursor.getString(
                                    cursor.getColumnIndexOrThrow("nickname")
                            )
                    )
            );
        }


        cursor.close();

        return users;
    }


    /*
     * 사용자 ID → 닉네임
     */
    public String getNicknameByUserId(
            int userId
    ) {

        if (userId == -1) {
            return "알 수 없음";
        }


        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT nickname FROM " +
                                TABLE_USER +
                                " WHERE id = ?",

                        new String[]{
                                String.valueOf(userId)
                        }
                );


        String nickname =
                "알 수 없음";


        if (cursor.moveToFirst()) {

            nickname =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("nickname")
                    );
        }


        cursor.close();

        return nickname;
    }


    // =========================================================
    // 등록
    // =========================================================

    @Override
    public long insertItem(Item item) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();


        values.put(
                "type",
                item.getType()
        );

        values.put(
                "name",
                item.getName()
        );

        values.put(
                "category",
                item.getCategory()
        );

        values.put(
                "color",
                item.getColor()
        );

        values.put(
                "location",
                item.getLocation()
        );

        values.put(
                "date",
                item.getDate()
        );

        values.put(
                "description",
                item.getDescription()
        );

        values.put(
                "status",
                item.getStatus()
        );

        values.put(
                "image_uri",
                item.getImageUri()
        );

        values.put(
                "user_id",
                item.getUserId()
        );

        values.put(
                "handoff_note",
                item.getHandoffNote()
        );


        if (item.getMatchedItemId() == -1) {

            values.putNull(
                    "matched_item_id"
            );

        } else {

            values.put(
                    "matched_item_id",
                    item.getMatchedItemId()
            );
        }


        return db.insert(
                TABLE_ITEM,
                null,
                values
        );
    }


    // =========================================================
    // 조회
    // =========================================================

    @Override
    public ArrayList<Item> getAllItems() {

        return getFilteredItems(
                "",
                "ALL",
                "ALL",
                "LATEST"
        );
    }


    @Override
    public ArrayList<Item> getItemsByType(
            String type
    ) {

        return getFilteredItems(
                "",
                type,
                "ALL",
                "LATEST"
        );
    }


    @Override
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


    /*
     * 검색 + 분실/습득 + 카테고리 + 정렬
     */
    @Override
    public ArrayList<Item> getFilteredItems(
            String keyword,
            String type,
            String category,
            String sortOrder
    ) {

        ArrayList<Item> itemList =
                new ArrayList<>();

        SQLiteDatabase db =
                getReadableDatabase();

        StringBuilder sql =
                new StringBuilder(
                        "SELECT * FROM " +
                                TABLE_ITEM +
                                " WHERE 1=1"
                );

        ArrayList<String> args =
                new ArrayList<>();


        /*
         * 검색
         *
         * 물품명 / 카테고리 / 장소 / 특징
         */
        if (keyword != null &&
                !keyword.trim().isEmpty()) {

            sql.append(
                    " AND (" +
                            "name LIKE ? " +
                            "OR category LIKE ? " +
                            "OR location LIKE ? " +
                            "OR description LIKE ?" +
                            ")"
            );


            String searchKeyword =
                    "%" + keyword.trim() + "%";

            args.add(searchKeyword);
            args.add(searchKeyword);
            args.add(searchKeyword);
            args.add(searchKeyword);
        }


        /*
         * 분실 / 습득
         */
        if (type != null &&
                !"ALL".equals(type)) {

            sql.append(
                    " AND type = ?"
            );

            args.add(type);
        }


        /*
         * 카테고리
         */
        if (category != null &&
                !"ALL".equals(category)) {

            sql.append(
                    " AND category = ?"
            );

            args.add(category);
        }


        /*
         * 정렬
         */
        if ("OLDEST".equals(sortOrder)) {

            sql.append(
                    " ORDER BY date ASC, id ASC"
            );

        } else if ("NAME".equals(sortOrder)) {

            sql.append(
                    " ORDER BY name COLLATE NOCASE ASC"
            );

        } else {

            sql.append(
                    " ORDER BY date DESC, id DESC"
            );
        }


        Cursor cursor =
                db.rawQuery(
                        sql.toString(),
                        args.toArray(
                                new String[0]
                        )
                );


        while (cursor.moveToNext()) {

            itemList.add(
                    cursorToItem(cursor)
            );
        }


        cursor.close();

        return itemList;
    }


    /*
     * ID로 한 개 조회
     */
    @Override
    public Item getItemById(int id) {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM " +
                                TABLE_ITEM +
                                " WHERE id = ?",

                        new String[]{
                                String.valueOf(id)
                        }
                );


        Item item = null;


        if (cursor.moveToFirst()) {

            item =
                    cursorToItem(cursor);
        }


        cursor.close();

        return item;
    }


    // =========================================================
    // 수정
    // =========================================================

    @Override
    public int updateItem(Item item) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();


        values.put(
                "type",
                item.getType()
        );

        values.put(
                "name",
                item.getName()
        );

        values.put(
                "category",
                item.getCategory()
        );

        values.put(
                "color",
                item.getColor()
        );

        values.put(
                "location",
                item.getLocation()
        );

        values.put(
                "date",
                item.getDate()
        );

        values.put(
                "description",
                item.getDescription()
        );

        values.put(
                "status",
                item.getStatus()
        );

        values.put(
                "image_uri",
                item.getImageUri()
        );

        values.put(
                "user_id",
                item.getUserId()
        );

        values.put(
                "handoff_note",
                item.getHandoffNote()
        );


        if (item.getMatchedItemId() == -1) {

            values.putNull(
                    "matched_item_id"
            );

        } else {

            values.put(
                    "matched_item_id",
                    item.getMatchedItemId()
            );
        }


        return db.update(
                TABLE_ITEM,
                values,
                "id = ?",
                new String[]{
                        String.valueOf(
                                item.getId()
                        )
                }
        );
    }


    // =========================================================
    // 일반 삭제
    // =========================================================

    /*
     * 물품을 일반 삭제하면
     * 연결된 상대 물품의 연결도 자동 해제한다.
     */
    @Override
    public int deleteItem(int id) {

        Item item =
                getItemById(id);


        if (item == null) {
            return 0;
        }


        SQLiteDatabase db =
                getWritableDatabase();

        db.beginTransaction();


        try {

            /*
             * 연결된 상대가 있다면
             * 상대의 연결 상태를 원래대로 되돌린다.
             */
            if (item.getMatchedItemId()
                    != -1) {

                ContentValues unlinkValues =
                        new ContentValues();

                unlinkValues.putNull(
                        "matched_item_id"
                );

                unlinkValues.put(
                        "status",
                        "SEARCHING"
                );

                /*
                 * 기존 전달 메모도 제거
                 */
                unlinkValues.put(
                        "handoff_note",
                        ""
                );


                db.update(
                        TABLE_ITEM,
                        unlinkValues,
                        "id = ?",
                        new String[]{
                                String.valueOf(
                                        item.getMatchedItemId()
                                )
                        }
                );
            }


            int result =
                    db.delete(
                            TABLE_ITEM,
                            "id = ?",
                            new String[]{
                                    String.valueOf(id)
                            }
                    );


            if (result > 0) {

                db.setTransactionSuccessful();
            }


            return result;


        } finally {

            db.endTransaction();
        }
    }


    // =========================================================
    // 연결 가능한 습득물
    // =========================================================

    @Override
    public ArrayList<Item> getAvailableFoundItems(
            int lostItemId
    ) {

        ArrayList<Item> list =
                new ArrayList<>();

        SQLiteDatabase db =
                getReadableDatabase();


        Cursor cursor =
                db.rawQuery(

                        "SELECT * FROM " +
                                TABLE_ITEM +

                                " WHERE type = 'FOUND'" +

                                " AND matched_item_id IS NULL" +

                                " AND id != ?" +

                                " ORDER BY date DESC, id DESC",

                        new String[]{
                                String.valueOf(
                                        lostItemId
                                )
                        }
                );


        while (cursor.moveToNext()) {

            list.add(
                    cursorToItem(cursor)
            );
        }


        cursor.close();

        return list;
    }


    // =========================================================
    // LOST ↔ FOUND 연결
    // =========================================================

    @Override
    public boolean linkItems(
            int lostItemId,
            int foundItemId
    ) {

        Item lostItem =
                getItemById(
                        lostItemId
                );

        Item foundItem =
                getItemById(
                        foundItemId
                );


        if (lostItem == null ||
                foundItem == null) {

            return false;
        }


        /*
         * LOST와 FOUND 관계만 가능
         */
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


        /*
         * 이미 연결된 물품은 연결 불가
         */
        if (lostItem.getMatchedItemId()
                != -1) {

            return false;
        }


        if (foundItem.getMatchedItemId()
                != -1) {

            return false;
        }


        /*
         * 같은 사용자가 등록한 분실/습득물은 연결하지 않음
         */
        if (lostItem.getUserId()
                == foundItem.getUserId()) {

            return false;
        }


        SQLiteDatabase db =
                getWritableDatabase();

        db.beginTransaction();


        try {

            /*
             * LOST 쪽
             */
            ContentValues lostValues =
                    new ContentValues();

            lostValues.put(
                    "matched_item_id",
                    foundItemId
            );

            lostValues.put(
                    "status",
                    "MATCHED"
            );


            int lostResult =
                    db.update(
                            TABLE_ITEM,
                            lostValues,
                            "id = ?",
                            new String[]{
                                    String.valueOf(
                                            lostItemId
                                    )
                            }
                    );


            /*
             * FOUND 쪽
             */
            ContentValues foundValues =
                    new ContentValues();

            foundValues.put(
                    "matched_item_id",
                    lostItemId
            );

            foundValues.put(
                    "status",
                    "MATCHED"
            );


            int foundResult =
                    db.update(
                            TABLE_ITEM,
                            foundValues,
                            "id = ?",
                            new String[]{
                                    String.valueOf(
                                            foundItemId
                                    )
                            }
                    );


            if (lostResult > 0 &&
                    foundResult > 0) {

                db.setTransactionSuccessful();

                return true;
            }


        } finally {

            db.endTransaction();
        }


        return false;
    }


    // =========================================================
    // 전달 메모
    // =========================================================

    @Override
    public boolean saveHandoffNote(
            int foundItemId,
            String note
    ) {

        Item foundItem =
                getItemById(
                        foundItemId
                );


        if (foundItem == null) {

            return false;
        }


        /*
         * FOUND에만 메모 작성 가능
         */
        if (!"FOUND".equals(
                foundItem.getType()
        )) {

            return false;
        }


        /*
         * 연결된 상태에서만 작성 가능
         */
        if (foundItem.getMatchedItemId()
                == -1) {

            return false;
        }


        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                "handoff_note",
                note
        );


        int result =
                db.update(
                        TABLE_ITEM,
                        values,
                        "id = ?",
                        new String[]{
                                String.valueOf(
                                        foundItemId
                                )
                        }
                );


        return result > 0;
    }
    /*
     * 특정 사용자가 등록한 물품만 조회
     */
    @Override
    public ArrayList<Item> getItemsByUserId(
            int userId
    ) {

        ArrayList<Item> itemList =
                new ArrayList<>();


        SQLiteDatabase db =
                getReadableDatabase();


        Cursor cursor =
                db.rawQuery(
                        "SELECT * FROM " +
                                TABLE_ITEM +
                                " WHERE user_id = ?" +
                                " ORDER BY date DESC, id DESC",

                        new String[]{
                                String.valueOf(userId)
                        }
                );


        while (cursor.moveToNext()) {

            itemList.add(
                    cursorToItem(cursor)
            );
        }


        cursor.close();


        return itemList;
    }

    // =========================================================
    // 물품 수령 완료
    // =========================================================

    /*
     * 분실자가 실제 물건을 받았으면
     * 연결된 LOST와 FOUND 모두 삭제
     */
    @Override
    public boolean finishAndDeleteMatchedItems(
            int lostItemId
    ) {

        Item lostItem =
                getItemById(
                        lostItemId
                );


        if (lostItem == null) {

            return false;
        }


        if (!"LOST".equals(
                lostItem.getType()
        )) {

            return false;
        }


        if (lostItem.getMatchedItemId()
                == -1) {

            return false;
        }


        int foundItemId =
                lostItem.getMatchedItemId();


        Item foundItem =
                getItemById(
                        foundItemId
                );


        if (foundItem == null) {

            return false;
        }


        SQLiteDatabase db =
                getWritableDatabase();

        db.beginTransaction();


        try {

            int lostResult =
                    db.delete(
                            TABLE_ITEM,
                            "id = ?",
                            new String[]{
                                    String.valueOf(
                                            lostItemId
                                    )
                            }
                    );


            int foundResult =
                    db.delete(
                            TABLE_ITEM,
                            "id = ?",
                            new String[]{
                                    String.valueOf(
                                            foundItemId
                                    )
                            }
                    );


            if (lostResult > 0 &&
                    foundResult > 0) {

                db.setTransactionSuccessful();

                return true;
            }


        } finally {

            db.endTransaction();
        }


        return false;
    }


    // =========================================================
    // Cursor → Item
    // =========================================================

    private Item cursorToItem(
            Cursor cursor
    ) {

        Item item =
                new Item();


        item.setId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                )
        );


        item.setType(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("type")
                )
        );


        item.setName(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                )
        );


        item.setCategory(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("category")
                )
        );


        item.setColor(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("color")
                )
        );


        item.setLocation(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("location")
                )
        );


        item.setDate(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("date")
                )
        );


        item.setDescription(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("description")
                )
        );


        item.setStatus(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("status")
                )
        );


        item.setImageUri(
                cursor.getString(
                        cursor.getColumnIndexOrThrow("image_uri")
                )
        );


        /*
         * user_id
         */
        int userIndex =
                cursor.getColumnIndex(
                        "user_id"
                );


        if (userIndex != -1 &&
                !cursor.isNull(userIndex)) {

            item.setUserId(
                    cursor.getInt(
                            userIndex
                    )
            );

        } else {

            item.setUserId(
                    -1
            );
        }


        /*
         * matched_item_id
         */
        int matchedIndex =
                cursor.getColumnIndex(
                        "matched_item_id"
                );


        if (matchedIndex != -1 &&
                !cursor.isNull(matchedIndex)) {

            item.setMatchedItemId(
                    cursor.getInt(
                            matchedIndex
                    )
            );

        } else {

            item.setMatchedItemId(
                    -1
            );
        }


        /*
         * 전달 메모
         */
        int noteIndex =
                cursor.getColumnIndex(
                        "handoff_note"
                );


        if (noteIndex != -1 &&
                !cursor.isNull(noteIndex)) {

            item.setHandoffNote(
                    cursor.getString(
                            noteIndex
                    )
            );

        } else {

            item.setHandoffNote("");
        }


        return item;
    }
}