package com.example.wheremystore.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wheremystore.R;
import com.example.wheremystore.api_zip.UserInfoAPI;
import com.example.wheremystore.dto.UserInfoDTO;
import com.example.wheremystore.item.Retrofit_Item;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_FindInfo extends AppCompatActivity {
    LinearLayout findInfoLayout, idInfoLayout, pwInfoLayout, pw_pwSetLayout, id_secLayout, id_idFindLayout;
    TextView pw_pwCndTv, pw_pwEqualsTv;
    EditText pw_phoneEt, pw_idEt, pw_pwEt, pw_pwAgainEt;
    AppCompatButton pw_authOkBtn, pw_okBtn;

    TextView id_secTv, id_secInfoTv, id_idFindTv;
    EditText id_phoneEt, id_nameEt, id_codeEt;
    AppCompatButton id_requestBtn, id_checkBtn, id_pwChangeBtn;

    boolean pw_pwOk = false;
    boolean pw_pwEquals = false;
    String userId;
    String userName;
    String userPhone;
    boolean id_ok;

    UserInfoAPI userInfoAPI;
    InputMethodManager imm;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId;
    Timer timer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_info);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_title03);

        userInfoAPI = Retrofit_Item.getRetrofit().create(UserInfoAPI.class);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        findInfoLayout = findViewById(R.id.findInfo_findInfoLayout);
        idInfoLayout = findViewById(R.id.idInfo_idInfoLayout);
        pwInfoLayout = findViewById(R.id.pwInfo_pwInfoLayout);
        findInfoLayout.setOnClickListener(v -> imm.hideSoftInputFromWindow(findInfoLayout.getWindowToken(), 0));
        //id find
        id_secLayout = findViewById(R.id.idFind_secondLayout);
        id_idFindLayout = findViewById(R.id.idFind_idFindLayout);
        id_secTv = findViewById(R.id.idFind_secondTv);
        id_secInfoTv = findViewById(R.id.idFind_secondInfoTv);
        id_idFindTv = findViewById(R.id.idFind_idFindTv);
        id_nameEt = findViewById(R.id.idFind_nameEt);
        id_phoneEt = findViewById(R.id.idFind_phoneEt);
        id_phoneEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        id_codeEt = findViewById(R.id.idFind_codeEt);
        id_requestBtn = findViewById(R.id.idFind_authOkBtn);
        id_checkBtn = findViewById(R.id.idFind_checkBtn);
        id_pwChangeBtn = findViewById(R.id.idFind_pwChangeBtn);
        id_requestBtn.setOnClickListener(v -> {
            imm.hideSoftInputFromWindow(id_requestBtn.getWindowToken(), 0);
            mAuth = FirebaseAuth.getInstance();
            id_requestBtn.setEnabled(false);
            id_requestBtn.setBackgroundResource(R.drawable.frame01);
            if(!id_checkBtn.isEnabled()) {
                id_checkBtn.setEnabled(true);
                id_checkBtn.setBackgroundResource(R.drawable.frame03);
            }
            requestCode();
        });
        id_checkBtn.setOnClickListener(v -> {
            imm.hideSoftInputFromWindow(id_checkBtn.getWindowToken(), 0);
            checkRequestCode();
        });
        id_pwChangeBtn.setOnClickListener(v -> {
            imm.hideSoftInputFromWindow(id_pwChangeBtn.getWindowToken(), 0);
            if(id_ok) {
                idInfoLayout.setVisibility(View.GONE);
                pwInfoLayout.setVisibility(View.VISIBLE);
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.pwFind);
                setPwLayout();
                pw_idEt.setText(userId);
                pw_phoneEt.setText(userPhone);
                pw_idEt.setEnabled(false);
                pw_phoneEt.setEnabled(false);
                pw_authOkBtn.setEnabled(false);
                pw_idEt.setBackgroundResource(R.drawable.frame01);
                pw_phoneEt.setBackgroundResource(R.drawable.frame01);
                pw_authOkBtn.setBackgroundResource(R.drawable.frame01);
                auth(id_pwChangeBtn);
            } else {
                finish();
            }
        });
        findViewById(R.id.idFind_closeBtn).setOnClickListener(v -> finish());


        // pw find
        pw_pwSetLayout = findViewById(R.id.pwFInd_pwSetLayout);
        pw_pwCndTv = findViewById(R.id.pwFind_pwCndTv);
        pw_pwEqualsTv = findViewById(R.id.pwFind_pwEqualsTv);
        pw_phoneEt = findViewById(R.id.pwFind_phoneEt);
        pw_idEt = findViewById(R.id.pwFind_idEt);
        pw_pwEt = findViewById(R.id.pwFind_pwEt);
        pw_pwAgainEt = findViewById(R.id.pwFind_pwAgainEt);
        pw_authOkBtn = findViewById(R.id.pwFind_authOkBtn);
        pw_authOkBtn.setOnClickListener(v -> auth(pw_authOkBtn));
        pw_okBtn = findViewById(R.id.pwFind_okBtn);
        pw_okBtn.setOnClickListener(v -> changePw(pw_okBtn));
        findViewById(R.id.pwFind_closeBtn).setOnClickListener(v -> close());

        findViewById(R.id.findInfo_idFindBtn).setOnClickListener(v -> {
            findInfoLayout.setVisibility(View.GONE);
            idInfoLayout.setVisibility(View.VISIBLE);
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.idFind);
        });
        findViewById(R.id.findInfo_pwFindBtn).setOnClickListener(v -> {
            findInfoLayout.setVisibility(View.GONE);
            pwInfoLayout.setVisibility(View.VISIBLE);
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.pwFind);
            setPwLayout();
        });
    }

    // ????????? ?????? ??????
    // ??????, ?????????????????? ????????? ?????? > ???????????? ?????? ?????? ????????? ????????? ????????? ???????????? ?????????, ????????? ???????????? ????????? ????????? ???????????? ???????????? ????????? ????????? ?????? ?????? ???????????? ???????????? ??????
    private void requestCode() {
        // ?????? ?????? ??????
        id_secInfoTv.setVisibility(View.VISIBLE);
        id_secLayout.setVisibility(View.VISIBLE);
        timer = new Timer();
        TimerTask task = new TimerTask() {
            int time = 90;
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if(time != 0) {
                        id_secTv.setText(String.valueOf(time));
                        time -= 1;
                    } else {
                        // ????????? ?????? ??? ???????????? ?????? ????????? ????????? ?????? ??????
                        cancel();
                        id_secInfoTv.setVisibility(View.GONE);
                        id_secTv.setText("????????? ?????????????????????. ?????? ??????????????????.");
                        id_requestBtn.setText(R.string.auth_send_again);
                        id_requestBtn.setEnabled(true);
                        id_requestBtn.setBackgroundResource(R.drawable.frame03);
                        id_checkBtn.setEnabled(false);
                        id_checkBtn.setBackgroundResource(R.drawable.frame01);
                    }
                });
            }
        };
        // 1????????? run ??????
        timer.schedule(task, 0, 1000);


        // ????????????????????? ?????? ???????????? ?????????
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                // ???????????? ?????? ??????????????? 1??? ???????????? ????????? ???????????? 82??? ???????????????
                .setPhoneNumber("+82" + id_phoneEt.getText().toString().replaceAll("-", ""))
                .setTimeout(90L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // ???????????? ????????? ???????????? ???
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // ???????????? ?????? ??????????????? ???????????? ???
                            Toast.makeText(Activity_FindInfo.this, "????????? ????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // ???????????? ?????? ?????? ????????? ?????? ???
                            Toast.makeText(Activity_FindInfo.this, "????????? ???????????? ?????? ????????? ????????????.\n?????? ??? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(verificationId, token);
                        // ??????????????? ???????????? ?????? ??? id??? ??????(????????? ?????? ??? ??????)??? ??????
                        mVerificationId = verificationId;
                        mResendToken = token;
                    }
                }).build();

        if(mResendToken != null) {
            // ???????????? ?????????
            PhoneAuthOptions.newBuilder(mAuth).setForceResendingToken(mResendToken);
        } else {
            // ???????????? ?????? ??????
            PhoneAuthProvider.verifyPhoneNumber(options);
        }
        Toast.makeText(this, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
    }

    private void checkRequestCode() {
        String codeNum = id_codeEt.getText().toString();

        if(codeNum.length() < 6) {
            Toast.makeText(this, "??????????????? ?????????????????? ????????????.", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, codeNum);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()) {
                // ????????? ??????????????? ??????????????? ????????? ???
                timer.cancel();
                id_secTv.setTextColor(ContextCompat.getColor(Activity_FindInfo.this, R.color.blue01));
                id_secTv.setText("????????? ????????? ?????????????????????.");
                id_secTv.setVisibility(View.GONE);

                id_nameEt.setEnabled(false);
                id_nameEt.setBackgroundResource(R.drawable.frame01);
                id_phoneEt.setEnabled(false);
                id_phoneEt.setBackgroundResource(R.drawable.frame01);
                id_codeEt.setEnabled(false);
                id_codeEt.setBackgroundResource(R.drawable.frame01);
                id_requestBtn.setEnabled(false);
                id_requestBtn.setBackgroundResource(R.drawable.frame01);
                id_checkBtn.setEnabled(false);
                id_checkBtn.setBackgroundResource(R.drawable.frame01);
                setIdLayout();
                Toast.makeText(this, "????????? ????????? ?????????????????????", Toast.LENGTH_SHORT).show();
            } else {
                // ???????????? ?????? ???
                Toast.makeText(this, "??????????????? ???????????? ????????????.\n?????? ?????????????????? ????????????.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setIdLayout() {
        id_idFindLayout.setVisibility(View.VISIBLE);
        userName = id_nameEt.getText().toString();
        userPhone = id_phoneEt.getText().toString().replaceAll("-", "");
        userInfoAPI.userInfoCheck2(userName, userPhone).enqueue(new Callback<List<UserInfoDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserInfoDTO>> call, @NonNull Response<List<UserInfoDTO>> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        List<UserInfoDTO> list = response.body();

                        String str = list.get(0).getUserId();
                        str = str.substring(0, 4) + "****";

                        id_idFindTv.setText(str);
                        id_ok = true;
                    } else {
                        id_idFindTv.setText(R.string.idFindText03);
                        id_pwChangeBtn.setText(R.string.ok);
                        id_ok = false;
                    }
                } else {
                    Log.e("Activity_FindInfo", "userInfoCheck2 error : " + response.errorBody());
                    Toast.makeText(Activity_FindInfo.this, "?????? ??? ?????? ?????????????????? ????????????.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserInfoDTO>> call, @NonNull Throwable t) {
                Log.e("Activity_FindInfo", "userInfoCheck2 error : " + t.getMessage());
                Toast.makeText(Activity_FindInfo.this, "?????? ??? ?????? ?????????????????? ????????????.", Toast.LENGTH_SHORT).show();
            }
        });
    }






    // ???????????? ?????? ??????
    private void setPwLayout() {
        // ???????????? ?????? ??? ?????????(-) ?????? ??????
        pw_phoneEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // ???????????? ????????? ?????? ?????? > ????????????, ??????, ????????? 3????????? ????????? 8?????? ??????????????? ???
        String pwRegex = "^(?=.*?[a-zA-Z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*]).{8,30}$";

        Pattern passPattern = Pattern.compile(pwRegex);
        pw_pwEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Matcher passMatcher = passPattern.matcher(pw_pwEt.getText().toString());
                if(passMatcher.find()) {
                    // ???????????? ?????? ??????
                    pw_pwCndTv.setText(R.string.pw_msg02);
                    pw_pwCndTv.setTextColor(ContextCompat.getColor(Activity_FindInfo.this, R.color.blue01));
                    pw_pwOk = true;
                } else {
                    // ???????????? ?????? ?????????
                    pw_pwCndTv.setText(R.string.pw_msg01);
                    pw_pwCndTv.setTextColor(ContextCompat.getColor(Activity_FindInfo.this, R.color.red01));
                    pw_pwOk = false;
                }

                if(pw_pwEt.getText().toString().equals(pw_pwAgainEt.getText().toString())) {
                    // ???????????? ??????
                    pw_pwEqualsTv.setText(R.string.pw_msg04);
                    pw_pwEqualsTv.setTextColor(ContextCompat.getColor(Activity_FindInfo.this, R.color.blue01));
                    pw_pwEquals = true;
                } else {
                    // ???????????? ?????????
                    pw_pwEqualsTv.setText(R.string.pw_msg03);
                    pw_pwEqualsTv.setTextColor(ContextCompat.getColor(Activity_FindInfo.this, R.color.red01));
                    pw_pwEquals = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // ???????????? ?????? ?????? ??????
        pw_pwAgainEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(pw_pwEt.getText().toString().equals(pw_pwAgainEt.getText().toString())) {
                    // ???????????? ??????
                    pw_pwEqualsTv.setText(R.string.pw_msg04);
                    pw_pwEqualsTv.setTextColor(ContextCompat.getColor(Activity_FindInfo.this, R.color.blue01));
                    pw_pwEquals = true;
                } else {
                    // ???????????? ?????????
                    pw_pwEqualsTv.setText(R.string.pw_msg03);
                    pw_pwEqualsTv.setTextColor(ContextCompat.getColor(Activity_FindInfo.this, R.color.red01));
                    pw_pwEquals = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void auth(AppCompatButton button) {
        imm.hideSoftInputFromWindow(button.getWindowToken(), 0);
        String id = pw_idEt.getText().toString();
        String phone = pw_phoneEt.getText().toString().replaceAll("-", "");
        // ????????? ?????????????????? ?????? ?????? ??? ?????? ?????? ???????????? ???????????? ??????
        userInfoAPI.userInfoCheck1(id, phone).enqueue(new Callback<UserInfoDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserInfoDTO> call, @NonNull Response<UserInfoDTO> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        userId = id;
                        userPhone = phone;
                        pw_pwSetLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(Activity_FindInfo.this, "????????? ??????????????? ??????????????????..", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Activity_FindInfo.this, "?????? ????????? ????????? ????????? ????????????.\n?????? ?????????????????? ????????????.", Toast.LENGTH_SHORT).show();
                        Log.e("Activity_FindInfo", "userInfoCheck data null");
                    }
                } else {
                    Toast.makeText(Activity_FindInfo.this, "?????? ??? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    Log.e("Activity_FindInfo", "userInfoCheck is not successful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserInfoDTO> call, @NonNull Throwable t) {
                Toast.makeText(Activity_FindInfo.this, "?????? ??? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                Log.e("Activity_FindInfo", "userInfoCheck is failure : " + t.getMessage());
            }
        });
    }

    private void changePw(AppCompatButton button) {
        imm.hideSoftInputFromWindow(button.getWindowToken(), 0);
        if(pw_pwOk && pw_pwEquals) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_FindInfo.this);
            builder.setTitle("??????").setMessage("????????? ????????? ??????????????? ?????????????????????????");
            builder.setPositiveButton("???", ((dialogInterface, i) -> {
                String userPw = pw_pwEt.getText().toString();
                userInfoAPI.passwordChange(userId, userPw, userPhone).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                        if(response.isSuccessful()) {
                            Toast.makeText(Activity_FindInfo.this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(Activity_FindInfo.this, "?????? ??? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                            Log.e("Activity_FindInfo", "changePw is not successful");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                        Toast.makeText(Activity_FindInfo.this, "?????? ??? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        Log.e("Activity_FindInfo", "changePw is failure : " + t.getMessage());
                    }
                });
            }));
            builder.setNegativeButton("?????????", (((dialogInterface, i) -> { })));

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void close() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_FindInfo.this);
        builder.setTitle("??????").setMessage("?????? ???????????? ??????????????????????");
        builder.setPositiveButton("???", ((dialogInterface, i) -> finish()));
        builder.setNegativeButton("?????????", (((dialogInterface, i) -> { })));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        close();
    }

    @Override
    public void finish() {
        super.finish();
        // ?????????????????? ??????
        if(mAuth != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }
}