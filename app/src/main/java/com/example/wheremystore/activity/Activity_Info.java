package com.example.wheremystore.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wheremystore.R;
import com.example.wheremystore.adapter.Adapter_Img;
import com.example.wheremystore.api_zip.StoreInfoAPI;
import com.example.wheremystore.dto.ImgDTO;
import com.example.wheremystore.dto.StoreInfoDTO;
import com.example.wheremystore.item.Retrofit_Item;
import com.example.wheremystore.item.Sp;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Info extends AppCompatActivity {
    TextView nameTv, addrTv, memoTv;
    TextView type01, type02, type03, type04, type05, type06, type07;
    TextView openTimeTv, closeTimeTv;
    TextView sunTv, monTv, thuTv, wenTv, thrTv, friTv, satTv;
    TextView pay01, pay02, pay03;
    RecyclerView recyclerView; // img 등록 하는 거 완료 후 등록하기
    Button deleteBtn;
    AppCompatImageButton bookmarkBtn;

    StoreInfoAPI storeInfoAPI = Retrofit_Item.getRetrofit().create(StoreInfoAPI.class);
    StoreInfoDTO dto;
    Sp sp;
    Adapter_Img adapter_img;
    ProgressDialog progressDialog;

    int seq;
    String userId;
    boolean bookmarkYN;
    int bookmarkSeq;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        sp = new Sp(Activity_Info.this);

        nameTv = findViewById(R.id.info_nameTv);
        addrTv = findViewById(R.id.info_addrTv);
        memoTv = findViewById(R.id.info_memoTv);
        type01 = findViewById(R.id.info_type01);
        type02 = findViewById(R.id.info_type02);
        type03 = findViewById(R.id.info_type03);
        type04 = findViewById(R.id.info_type04);
        type05 = findViewById(R.id.info_type05);
        type06 = findViewById(R.id.info_type06);
        type07 = findViewById(R.id.info_type07);
        openTimeTv = findViewById(R.id.info_openTimeTv);
        closeTimeTv = findViewById(R.id.info_closeTimeTv);
        sunTv = findViewById(R.id.info_week01);
        monTv = findViewById(R.id.info_week02);
        thuTv = findViewById(R.id.info_week03);
        wenTv = findViewById(R.id.info_week04);
        thrTv = findViewById(R.id.info_week05);
        friTv = findViewById(R.id.info_week06);
        satTv = findViewById(R.id.info_week07);
        pay01 = findViewById(R.id.info_pay01);
        pay02 = findViewById(R.id.info_pay02);
        pay03 = findViewById(R.id.info_pay03);
        recyclerView = findViewById(R.id.info_imgRecyclerView);
        deleteBtn = findViewById(R.id.info_deleteBtn);
        bookmarkBtn = findViewById(R.id.info_bookmarkBtn);

        findViewById(R.id.info_okBtn).setOnClickListener(v -> finish());
        bookmarkBtn.setOnClickListener(v -> bookmark());
        deleteBtn.setOnClickListener(v -> deleteInfo());

        progressDialog = ProgressDialog.show(Activity_Info.this, "안내", "잠시만 기다려 주세요...", true, false);

        setLayout();
    }

    private void setLayout() {
        Intent intent = getIntent();
        seq = intent.getIntExtra("seq", 0);
        userId = sp.getUserId();
        storeInfoAPI.getStoreInfo(seq, userId).enqueue(new Callback<StoreInfoDTO>() {
            @Override
            public void onResponse(@NonNull Call<StoreInfoDTO> call, @NonNull Response<StoreInfoDTO> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        dto = response.body();
                       if(dto.getId().equals(userId)) {
                            deleteBtn.setVisibility(View.VISIBLE);
                        }
                        nameTv.setText(dto.getName());
                        addrTv.setText(dto.getAddress());
                        // setText 에서 \n 을 인식하지 못하고 표기가 되기 때문에 replace 를 사용해서 라인 변경을 해준다.
                        memoTv.setText(dto.getMemo().replace("\\n", Objects.requireNonNull(System.getProperty("line.separator"))));
                        openTimeTv.setText(dto.getOpenTime());
                        closeTimeTv.setText(dto.getCloseTime());
                        int cnd = 0;
                        for(int i=0; i<dto.getType().length(); i++) {
                            cnd++;
                            String type = dto.getType().substring(i, i+1);
                            if(type.equals("1")) {
                                switch (cnd) {
                                    case 1 :
                                        setTextView(type01);
                                        break;
                                    case 2 :
                                        setTextView(type02);
                                        break;
                                    case 3 :
                                        setTextView(type03);
                                        break;
                                    case 4 :
                                        setTextView(type04);
                                        break;
                                    case 5 :
                                        setTextView(type05);
                                        break;
                                    case 6 :
                                        setTextView(type06);
                                        break;
                                    case 7 :
                                        setTextView(type07);
                                        break;
                                }
                            }
                        }
                        for(int i=0; i<dto.getDay().length(); i++) {
                            cnd++;
                            String day = dto.getDay().substring(i, i+1);
                            if(day.equals("1")) {
                                switch (cnd) {
                                    case 8 :
                                        setTextView(sunTv);
                                        break;
                                    case 9 :
                                        setTextView(monTv);
                                        break;
                                    case 10 :
                                        setTextView(thuTv);
                                        break;
                                    case 11 :
                                        setTextView(wenTv);
                                        break;
                                    case 12 :
                                        setTextView(thrTv);
                                        break;
                                    case 13 :
                                        setTextView(friTv);
                                        break;
                                    case 14 :
                                        setTextView(satTv);
                                        break;
                                }
                            }
                        }
                        for(int i=0; i<dto.getCredit().length(); i++) {
                            cnd++;
                            String type = dto.getType().substring(i, i+1);
                            if(type.equals("1")) {
                                switch (cnd) {
                                    case 15 :
                                        setTextView(pay01);
                                        break;
                                    case 16 :
                                        setTextView(pay02);
                                        break;
                                    case 17 :
                                        setTextView(pay03);
                                        break;
                                }
                            }
                        }

                        if(dto.getBookmarkSeq() == 0) {
                            bookmarkYN = false;
                            bookmarkBtn.setBackgroundResource(R.drawable.icon_bookmark_non);
                        } else {
                            bookmarkYN = true;
                            bookmarkSeq = dto.getBookmarkSeq();
                            bookmarkBtn.setBackgroundResource(R.drawable.icon_bookmark);
                        }

                        storeInfoAPI.getImgInfo(dto.getSeq()).enqueue(new Callback<List<ImgDTO>>() {
                            @Override
                            public void onResponse(@NonNull Call<List<ImgDTO>> call, @NonNull Response<List<ImgDTO>> response) {
                                if(response.isSuccessful()) {
                                    List<ImgDTO> dtoList = response.body();
                                    adapter_img = new Adapter_Img(null, dtoList);
                                    recyclerView.setAdapter(adapter_img);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(Activity_Info.this, LinearLayoutManager.HORIZONTAL, false));
                                    recyclerView.setVisibility(View.VISIBLE);
                                    progressDialog.dismiss();
                                } else {
                                    Log.e("Activity_info_error", "img 없음");
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<List<ImgDTO>> call, @NonNull Throwable t) {
                                Log.e("Activity_info_error", "img 없음");
                                progressDialog.dismiss();
                            }
                        });
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Info.this);
                        builder.setTitle("안내").setMessage("해당 글이 존재하지 않습니다.\n새로고침 후 다시 시도해주세요.");
                        builder.setPositiveButton("확인", ((dialogInterface, i) -> finish()));
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                } else {
                    Log.e("Activity_info_error", "error 발생");
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Info.this);
                    builder.setTitle("안내").setMessage("해당 글이 존재하지 않습니다.\n새로고침 후 다시 시도해주세요.");
                    builder.setPositiveButton("확인", ((dialogInterface, i) -> finish()));
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StoreInfoDTO> call, @NonNull Throwable t) {
                Log.e("Activity_info_error", "error : " + t.getMessage());
                Toast.makeText(Activity_Info.this, "자료가 없습니다.\n잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void setTextView(TextView tv) {
        tv.setTextColor(ContextCompat.getColor(this, R.color.white));
        tv.setBackgroundResource(R.drawable.frame03);
    }

    private void bookmark() {
        progressDialog.show();

        if(bookmarkYN) {
            storeInfoAPI.deleteBookmark(bookmarkSeq).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                    if(response.isSuccessful()) {
                        setLayout();
                    } else {
                        Toast.makeText(Activity_Info.this, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        Log.e("Activity_info_error", "bookmark error");
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                    Toast.makeText(Activity_Info.this, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    Log.e("Activity_info_error", "bookmark error : " + t.getMessage());
                    progressDialog.dismiss();
                }
            });
        } else {
            storeInfoAPI.registerBookmark(seq, userId).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                    if(response.isSuccessful()) {
                        setLayout();
                    } else {
                        Toast.makeText(Activity_Info.this, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        Log.e("Activity_info_error", "bookmark error");
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                    Toast.makeText(Activity_Info.this, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    Log.e("Activity_info_error", "bookmark error : " + t.getMessage());
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void deleteInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Info.this);
        builder.setTitle("안내").setMessage("해당 글을 삭제하시겠습니까?");
        builder.setPositiveButton("예", (dialogInterface, i) -> storeInfoAPI.deleteStore(seq).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(Activity_Info.this, "해당 게시글의 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Activity_Info.this, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Toast.makeText(Activity_Info.this, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                Log.e("Activity_Info", "Activity Info Delete Error : " + t.getMessage());
            }
        }));
        builder.setNegativeButton("아니오", ((dialogInterface, i) -> {  }));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}