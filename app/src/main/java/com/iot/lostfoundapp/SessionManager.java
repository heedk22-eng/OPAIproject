package com.iot.lostfoundapp;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * 현재 어떤 사용자를 선택해서 앱을 사용하고 있는지 저장한다.
 *
 * 사용자 목록 자체는 SQLite에 저장하고,
 * 현재 사용자만 SharedPreferences에 저장한다.
 */
public class SessionManager {

    private static final String PREF_NAME =
            "lost_found_session";

    private static final String KEY_USER_ID =
            "user_id";

    private static final String KEY_NICKNAME =
            "nickname";

    private final SharedPreferences preferences;


    public SessionManager(Context context) {

        preferences =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );
    }


    /*
     * 현재 사용자 저장
     */
    public void saveUser(
            int userId,
            String nickname
    ) {

        preferences.edit()
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_NICKNAME, nickname)
                .apply();
    }


    /*
     * 현재 사용자 ID
     *
     * 선택된 사용자가 없으면 -1
     */
    public int getUserId() {

        return preferences.getInt(
                KEY_USER_ID,
                -1
        );
    }


    /*
     * 현재 사용자 닉네임
     */
    public String getNickname() {

        return preferences.getString(
                KEY_NICKNAME,
                ""
        );
    }


    /*
     * 현재 사용자 선택만 해제
     *
     * SQLite의 사용자 데이터는 삭제되지 않는다.
     */
    public void clear() {

        preferences.edit()
                .clear()
                .apply();
    }
}