package com.example.wheremystore.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.os.Bundle;

import com.example.wheremystore.R;
import com.example.wheremystore.fragment.Fragment_Add;
import com.example.wheremystore.fragment.Fragment_Map;
import com.example.wheremystore.fragment.Fragment_My;
import com.example.wheremystore.item.BackspaceHandler;
import com.example.wheremystore.item.Sp;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class Activity_Main extends AppCompatActivity {
    Fragment_Map f01 = null;
    Fragment_Add f02 = null;
    Fragment_My f03 = null;
    BackspaceHandler bsh = new BackspaceHandler(Activity_Main.this);
    Sp sp;

    @SuppressLint({"MissingInflatedId", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bn = findViewById(R.id.main_bottomNavigation);

        sp = new Sp(Activity_Main.this);
        // 첫 화면으로 map (지도)를 띄움
        f01 = new Fragment_Map();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frameContainer, f01).commit();

        // bottom menu 선택 별 화면 설정 (replace 말고 show&hide 를 사용하는 것은 화면전환을 했을 때 구현된 화면 그대로 가져오기 위해서)
        bn.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.main_bottom01 :
                    getSupportFragmentManager().beginTransaction().show(f01).commit();
                    if(f02 != null) getSupportFragmentManager().beginTransaction().hide(f02).commit();
                    if(f03 != null) getSupportFragmentManager().beginTransaction().hide(f03).commit();
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.map_title);
                    break;
                case R.id.main_bottom02 :
                    if (f02 == null) {
                        f02 = new Fragment_Add();
                        getSupportFragmentManager().beginTransaction().add(R.id.main_frameContainer, f02).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().show(f02).commit();
                    }
                    if(f01 != null) getSupportFragmentManager().beginTransaction().hide(f01).commit();
                    if(f03 != null) getSupportFragmentManager().beginTransaction().hide(f03).commit();
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.add_title);
                    break;
                case R.id.main_bottom03 :
                    if (f03 == null) {
                        f03 = new Fragment_My();
                        getSupportFragmentManager().beginTransaction().add(R.id.main_frameContainer, f03).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().show(f03).commit();
                    }
                    if(f01 != null) getSupportFragmentManager().beginTransaction().hide(f01).commit();
                    if(f02 != null) getSupportFragmentManager().beginTransaction().hide(f02).commit();
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.myPage);
                    break;
            }
            return true;
        });
    }

    @Override
    public void onBackPressed() { bsh.onBackPressed("'뒤로가기'를 한번 더 누르면 종료됩니다."); }
}