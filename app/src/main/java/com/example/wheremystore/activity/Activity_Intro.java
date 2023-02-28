package com.example.wheremystore.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.wheremystore.R;
import com.example.wheremystore.application.Permission;
import com.example.wheremystore.item.Retrofit_Item;
import com.example.wheremystore.item.Sp;

import java.util.Objects;

public class Activity_Intro extends AppCompatActivity {
    final static int PERMISSIONS_REQUEST = 1000;
    Sp sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Objects.requireNonNull(getSupportActionBar()).hide();

        // 권한 요청(체크)
        Permission.checkPermission(Activity_Intro.this);
        sp = new Sp(Activity_Intro.this);

        if(sp.getPermissionCheck()) {
            startSet();
        }
    }

    private void startSet() {
        // Retrofit 을 통해 API 사용하기 위해 셋팅
        Retrofit_Item.setGsonAndRetrofit();

        // platform 을 통해 로그인했는지 확인 후 로그인했으면 Main 화면으로, 로그아웃을 해서 다시 로그인해야되면 Login 화면으로
        Intent changeLayout;

        if(sp.getPlatformLogin()) {
            changeLayout = new Intent(Activity_Intro.this, Activity_Main.class);
        } else {
            changeLayout = new Intent(Activity_Intro.this, Activity_Login.class);
        }

        // need to change intro img to gif
        new Handler().postDelayed(() -> {
            startActivity(changeLayout);
            finish();
        }, 3000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 권한 체크 결과를 불러와 확인
        if (PERMISSIONS_REQUEST == requestCode) {
            for (int result : grantResults) {
                // GRANTED > 권한이 있는 경우 (0) || DENIED > 권한이 없는 경우 (-1)
                if (result == PackageManager.PERMISSION_DENIED) {
                    // 권한 체크에 동의하지 않으면 앱 종료
                    Toast.makeText(this, "앱을 실행하기 위해선 모든 권한 요청을 수락해주세요.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
            }

            // 권한이 있는 경우 실행됨  > why? 권한이 없으면 앞에서 앱이 종료됨
            Toast.makeText(this, "권한 요청이 모두 수락되었습니다.", Toast.LENGTH_SHORT).show();
            sp.setPermissionCheck(true);
            startSet();
        }
    }
}