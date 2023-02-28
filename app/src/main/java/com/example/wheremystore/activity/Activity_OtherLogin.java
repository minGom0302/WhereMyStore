package com.example.wheremystore.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wheremystore.R;
import com.example.wheremystore.api_zip.UserInfoAPI;
import com.example.wheremystore.dto.UserInfoDTO;
import com.example.wheremystore.item.Retrofit_Item;
import com.example.wheremystore.item.Sp;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_OtherLogin extends AppCompatActivity {
    EditText idEt, pwEt;
    CheckBox cb01, cb02;

    Sp sp;
    UserInfoAPI userInfoAPI;
    InputMethodManager imm;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_login);

        Objects.requireNonNull(getSupportActionBar()).hide();

        sp = new Sp(Activity_OtherLogin.this);
        userInfoAPI = Retrofit_Item.getRetrofit().create(UserInfoAPI.class);

        idEt = findViewById(R.id.otherLogin_idEt);
        pwEt = findViewById(R.id.otherLogin_pwEt);
        cb01 = findViewById(R.id.otherLogin_cb01);
        cb02 = findViewById(R.id.otherLogin_cb02);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        findViewById(R.id.otherLogin_loginBtn).setOnClickListener(v -> {
            hideKeyboard(findViewById(R.id.otherLogin_loginBtn));
            login();
        });
        findViewById(R.id.otherLogin_closeBtn).setOnClickListener(v -> finish());
        findViewById(R.id.otherLogin_registerBtn).setOnClickListener(v -> {
            hideKeyboard(findViewById(R.id.otherLogin_registerBtn));
            changeLayout(0);
        });
        findViewById(R.id.otherLogin_findInfoBtn).setOnClickListener(v -> {
            hideKeyboard(findViewById(R.id.otherLogin_findInfoBtn));
            changeLayout(1);
        });
        findViewById(R.id.otherLogin).setOnClickListener(v -> imm.hideSoftInputFromWindow(findViewById(R.id.otherLogin).getWindowToken(), 0));

        cb01.setOnClickListener(v -> {
            if(cb01.isChecked()) {
                cb02.setChecked(true);
                cb02.setEnabled(false);
            } else {
                cb02.setEnabled(true);
            }
        });

        setLayout();
    }

    private void setLayout() {
        if(sp.getIdSave()) {
            idEt.setText(sp.getUserId());
            cb01.setChecked(false);
            cb02.setChecked(true);
        }
    }

    private void login() {
        userInfoAPI.login(idEt.getText().toString(), pwEt.getText().toString()).enqueue(new Callback<UserInfoDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserInfoDTO> call, @NonNull Response<UserInfoDTO> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        UserInfoDTO userInfo = response.body();
                        sp.setPlatformLogin(true);
                        sp.setUserPlatformId(userInfo.getPlatformId());
                        sp.setUserId(userInfo.getUserId());
                        sp.setUserPw(userInfo.getUserPw());
                        sp.setUserNickname(userInfo.getUserNickname());
                        sp.setUserPhone(userInfo.getUserPhone());
                        sp.setUserBirth(userInfo.getUserBirth());
                        sp.setUserSex(userInfo.getUserSex());

                        sp.setAutoLogin(cb01.isChecked());
                        sp.setIdSave(cb02.isChecked());

                        changeLayout(2);
                    } else {
                        Log.e("Activity_OtherLogin", "response body is null");
                        Toast.makeText(Activity_OtherLogin.this, "아이디 혹은 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("Activity_OtherLogin", "response is not successful");
                    Toast.makeText(Activity_OtherLogin.this, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserInfoDTO> call, @NonNull Throwable t) {
                Log.e("Activity_OtherLogin", "login error : " + t.getMessage());
                Toast.makeText(Activity_OtherLogin.this, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeLayout(int cnd) {
        Intent intent = null;

        if(cnd == 0) {
            intent = new Intent(Activity_OtherLogin.this, Activity_Register.class);
            intent.putExtra("cnd", 1);
        } else if(cnd == 1) {
            intent = new Intent(Activity_OtherLogin.this, Activity_FindInfo.class);
        } else if(cnd == 2) {
            intent = new Intent(Activity_OtherLogin.this, Activity_Main.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Toast.makeText(this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
        }

        startActivity(intent);
    }

    private void hideKeyboard(AppCompatButton button) {
        imm.hideSoftInputFromWindow(button.getWindowToken(), 0);
    }
}