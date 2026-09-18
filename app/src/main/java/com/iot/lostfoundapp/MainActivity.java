package com.iot.lostfoundapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity
        extends AppCompatActivity {

    private TextView textCurrentUser;

    private Button btnSwitchUser;

    private Button btnLostRegister;

    private Button btnFoundRegister;

    private Button btnItemList;

    private SessionManager sessionManager;


    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(
                savedInstanceState
        );


        sessionManager =
                new SessionManager(this);


        /*
         * 선택된 사용자가 없으면
         * 사용자 선택 화면으로 이동
         */
        if (sessionManager.getUserId()
                == -1) {

            Intent intent =
                    new Intent(
                            this,
                            NicknameActivity.class
                    );


            startActivity(intent);

            finish();

            return;
        }


        setContentView(
                R.layout.activity_main
        );


        textCurrentUser =
                findViewById(
                        R.id.textCurrentUser
                );

        btnSwitchUser =
                findViewById(
                        R.id.btnSwitchUser
                );

        btnLostRegister =
                findViewById(
                        R.id.btnLostRegister
                );

        btnFoundRegister =
                findViewById(
                        R.id.btnFoundRegister
                );

        btnItemList =
                findViewById(
                        R.id.btnItemList
                );


        textCurrentUser.setText(
                "현재 사용자 : " +
                        sessionManager.getNickname()
        );


        /*
         * 사용자 변경
         */
        btnSwitchUser.setOnClickListener(
                view -> {

                    sessionManager.clear();


                    Intent intent =
                            new Intent(
                                    this,
                                    NicknameActivity.class
                            );


                    startActivity(intent);

                    finish();
                }
        );


        /*
         * 분실물 등록
         */
        btnLostRegister.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    this,
                                    RegisterActivity.class
                            );


                    intent.putExtra(
                            RegisterActivity.EXTRA_ITEM_TYPE,
                            "LOST"
                    );


                    startActivity(intent);
                }
        );


        /*
         * 습득물 등록
         */
        btnFoundRegister.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    this,
                                    RegisterActivity.class
                            );


                    intent.putExtra(
                            RegisterActivity.EXTRA_ITEM_TYPE,
                            "FOUND"
                    );


                    startActivity(intent);
                }
        );


        /*
         * 목록
         */
        btnItemList.setOnClickListener(
                view -> {

                    Intent intent =
                            new Intent(
                                    this,
                                    ItemListActivity.class
                            );


                    startActivity(intent);
                }
        );
    }
}