package com.iot.lostfoundapp;

/*
 * 같은 기기 안에서 사용자를 구분하기 위한 클래스
 */
public class User {

    // 사용자 고유번호
    private int id;

    // 중복되지 않는 닉네임
    private String nickname;


    public User() {
    }


    public User(int id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    /*
     * Spinner에 User 객체를 넣었을 때
     * 닉네임이 표시되도록 한다.
     */
    @Override
    public String toString() {
        return nickname;
    }
}