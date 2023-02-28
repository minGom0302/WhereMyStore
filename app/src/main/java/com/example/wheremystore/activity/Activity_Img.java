package com.example.wheremystore.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.wheremystore.R;
import com.example.wheremystore.adapter.Adapter_ImgFull;
import com.example.wheremystore.dto.ImgDTO;
import com.example.wheremystore.item.ItemList;

import java.util.List;

public class Activity_Img extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);

        Intent getIntent = getIntent();
        int position = getIntent.getIntExtra("position", 0);
        List<ImgDTO> imgList = ItemList.getImgDTOList();

        Log.i("test", "imgList.size : " + imgList.size());

        ViewPager2 viewPager2 = findViewById(R.id.img_viewPage);
        Adapter_ImgFull imgFull = new Adapter_ImgFull(imgList); // 뷰페이저 어뎁터 생성
        viewPager2.setAdapter(imgFull); // 어뎁터 연결
        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager2.setCurrentItem(position); // 사진의 포지션을 설정할 수 있음

    }
}