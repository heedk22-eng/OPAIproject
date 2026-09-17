package com.iot.lostfoundapp;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


/*
 * DatabaseHelper
 *
 * SQLite 데이터베이스를 실제로 관리하는 클래스
 *
 * SQLiteOpenHelper 상속
 * ItemRepository 구현
 */
public class DatabaseHelper
        extends SQLiteOpenHelper
        implements ItemRepository {


    /*
     * DB 파일 이름
     */
    private static final String DB_NAME =
            "lostfound.db";


    /*
     * 데이터베이스 버전
     *
     * 테이블 구조를 변경할 경우
     * 숫자를 증가시킬 수 있다.
     */
    private static final int DB_VERSION = 1;


    /*
     * 테이블 이름
     */
    private static final String TABLE_ITEM =
            "item";


    /*
     * 생성자
     */
    public DatabaseHelper(Context context) {

        super(
                context,
                DB_NAME,
                null,
                DB_VERSION
        );
    }


    /*
     * DB가 처음 만들어질 때 딱 한 번 실행
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        /*
         * ITEM 테이블 생성 SQL
         */
        String sql =
                "CREATE TABLE " + TABLE_ITEM + " (" +

                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        "type TEXT NOT NULL, " +

                        "name TEXT NOT NULL, " +

                        "category TEXT NOT NULL, " +

                        "color TEXT, " +

                        "location TEXT NOT NULL, " +

                        "date TEXT NOT NULL, " +

                        "description TEXT" +

                        ")";


        // SQL 실행
        db.execSQL(sql);
    }


    /*
     * DB 버전이 변경됐을 때 실행
     *
     * 지금 프로젝트에서는 학습용으로
     * 기존 테이블을 삭제 후 다시 생성한다.
     */
    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        db.execSQL(
                "DROP TABLE IF EXISTS "
                        + TABLE_ITEM
        );

        // 테이블 다시 생성
        onCreate(db);
    }


    /*
     * 물품 등록
     */
    @Override
    public long insertItem(Item item) {

        // 쓰기 가능한 DB 가져오기
        SQLiteDatabase db =
                getWritableDatabase();


        /*
         * ContentValues는
         * DB에 저장할 column / value를 담는 객체
         */
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


        /*
         * INSERT 실행
         *
         * 성공 → id 반환
         * 실패 → -1 반환
         */
        return db.insert(
                TABLE_ITEM,
                null,
                values
        );
    }


    /*
     * 전체 물품 조회
     */
    @Override
    public ArrayList<Item> getAllItems() {

        ArrayList<Item> itemList =
                new ArrayList<>();


        SQLiteDatabase db =
                getReadableDatabase();


        /*
         * id를 기준으로 최신 등록 항목이
         * 위에 나타나도록 DESC 사용
         */
        Cursor cursor =
                db.rawQuery(

                        "SELECT * FROM "
                                + TABLE_ITEM
                                + " ORDER BY id DESC",

                        null
                );


        /*
         * Cursor에 데이터가 있는 동안 반복
         */
        while (cursor.moveToNext()) {

            Item item =
                    cursorToItem(cursor);


            itemList.add(item);
        }


        // Cursor 사용이 끝났으므로 닫기
        cursor.close();


        return itemList;
    }


    /*
     * LOST / FOUND 구분 조회
     */
    @Override
    public ArrayList<Item> getItemsByType(
            String type
    ) {

        ArrayList<Item> itemList =
                new ArrayList<>();


        SQLiteDatabase db =
                getReadableDatabase();


        /*
         * ? 자리에 type 값이 들어간다.
         *
         * SQL 문자열에 직접 값을 연결하는 것보다
         * 안전하게 사용할 수 있다.
         */
        Cursor cursor =
                db.rawQuery(

                        "SELECT * FROM "
                                + TABLE_ITEM
                                + " WHERE type = ?"
                                + " ORDER BY id DESC",

                        new String[]{type}
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
     * 검색
     *
     * 물품명 또는 카테고리에
     * 검색어가 포함되어 있는 데이터 조회
     */
    @Override
    public ArrayList<Item> searchItems(
            String keyword
    ) {

        ArrayList<Item> itemList =
                new ArrayList<>();


        SQLiteDatabase db =
                getReadableDatabase();


        /*
         * LIKE %검색어%
         *
         * 예:
         *
         * keyword = 에어
         *
         * 에어팟도 검색됨
         */
        String searchKeyword =
                "%" + keyword + "%";


        Cursor cursor =
                db.rawQuery(

                        "SELECT * FROM "
                                + TABLE_ITEM
                                + " WHERE name LIKE ?"
                                + " OR category LIKE ?"
                                + " ORDER BY id DESC",

                        new String[]{
                                searchKeyword,
                                searchKeyword
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


    /*
     * ID로 물품 한 개 조회
     */
    @Override
    public Item getItemById(int id) {

        SQLiteDatabase db =
                getReadableDatabase();


        Cursor cursor =
                db.rawQuery(

                        "SELECT * FROM "
                                + TABLE_ITEM
                                + " WHERE id = ?",

                        new String[]{
                                String.valueOf(id)
                        }
                );


        Item item = null;


        /*
         * 해당 데이터가 존재하는 경우
         */
        if (cursor.moveToFirst()) {

            item =
                    cursorToItem(cursor);
        }


        cursor.close();


        return item;
    }


    /*
     * 물품정보 수정
     */
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


        /*
         * WHERE id = ?
         *
         * 해당 id의 데이터만 수정
         */
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


    /*
     * 물품 삭제
     */
    @Override
    public int deleteItem(int id) {

        SQLiteDatabase db =
                getWritableDatabase();


        return db.delete(

                TABLE_ITEM,

                "id = ?",

                new String[]{
                        String.valueOf(id)
                }
        );
    }


    /*
     * Cursor의 현재 행을
     * Item 객체로 변환하는 공통 메소드
     *
     * 같은 코드를 여러 번 작성하지 않기 위해
     * 따로 만들었다.
     */
    private Item cursorToItem(
            Cursor cursor
    ) {

        Item item =
                new Item();


        item.setId(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "id"
                        )
                )
        );


        item.setType(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "type"
                        )
                )
        );


        item.setName(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "name"
                        )
                )
        );


        item.setCategory(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "category"
                        )
                )
        );


        item.setColor(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "color"
                        )
                )
        );


        item.setLocation(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "location"
                        )
                )
        );


        item.setDate(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "date"
                        )
                )
        );


        item.setDescription(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "description"
                        )
                )
        );


        return item;
    }
}