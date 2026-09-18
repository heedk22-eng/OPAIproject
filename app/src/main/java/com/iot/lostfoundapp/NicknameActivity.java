package com.iot.lostfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


/*
 * 앱 시작 시 사용자 선택/생성 화면
 */
public class NicknameActivity
        extends AppCompatActivity {

    private EditText editNickname;

    private Button btnCreateUser;

    private Button btnEnterUser;

    private Spinner spinnerUsers;

    private DatabaseHelper databaseHelper;

    private SessionManager sessionManager;

    private ArrayList<User> userList;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );


        setContentView(
                R.layout.activity_nickname
        );


        editNickname =
                findViewById(
                        R.id.editNickname
                );

        btnCreateUser =
                findViewById(
                        R.id.btnCreateUser
                );

        btnEnterUser =
                findViewById(
                        R.id.btnEnterUser
                );

        spinnerUsers =
                findViewById(
                        R.id.spinnerUsers
                );


        databaseHelper =
                new DatabaseHelper(this);

        sessionManager =
                new SessionManager(this);


        loadUsers();


        btnCreateUser.setOnClickListener(
                view ->
                        createUser()
        );


        btnEnterUser.setOnClickListener(
                view ->
                        enterExistingUser()
        );
    }


    /*
     * 기존 사용자 목록
     */
    private void loadUsers() {

        userList =
                databaseHelper
                        .getAllUsers();


        ArrayAdapter<User> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        userList
                );


        adapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item
        );


        spinnerUsers.setAdapter(
                adapter
        );


        btnEnterUser.setEnabled(
                !userList.isEmpty()
        );
    }


    /*
     * 새 사용자
     */
    private void createUser() {

        String nickname =
                editNickname
                        .getText()
                        .toString()
                        .trim();


        if (nickname.isEmpty()) {

            editNickname.setError(
                    "닉네임을 입력해주세요."
            );

            return;
        }


        if (nickname.length() < 2) {

            editNickname.setError(
                    "닉네임은 2글자 이상 입력해주세요."
            );

            return;
        }


        /*
         * 중복 확인
         */
        User existing =
                databaseHelper
                        .getUserByNickname(
                                nickname
                        );


        if (existing != null) {

            Toast.makeText(
                    this,
                    "이미 사용 중인 닉네임입니다.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        long result =
                databaseHelper
                        .createUser(
                                nickname
                        );


        if (result == -1) {

            Toast.makeText(
                    this,
                    "사용자 생성에 실패했습니다.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        User user =
                databaseHelper
                        .getUserByNickname(
                                nickname
                        );


        if (user != null) {

            sessionManager.saveUser(
                    user.getId(),
                    user.getNickname()
            );


            openMainActivity();
        }
    }


    /*
     * 기존 사용자 입장
     */
    private void enterExistingUser() {

        User selectedUser =
                (User) spinnerUsers
                        .getSelectedItem();


        if (selectedUser == null) {
            return;
        }


        sessionManager.saveUser(
                selectedUser.getId(),
                selectedUser.getNickname()
        );


        openMainActivity();
    }


    private void openMainActivity() {

        Intent intent =
                new Intent(
                        this,
                        MainActivity.class
                );


        startActivity(intent);

        finish();
    }
}