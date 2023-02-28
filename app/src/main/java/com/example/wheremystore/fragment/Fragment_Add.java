package com.example.wheremystore.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.wheremystore.R;
import com.example.wheremystore.activity.Activity_Map;
import com.example.wheremystore.adapter.Adapter_Img;
import com.example.wheremystore.api_zip.StoreInfoAPI;
import com.example.wheremystore.item.BottomSheet;
import com.example.wheremystore.item.Retrofit_Item;
import com.example.wheremystore.item.Sp;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Add extends Fragment {
    EditText nameEt, memoEt;
    TextView mapChoiceTv, openTimeTv, closeTimeTv, addrTv;
    // 음식 종류
    CheckBox type01, type02, type03, type04, type05, type06, type07;
    // 요일
    CheckBox mon, tue, wen, thr, fri, sat, sun;
    // 결제 방법
    CheckBox cash, card, bank;

    RecyclerView imgRecyclerView;
    Adapter_Img adapterImg;

    int MAP_REQUEST_CODE = 1004;
    String lat, lng, address;
    List<Uri> uriList;
    String imageFilePath = null; // 내부 저장소에 저장한 image > 사진 저장하거나 빠져나갈 때 삭제하기
    
    StoreInfoAPI storeInfoAPI = Retrofit_Item.getRetrofit().create(StoreInfoAPI.class);
    Sp sp;
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__add, container, false);

        sp = new Sp(requireContext());

        nameEt = view.findViewById(R.id.add_nameEt);
        addrTv = view.findViewById(R.id.add_addrTv);
        memoEt = view.findViewById(R.id.add_memoEt);
        mapChoiceTv = view.findViewById(R.id.add_mapChoiceTv);
        openTimeTv = view.findViewById(R.id.add_openTimeTv);
        closeTimeTv = view.findViewById(R.id.closeTimeTv);
        type01 = view.findViewById(R.id.add_chType01);
        type02 = view.findViewById(R.id.add_chType02);
        type03 = view.findViewById(R.id.add_chType03);
        type04 = view.findViewById(R.id.add_chType04);
        type05 = view.findViewById(R.id.add_chType05);
        type06 = view.findViewById(R.id.add_chType06);
        type07 = view.findViewById(R.id.add_chType07);
        mon = view.findViewById(R.id.add_chWeek02);
        tue = view.findViewById(R.id.add_chWeek03);
        wen = view.findViewById(R.id.add_chWeek04);
        thr = view.findViewById(R.id.add_chWeek05);
        fri = view.findViewById(R.id.add_chWeek06);
        sat = view.findViewById(R.id.add_chWeek07);
        sun = view.findViewById(R.id.add_chWeek01);
        cash = view.findViewById(R.id.add_chPay01);
        card = view.findViewById(R.id.add_chPay02);
        bank = view.findViewById(R.id.add_chPay03);
        imgRecyclerView = view.findViewById(R.id.add_imgRecyclerView);

        type01.setOnClickListener(v -> textColorChange(type01));
        type02.setOnClickListener(v -> textColorChange(type02));
        type03.setOnClickListener(v -> textColorChange(type03));
        type04.setOnClickListener(v -> textColorChange(type04));
        type05.setOnClickListener(v -> textColorChange(type05));
        type06.setOnClickListener(v -> textColorChange(type06));
        type07.setOnClickListener(v -> textColorChange(type07));
        mon.setOnClickListener(v -> textColorChange(mon));
        tue.setOnClickListener(v -> textColorChange(tue));
        wen.setOnClickListener(v -> textColorChange(wen));
        thr.setOnClickListener(v -> textColorChange(thr));
        fri.setOnClickListener(v -> textColorChange(fri));
        sat.setOnClickListener(v -> textColorChange(sat));
        sun.setOnClickListener(v -> textColorChange(sun));
        cash.setOnClickListener(v -> textColorChange(cash));
        card.setOnClickListener(v -> textColorChange(card));
        bank.setOnClickListener(v -> textColorChange(bank));

        mapChoiceTv.setOnClickListener(v -> mapChoice());
        openTimeTv.setOnClickListener(v -> timeChoice(openTimeTv));
        closeTimeTv.setOnClickListener(v -> timeChoice(closeTimeTv));

        Button imgBtn = view.findViewById(R.id.add_imgBtn);
        Button registerBtn = view.findViewById(R.id.add_registerBtn);
        imgBtn.setOnClickListener(v -> choiceImg());
        registerBtn.setOnClickListener(v -> register());

        return view;
    }

    @SuppressLint("ResourceAsColor")
    private void textColorChange(CheckBox cb) {
        if(cb.isChecked()) {
            // 이 형식은 작동 안됨 > setTextColor(R.color.white); 아래처럼 작동시켜야됨
            cb.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        } else {
            if(cb == sun) {
                cb.setTextColor(ContextCompat.getColor(requireContext(), R.color.red01));
            } else if(cb == sat) {
                cb.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue01));
            } else {
                cb.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            }
        }
    }

    private void timeChoice(TextView tv) {
        // theme 를 바꾸기 위해 getActivity() 와 (view...) 사이에 추가함
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog, (view, hourOfDay, minute) -> {
            String timeSet;
            if(hourOfDay < 10) {
                timeSet = "오전 0" + hourOfDay;
            } else if(hourOfDay < 12) {
                timeSet = "오전 " + hourOfDay;
            } else if(hourOfDay == 12) {
                timeSet = "오후 12";
            } else if(hourOfDay < 22) {
                int i = hourOfDay - 12;
                timeSet = "오후 0" + i;
            } else {
                int i = hourOfDay - 12;
                timeSet = "오후 " + i;
            }
            timeSet += " : ";
            if(minute < 10) {
                timeSet += "0" + minute;
            } else {
                timeSet += String.valueOf(minute);
            }
            tv.setText(timeSet);
        }, 0, 0, false);
        timePickerDialog.show();
    }

    private void mapChoice() {
        Intent intent = new Intent(requireActivity(), Activity_Map.class);
        startActivityForResult(intent, MAP_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MAP_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                if(data != null) {
                    address = data.getStringExtra("address");
                    lat = data.getStringExtra("lat");
                    lng = data.getStringExtra("lng");
                    addrTv.setText(address);
                }
            }
        }
    }

    private void choiceImg() {
        // BottomSheet 보여주기
        BottomSheet bottomSheet = new BottomSheet();
        // BottomSheet 에서 자료 넘어오는거 받기
        getParentFragmentManager().setFragmentResultListener("choiceImgRequestKey", requireActivity(), (requestKey, result) -> {
            // 저장소에 저장된 사진이 있으면 지우기
            deleteImageFile();

            uriList = result.getParcelableArrayList("uriList");
            imageFilePath = result.getString("imageFilePath");

            adapterImg = new Adapter_Img(uriList);
            imgRecyclerView.setAdapter(adapterImg);
            imgRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        });
        bottomSheet.show(requireFragmentManager(), bottomSheet.getTag());
    }

    private void register() {
        progressDialog = ProgressDialog.show(requireContext(), "등록", "잠시만 기다려주세요...", true, false);

        if(nameEt.getText().length() == 0) {
            Snackbar.make(requireView(), "이름 혹은 위치정보를 입력해주세요.", Snackbar.LENGTH_SHORT).show();
            return;
        } else if(address == null || address.equals("")) {
            Snackbar.make(requireView(), "주소를 입력해주세요.", Snackbar.LENGTH_SHORT).show();
            return;
        }
        
        StringBuilder type = new StringBuilder();
        String openTime = "";
        String closeTime = "";
        StringBuilder day = new StringBuilder();
        StringBuilder credit = new StringBuilder();
        String memo = "";
        
        if(openTimeTv.getText().length() != 0) {
            openTime = openTimeTv.getText().toString();
        }
        if(closeTimeTv.getText().length() != 0) {
            closeTime = closeTimeTv.getText().toString();
        }
        if(memoEt.getText().length() != 0) {
            memo = memoEt.getText().toString();
        }
        // day 설정
        for(int i=0; i<7; i++) {
            switch (i) {
                case 0 :
                    if(type01.isChecked()) {
                        type = new StringBuilder("1");
                    } else {
                        type = new StringBuilder("0");
                    }
                    if(sun.isChecked()) {
                        day = new StringBuilder("1");
                    } else {
                        day = new StringBuilder("0");
                    }
                    if(cash.isChecked()) {
                        credit = new StringBuilder("1");
                    } else {
                        credit = new StringBuilder("0");
                    }
                    break;
                case 1 :
                    if(type02.isChecked()) {
                        type.append("1");
                    } else {
                        type.append("0");
                    }
                    if(mon.isChecked()) {
                        day.append("1");
                    } else {
                        day.append("0");
                    }
                    if(card.isChecked()) {
                        credit.append("1");
                    } else {
                        credit.append("0");
                    }
                    break;
                case 2 :
                    if(type03.isChecked()) {
                        type.append("1");
                    } else {
                        type.append("0");
                    }
                    if(tue.isChecked()) {
                        day.append("1");
                    } else {
                        day.append("0");
                    }
                    if(bank.isChecked()) {
                        credit.append("1");
                    } else {
                        credit.append("0");
                    }
                    break;
                case 3 :
                    if(type04.isChecked()) {
                        type.append("1");
                    } else {
                        type.append("0");
                    }
                    if(wen.isChecked()) {
                        day.append("1");
                    } else {
                        day.append("0");
                    }
                    break;
                case 4 :
                    if(type05.isChecked()) {
                        type.append("1");
                    } else {
                        type.append("0");
                    }
                    if(thr.isChecked()) {
                        day.append("1");
                    } else {
                        day.append("0");
                    }
                    break;
                case 5 :
                    if(type06.isChecked()) {
                        type.append("1");
                    } else {
                        type.append("0");
                    }
                    if(fri.isChecked()) {
                        day.append("1");
                    } else {
                        day.append("0");
                    }
                    break;
                case 6 :
                    if(type07.isChecked()) {
                        type.append("1");
                    } else {
                        type.append("0");
                    }
                    if(sat.isChecked()) {
                        day.append("1");
                    } else {
                        day.append("0");
                    }
                    break;
            }
            
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("type", type.toString());
        data.put("name", nameEt.getText().toString());
        data.put("address", address);
        data.put("lat", lat);
        data.put("lng", lng);
        data.put("openTime", openTime);
        data.put("closeTime", closeTime);
        data.put("day", day.toString());
        data.put("credit", credit.toString());
        data.put("memo", memo);
        data.put("id", sp.getUserId());

        // 자료 업로드하면 사진 업로드를 여기서 하고 완료 시 아래 처리하기
        ArrayList<MultipartBody.Part> bodyList = new ArrayList<>();
        if(uriList != null) {
            for (Uri uri : uriList) {
                // 사진의 루트 가져와서 File 객체 생성
                String imagePath = getRealPathFromUri(uri);
                File file = new File(imagePath);
                String timeMillis = String.valueOf(System.currentTimeMillis());

                // MultipartBody 형식으로 만들어 ArrayList 모으기
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("files", nameEt.getText() + timeMillis, requestBody);
                bodyList.add(uploadFile);
            }
        } else {
            bodyList.add(null);
        }

        // 사진은 따로 업로드할거고 type은 서버로 넘어가서 분리해서 저장, 조회 시 type을 분리해서 where 문을 만들어서 mapper에 전달하여 조회
        storeInfoAPI.register(data, bodyList).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                if(response.isSuccessful()) {
                    // 저장소에 저장된 사진이 있으면 지우기
                    deleteImageFile();
                    // 로딩창 없애기
                    progressDialog.dismiss();
                    Snackbar.make(requireView(), "등록이 완료되었습니다.", Snackbar.LENGTH_SHORT).show();
                    // 프래그먼트 리프레쉬 모르겠음 > 하드코딩
                    clearLayout();
                } else {
                    Log.e("storeInfo", "store info register failure");
                    // 실패 안내와 함께 로딩창 없애기
                    progressDialog.dismiss();
                    Snackbar.make(requireView(), "등록 실패, 잠시 후 다시 시도해주세요.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                Log.e("storeInfo", "store info register failure " + t.getMessage());
                // 실패 안내와 함께 로딩창 없애기
                progressDialog.dismiss();
                Snackbar.make(requireView(), "등록 실패, 잠시 후 다시 시도해주세요.", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private String getRealPathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(requireContext(), uri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        assert cursor != null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    private void deleteImageFile() {
        // 저장소에 저장된 사진이 있으면 지우기
        if(imageFilePath != null) {
            File deleteFile = new File(imageFilePath);
            boolean b = deleteFile.delete();
            Log.i("delete YN", "delete TF = " + b);
        }
    }

    private void clearLayout() {
        nameEt.setText("");
        addrTv.setText(null);
        type01.setChecked(false);
        textColorChange(type01);
        type02.setChecked(false);
        textColorChange(type02);
        type03.setChecked(false);
        textColorChange(type03);
        type04.setChecked(false);
        textColorChange(type04);
        type05.setChecked(false);
        textColorChange(type05);
        type06.setChecked(false);
        textColorChange(type06);
        type07.setChecked(false);
        textColorChange(type07);
        openTimeTv.setText("");
        closeTimeTv.setText("");
        sun.setChecked(false);
        textColorChange(sun);
        mon.setChecked(false);
        textColorChange(mon);
        tue.setChecked(false);
        textColorChange(tue);
        wen.setChecked(false);
        textColorChange(wen);
        thr.setChecked(false);
        textColorChange(thr);
        fri.setChecked(false);
        textColorChange(fri);
        sat.setChecked(false);
        textColorChange(sat);
        card.setChecked(false);
        textColorChange(card);
        cash.setChecked(false);
        textColorChange(cash);
        bank.setChecked(false);
        textColorChange(bank);
        memoEt.setText("");
        imgRecyclerView.removeAllViewsInLayout();
        imgRecyclerView.setVisibility(View.GONE);
    }
}