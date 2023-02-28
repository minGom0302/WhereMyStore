package com.example.wheremystore.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.wheremystore.R;
import com.example.wheremystore.activity.Activity_Info;
import com.example.wheremystore.activity.Activity_Login;
import com.example.wheremystore.adapter.Adapter_myRecycler;
import com.example.wheremystore.api_zip.UserInfoAPI;
import com.example.wheremystore.dto.StoreInfoDTO;
import com.example.wheremystore.item.Retrofit_Item;
import com.example.wheremystore.item.Sp;
import com.google.android.material.snackbar.Snackbar;
import com.kakao.sdk.user.UserApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_My extends Fragment {
    TextView idTv, nicknameTv, memoTv;
    RadioButton rb01, rb02;
    RecyclerView recyclerView;
    CheckBox autoLoginUnlockCb;

    Sp sp;
    UserInfoAPI userInfoAPI;
    ProgressDialog progressDialog;

    int rbValues;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__my, container, false);

        progressDialog = ProgressDialog.show(requireContext(), "안내", "잠시만 기다려주세요...", true, false);

        sp = new Sp(requireContext());
        userInfoAPI = Retrofit_Item.getRetrofit().create(UserInfoAPI.class);

        idTv = view.findViewById(R.id.my_idTv);
        nicknameTv = view.findViewById(R.id.my_nicknameTv);
        memoTv = view.findViewById(R.id.my_memoTv);
        rb01 = view.findViewById(R.id.my_rb01);
        rb02 = view.findViewById(R.id.my_rb02);
        recyclerView = view.findViewById(R.id.my_recyclerview);
        autoLoginUnlockCb = view.findViewById(R.id.my_autoLoginCb);

        rb01.setOnClickListener(v -> {
            if(rb01.isChecked() && rbValues != 0) {
                secondSetLayout(0);
                rbValues = 0;
            }
        });
        rb02.setOnClickListener(v -> {
            if(rb02.isChecked() && rbValues != 1) {
                secondSetLayout(1);
                rbValues = 1;
            }
        });
        // 로그아웃 버튼
        view.findViewById(R.id.my_logoutBtn).setOnClickListener(v -> logout());

        setLayout();

        return view;
    }

    // 레이아웃 셋팅
    private void setLayout() {
        idTv.setText(sp.getUserId());
        nicknameTv.setText(sp.getUserNickname());

        if(rb01.isChecked()) {
            secondSetLayout(0);
        } else if(rb02.isChecked()) {
            secondSetLayout(1);
        }

        if(sp.getAutoLogin()) {
            autoLoginUnlockCb.setVisibility(View.VISIBLE);
        }
    }

    // 메뉴에 따른 리사이클러뷰 셋팅
    private void secondSetLayout(int cnd) {
        progressDialog.show();

        userInfoAPI.getRegisterInfo(sp.getUserId(), cnd).enqueue(new Callback<List<StoreInfoDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<StoreInfoDTO>> call, @NonNull Response<List<StoreInfoDTO>> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        memoTv.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        List<StoreInfoDTO> dtoList = response.body();
                        if(dtoList.size() == 0) {
                            if(cnd == 0) {
                                memoTv.setText(R.string.my_memoTv01);
                            } else if(cnd == 1) {
                                memoTv.setText(R.string.my_memoTv02);
                            }
                            memoTv.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            // 즐겨찾기 등록목록 둘 다 형태는 똑같이 할거라 구분 안함
                            Adapter_myRecycler favorite = new Adapter_myRecycler(dtoList);
                            recyclerView.setAdapter(favorite);
                            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
                        }
                        progressDialog.dismiss();
                    } else {
                        if(cnd == 0) {
                            memoTv.setText(R.string.my_memoTv01);
                        } else if(cnd == 1) {
                            memoTv.setText(R.string.my_memoTv02);
                        }
                        memoTv.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    }
                } else {
                    if(cnd == 0) {
                        memoTv.setText(R.string.my_memoTv01);
                    } else if(cnd == 1) {
                        memoTv.setText(R.string.my_memoTv02);
                    }
                    memoTv.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    Log.e("FragmentMy", "FragmentMy secondLayout is not successful");
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StoreInfoDTO>> call, @NonNull Throwable t) {
                Log.e("FragmentMy", "FragmentMy secondLayout failure : " + t.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    // 로그아웃 > 현재 카카오톡 로그아웃만 구현함
    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("경고").setMessage("로그아웃을 하시겠습니까?");
        builder.setPositiveButton("예", ((dialogInterface, i) -> {
            if(sp.getPlatformLogin()) {
                String id = sp.getUserId();
                String pw = sp.getUserPw();

                sp.setPlatformLogin(false);
                sp.setUserPlatformId("");
                sp.setUserName("");
                sp.setUserId("");
                sp.setUserPw("");
                sp.setUserNickname("");
                sp.setUserPhone("");
                sp.setUserBirth("");
                sp.setUserSex("");

                if(sp.getAutoLogin()) {
                    sp.setUserId(id);
                    sp.setUserPw(pw);
                } else if(sp.getIdSave()) {
                    sp.setUserId(id);
                }

                if(autoLoginUnlockCb.isChecked()) {
                    sp.setAutoLogin(false);
                }
                // 카카오 로그아웃
                UserApiClient.getInstance().logout(throwable -> null);
                Log.i("Fragment_My", "Fragment_My Logout");
                Log.i("test", "auto : " + sp.getAutoLogin() + " / save : " + sp.getIdSave());
            }

            Snackbar.make(requireView(), "로그아웃이 되었습니다.", Snackbar.LENGTH_SHORT).show();
            Intent intent = new Intent(requireContext(), Activity_Login.class);
            startActivity(intent);
            requireActivity().finish();
        }));
        builder.setNegativeButton("아니오", ((dialogInterface, i) -> {  }));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(rb01.isChecked()) {
            secondSetLayout(0);
        } else if(rb02.isChecked()) {
            secondSetLayout(1);
        }
    }
}