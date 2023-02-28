package com.example.wheremystore.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.wheremystore.R;
import com.example.wheremystore.api_zip.UserInfoAPI;
import com.example.wheremystore.dto.UserInfoDTO;
import com.example.wheremystore.item.Retrofit_Item;
import com.example.wheremystore.item.Sp;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Register extends AppCompatActivity {
    TextView pwCndTv, pwEqualsTv, goTerms01Tv, goTerms02Tv, secondTv, secondInfoTv, idCheckTv;
    EditText idEt, pwEt, pwAgainEt, nameEt, nicknameEt, phoneEt, authCodeEt, birthYearEt, birthMonthEt, birthDayEt;
    RadioButton manRb, womanRb;
    CheckBox terms01Cb, terms02Cb;
    Button codeCheckBtn, codeRequestBtn, idEqualsOkBtn;
    LinearLayout secondLayout, registerLayout;
    ProgressDialog loading;

    Sp sp;
    InputMethodManager imm;
    UserInfoAPI userInfoAPI = Retrofit_Item.getRetrofit().create(UserInfoAPI.class);
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    int cnd;
    String mVerificationId;
    String codeNum;
    String platformId;
    String userName;
    boolean pwOk = false;
    boolean pwEquals = false;
    boolean codeEquals = false;
    boolean id_equals_ok = false;
    Timer timer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_title02);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        sp = new Sp(Activity_Register.this);

        // 파이어베이스 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();

        idEt = findViewById(R.id.register_idEt);
        pwCndTv = findViewById(R.id.register_pwCndTv); // 비밀번호 조건 맞는지 텍스트
        pwEqualsTv = findViewById(R.id.register_pwEqualsTv); // 비밀번호 일치하는지 텍스트
        goTerms01Tv = findViewById(R.id.register_goTerms01Tv); // 이용약관 바로가기
        goTerms02Tv = findViewById(R.id.register_goTerms02Tv); // 개인정보 동의 바로가기
        secondTv = findViewById(R.id.register_secondTv);
        secondInfoTv = findViewById(R.id.register_secondInfoTv);
        idCheckTv = findViewById(R.id.register_idCheckTv);
        pwEt = findViewById(R.id.register_pwEt);
        pwAgainEt = findViewById(R.id.register_pwAgainEt);
        nameEt = findViewById(R.id.register_nameEt);
        nicknameEt = findViewById(R.id.register_nicknameEt);
        phoneEt = findViewById(R.id.register_phoneEt);
        authCodeEt = findViewById(R.id.register_authCodeEt);
        birthYearEt = findViewById(R.id.register_birthYearEt);
        birthMonthEt = findViewById(R.id.register_birthMonthEt);
        birthDayEt = findViewById(R.id.register_birthDayEt);
        manRb = findViewById(R.id.register_manRb);
        womanRb = findViewById(R.id.register_womanRb);
        terms01Cb = findViewById(R.id.register_terms01Cb);
        terms02Cb = findViewById(R.id.register_terms02Cb);
        secondLayout = findViewById(R.id.register_secondLayout);
        registerLayout = findViewById(R.id.registerLayout);

        codeRequestBtn = findViewById(R.id.register_authCodeRequestBtn);
        codeCheckBtn = findViewById(R.id.register_authCodeOkBtn);
        idEqualsOkBtn = findViewById(R.id.register_idEqualsOkBtn);
        Button registerBtn = findViewById(R.id.register_registerBtn);
        codeRequestBtn.setOnClickListener(v -> codeRequest());
        codeCheckBtn.setOnClickListener(v -> codeCheck());
        idEqualsOkBtn.setOnClickListener(v -> {
            imm.hideSoftInputFromWindow(idEqualsOkBtn.getWindowToken(), 1);
            idCheck();
        });
        registerBtn.setOnClickListener(v -> register());

        setLayout();
    }

    private void setLayout() {
        // 키보드 내리기
        registerLayout.setOnClickListener(v -> imm.hideSoftInputFromWindow(registerLayout.getWindowToken(), 0) );

        // 넘어온 정보로 아이디와 닉네임만 설정
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String userNickname = intent.getStringExtra("userNickname");
        platformId = intent.getStringExtra("platformId");
        userName = intent.getStringExtra("userName");
        cnd = intent.getIntExtra("cnd", 0); // 0: Login 에서 넘어옴, 1: OtherLogin 에서 넘어옴

        if(userId == null) {
            idEt.setEnabled(true);
            idEt.setBackgroundResource(R.drawable.frame02);
            id_equals_ok = false;
            idEqualsOkBtn.setVisibility(View.VISIBLE);
            idCheckTv.setVisibility(View.INVISIBLE);
        } else {
            idEt.setEnabled(false);
            idEt.setBackgroundResource(R.drawable.frame01);
            idEt.setText(userId);
            nicknameEt.setText(userNickname);
            id_equals_ok = true;
        }

        if(userName == null) {
            nameEt.setEnabled(true);
            nameEt.setBackgroundResource(R.drawable.frame02);
        } else {
            nameEt.setEnabled(false);
            nameEt.setBackgroundResource(R.drawable.frame01);
            nameEt.setText(userName);
        }

        if(platformId == null) {
            platformId = "";
        }

        // 휴대전화 입력 시 하이픈(-) 자동 입력
        phoneEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // 비밀번호 정규식 검사 셋팅 > 대소문자, 숫자, 알파벳 3가지를 포함해 8자리 이상이여야 됨
        String pwRegex = "^(?=.*?[a-zA-Z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*]).{8,30}$";

        Pattern passPattern = Pattern.compile(pwRegex);
        pwEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Matcher passMatcher = passPattern.matcher(pwEt.getText().toString());
                if(passMatcher.find()) {
                    // 비밀번호 조건 일치
                    pwCndTv.setText(R.string.pw_msg02);
                    pwCndTv.setTextColor(ContextCompat.getColor(Activity_Register.this, R.color.blue01));
                    pwOk = true;
                } else {
                    // 비밀번호 조건 불일치
                    pwCndTv.setText(R.string.pw_msg01);
                    pwCndTv.setTextColor(ContextCompat.getColor(Activity_Register.this, R.color.red01));
                    pwOk = false;
                }

                if(pwEt.getText().toString().equals(pwAgainEt.getText().toString())) {
                    // 비밀번호 일치
                    pwEqualsTv.setText(R.string.pw_msg04);
                    pwEqualsTv.setTextColor(ContextCompat.getColor(Activity_Register.this, R.color.blue01));
                    pwEquals = true;
                } else {
                    // 비밀번호 불일치
                    pwEqualsTv.setText(R.string.pw_msg03);
                    pwEqualsTv.setTextColor(ContextCompat.getColor(Activity_Register.this, R.color.red01));
                    pwEquals = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // 비밀번호 일치 여부 확인
        pwAgainEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(pwEt.getText().toString().equals(pwAgainEt.getText().toString())) {
                    // 비밀번호 일치
                    pwEqualsTv.setText(R.string.pw_msg04);
                    pwEqualsTv.setTextColor(ContextCompat.getColor(Activity_Register.this, R.color.blue01));
                    pwEquals = true;
                } else {
                    // 비밀번호 불일치
                    pwEqualsTv.setText(R.string.pw_msg03);
                    pwEqualsTv.setTextColor(ContextCompat.getColor(Activity_Register.this, R.color.red01));
                    pwEquals = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void codeRequest() {
        // 키보드 내리기
        imm.hideSoftInputFromWindow(phoneEt.getWindowToken(), 0);

        // 입력 시간 표현
        secondInfoTv.setVisibility(View.VISIBLE);
        secondLayout.setVisibility(View.VISIBLE);
        timer = new Timer();
        TimerTask task = new TimerTask() {
            int time = 90;
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if(time != 0) {
                        secondTv.setText(String.valueOf(time));
                        time -= 1;
                    } else {
                        // 타이머 취소 및 시간초과 안내 그리고 재전송 버튼 설정
                        cancel();
                        secondInfoTv.setVisibility(View.GONE);
                        secondTv.setText("시간이 초과되었습니다. 다시 시도해주세요.");
                        codeRequestBtn.setText(R.string.auth_send_again);
                        codeRequestBtn.setEnabled(true);
                    }
                });
            }
        };
        // 1초마다 run 실행
        timer.schedule(task, 0, 1000);

        // 인증코드 번호와 입력칸 살리고 버튼 막기
        codeRequestBtn.setEnabled(false);
        codeRequestBtn.setBackgroundResource(R.drawable.frame01);
        authCodeEt.setEnabled(true);
        authCodeEt.setBackgroundResource(R.drawable.frame02);
        codeCheckBtn.setEnabled(true);
        codeCheckBtn.setBackgroundResource(R.drawable.frame03);

        authRequest();
    }

    private void authRequest() {
        // 파이어베이스를 통해 인증코드 보내기
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                // 테스트를 위해 국제코드를 1로 설정해둠 나중에 한국코드 82로 설정해야함
                .setPhoneNumber("+82" + phoneEt.getText().toString())
                .setTimeout(90L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {  }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // 인증코드 요청이 실패했을 때
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // 유효하지 않은 전화번호로 요청했을 때
                            showSnackBar("핸드폰 번호를 다시 확인해주세요.");
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // 유효하지 않은 인증 요청을 했을 때
                            showSnackBar("잘못된 방식으로 인증 요청을 했습니다.\n잠시 후 다시 시도해주세요.");
                        }
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(verificationId, token);
                        // 인증코드가 요청되어 왔을 때 id와 토큰(재발송 요청 시 사용)을 저장
                        mVerificationId = verificationId;
                        mResendToken = token;
                    }
                }).build();

        if(mResendToken != null) {
            // 인증코드 재요청
            PhoneAuthOptions.newBuilder(mAuth).setForceResendingToken(mResendToken);
        } else {
            // 인증코드 처음 요청
            PhoneAuthProvider.verifyPhoneNumber(options);
        }
        showSnackBar("인증코드를 발송했습니다.");
    }

    @SuppressLint("ResourceAsColor")
    private void codeCheck() {
        // 키보드 내리기
        imm.hideSoftInputFromWindow(authCodeEt.getWindowToken(), 0);

        // 인증코드 확인
        codeNum = authCodeEt.getText().toString();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, codeNum);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()) {
                // 입력한 코드번호와 코드번호가 일치할 때
                timer.cancel();
                secondTv.setTextColor(ContextCompat.getColor(Activity_Register.this, R.color.blue01));
                secondTv.setText("휴대폰 인증이 완료되었습니다.");
                secondInfoTv.setVisibility(View.GONE);

                codeEquals = true;
                phoneEt.setEnabled(false);
                phoneEt.setBackgroundResource(R.drawable.frame01);
                codeRequestBtn.setEnabled(false);
                codeRequestBtn.setBackgroundResource(R.drawable.frame01);
                authCodeEt.setEnabled(false);
                authCodeEt.setBackgroundResource(R.drawable.frame01);
                codeCheckBtn.setEnabled(false);
                codeCheckBtn.setBackgroundResource(R.drawable.frame01);
                showSnackBar("휴대폰 인증이 완료되었습니다.");
            } else {
                // 일치하지 않을 때
                showSnackBar("코드번호가 일치하지 않습니다.\n다시 확인하시기 바랍니다.");
                codeEquals = false;
            }
        });
    }

    private void idCheck() {
        //retrofit 하고 무조건 Alert 띄워서 결과에 따라 내용 작성
        loading = ProgressDialog.show(Activity_Register.this, "확인중...", "잠시만 기다려주세요...", true, false);
        String userId = idEt.getText().toString();
        userInfoAPI.checkId(userId).enqueue(new Callback<UserInfoDTO>() {
            @Override
            public void onResponse(@NonNull Call<UserInfoDTO> call, @NonNull Response<UserInfoDTO> response) {
                if(response.isSuccessful()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Register.this);
                    builder.setTitle("안내");
                    if(response.body() != null) {
                        builder.setMessage("사용중인 아이디입니다.\n다른 아이디로 설정해주시기 바랍니다.");
                        builder.setPositiveButton("예", ((dialogInterface, i) -> {  }));
                    } else {
                        builder.setMessage("사용 가능한 아이디입니다.\n해당 아이디로 사용하시겠습니까?");
                        builder.setPositiveButton("예", ((dialogInterface, i) -> {
                            id_equals_ok = true;
                            idCheckTv.setVisibility(View.VISIBLE);
                            idEqualsOkBtn.setVisibility(View.GONE);
                            idEt.setEnabled(false);
                            idEt.setBackgroundResource(R.drawable.frame01);
                        }));
                        builder.setNegativeButton("아니오", ((dialogInterface, i) -> {  }));
                    }
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    loading.dismiss();
                } else {
                    showSnackBar("회원가입에 실패했습니다.\n잠시 후 다시 시도해주시기 바랍니다.");
                    Log.i("id check", "id check response is not successful");
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserInfoDTO> call, @NonNull Throwable t) {
                showSnackBar("잠시 후 다시 시도해주시기 바랍니다.");
                Log.i("id check", "id check failure : " + t.getLocalizedMessage());
                loading.dismiss();
            }
        });
    }

    private void register() {
        loading = ProgressDialog.show(Activity_Register.this, "회원가입중...", "잠시만 기다려주세요...", true, false);
        // 가입 진행
        String name = nameEt.getText().toString();
        String id = idEt.getText().toString();
        String pw = pwEt.getText().toString();
        String pwAgain = pwAgainEt.getText().toString();
        String nickname = nicknameEt.getText().toString();
        String phone = phoneEt.getText().toString();
        String birth = "";
        String sex = "";
        String agree01 = "1";
        String agree02 = "1";
        String msg;

        // 입력 확인 및 값 설정
        if(!id_equals_ok) {
            msg = "아이디 중복 확인을 해주세요.";
            showSnackBar(msg);
            return;
        } else if(name.length() == 0) {
            msg = "이름을 입력해주세요";
            showSnackBar(msg);
            return;
        } else if(pw.length() == 0) {
            msg = "비밀번호를 입력해주세요";
            showSnackBar(msg);
            return;
        } else if(!pwOk) {
            msg = "비밀번호 조건에 맞게 입력해주세요.";
            showSnackBar(msg);
            return;
        } else if(pwAgain.length() == 0) {
            msg = "비밀번호 확인을 입력해주세요.";
            showSnackBar(msg);
            return;
        } else if(!pwEquals) {
            msg = "비밀번호가 서로 일치하지 않습니다.\n확인해주시기 바랍니다.";
            showSnackBar(msg);
            return;
        } else if(nickname.length() == 0) {
            msg = "닉네임을 입력해주세요.";
            showSnackBar(msg);
            return;
        } else if(phone.length() != 13) {
            msg = "올바른 핸드폰 번호를 입력해주세요.";
            showSnackBar(msg);
            return;
        } else if(!codeEquals) {
            msg = "인증번호 요청을 하시기 바랍니다.";
            showSnackBar(msg);
            return;
        } else if(!terms01Cb.isChecked()) {
            msg = "이용약관을 동의해주시기 바랍니다.";
            showSnackBar(msg);
            return;
        } else if(!terms02Cb.isChecked()) {
            msg = "개인정보 수집 이용에 동의해주시기 바랍니다.";
            showSnackBar(msg);
            return;
        }

        if(manRb.isChecked()) {
            sex = "0";
        } else if(womanRb.isChecked()) {
            sex = "1";
        } else if(!manRb.isChecked() && !womanRb.isChecked()) {
            sex = "9";
        }

        if(birthYearEt.getText().toString().length() == 4) {
            birth = birthYearEt.getText().toString();
        }
        if(birthMonthEt.getText().toString().length() == 2) {
            birth += birthMonthEt.getText().toString();
        }
        if(birthDayEt.getText().toString().length() == 2) {
            birth += birthDayEt.getText().toString();
        }
        if(birth.length() != 8) {
            birth = "";
        }

        // sp에 값 저장하고 db에 값 저장한 다음 main으로 이동
        sp.setPlatformLogin(true);
        sp.setUserName(name);
        sp.setUserPlatformId(platformId);
        sp.setUserId(id);
        sp.setUserPw(pw);
        sp.setUserNickname(nickname);
        sp.setUserPhone(phone.replaceAll("-", ""));
        sp.setUserBirth(birth);
        sp.setUserSex(sex);

        HashMap<String, String> data = new HashMap<>();
        data.put("platformId", platformId);
        data.put("userName", name);
        data.put("userId", id);
        data.put("userPw", pw);
        data.put("userPwAgain", pwAgain);
        data.put("userNickname", nickname);
        data.put("userPhone", phone.replaceAll("-", ""));
        data.put("userBirth", birth);
        data.put("userSex", sex);
        data.put("userAgree01", agree01);
        data.put("userAgree02", agree02);

        userInfoAPI.signUp(data).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                if(response.isSuccessful()) {
                    showSnackBar("회원가입에 성공했습니다!");
                    loading.dismiss();
                    Intent i = new Intent(Activity_Register.this, Activity_Main.class);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                showSnackBar("회원가입에 실패했습니다.\n잠시 후 다시 시도해주시기 바랍니다.");
                Log.i("signUp onFailure", t.getLocalizedMessage());
                loading.dismiss();
            }
        });
    }

    private void showSnackBar(String msg) { Snackbar.make(registerLayout, msg, Snackbar.LENGTH_SHORT).show(); }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Register.this);
        builder.setTitle("경고").setMessage("회원가입을 멈추시겠습니까?");
        builder.setPositiveButton("예", ((dialog, which) -> {
            if(cnd == 0) {
                Intent intent = new Intent(Activity_Register.this, Activity_Login.class);
                startActivity(intent);
            }
            finish();
        }));
        builder.setNegativeButton("아니오", ((dialog, which) -> { }));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void finish() {
        super.finish();
        // 파이어베이스 끊기
        FirebaseAuth.getInstance().signOut();
    }
}