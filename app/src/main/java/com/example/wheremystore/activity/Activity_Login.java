package com.example.wheremystore.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.example.wheremystore.R;
import com.example.wheremystore.api_zip.UserInfoAPI;
import com.example.wheremystore.dto.UserInfoDTO;
import com.example.wheremystore.item.BackspaceHandler;
import com.example.wheremystore.item.Retrofit_Item;
import com.example.wheremystore.item.Sp;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Login extends AppCompatActivity {
    Function2<OAuthToken, Throwable, Unit> callback;
    UserInfoAPI userInfoAPI = Retrofit_Item.getRetrofit().create(UserInfoAPI.class);
    BackspaceHandler bs = new BackspaceHandler(Activity_Login.this);
    ProgressDialog loading;
    Sp sp;
    GoogleSignInClient mGoogleSignInClient;

    String TAG = "Activity_Login";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();

        sp = new Sp(Activity_Login.this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(Activity_Login.this, gso);

        findViewById(R.id.login_kakaoLoginImg).setOnClickListener(v -> kakaoLogin());
        findViewById(R.id.login_otherLoginBtn).setOnClickListener(v -> {
            Intent intent = new Intent(Activity_Login.this, Activity_OtherLogin.class);
            startActivity(intent);
        });

        setLayout();
    }

    private void setLayout() {
        // 카카오 로그인 시 콜백되는 메서드
        callback = (oAuthToken, throwable) -> {
            // 이때 토큰이 전달이 되면 로그인이 성공한 것이고 토큰이 전달되지 않았다면 로그인 실패
            if (oAuthToken != null) {
                Log.i(TAG, "Login Success (Token) : " + oAuthToken.getAccessToken());
                updateKakaoLoginUi();
            } else if (throwable != null) {
                Toast.makeText(Activity_Login.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Login failure", throwable);
            }
            return null;
        };

        if(sp.getAutoLogin()) {
            userInfoAPI.login(sp.getUserId(), sp.getUserPw()).enqueue(new Callback<UserInfoDTO>() {
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

                            changeLayout();
                        } else {
                            Log.e("Activity_Login", "response body is null");
                            Toast.makeText(Activity_Login.this, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Activity_Login", "login is not successful");
                        Toast.makeText(Activity_Login.this, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserInfoDTO> call, @NonNull Throwable t) {
                    Log.e("Activity_Login", "login error : " + t.getMessage());
                    Toast.makeText(Activity_Login.this, "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void kakaoLogin() {
        loading = ProgressDialog.show(Activity_Login.this, "로딩중 ...", "잠시만 기다려주세요...", true, false);

        if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(Activity_Login.this)) {
            // 카카오톡이 있을 경우 카카오톡으로 연동하여 로그인 요청
            UserApiClient.getInstance().loginWithKakaoTalk(Activity_Login.this, callback);
        } else {
            // 카카오톡이 없을 경우 웹을 통해 카카오톡 계정 로그인 요청
            UserApiClient.getInstance().loginWithKakaoAccount(Activity_Login.this, callback);
        }
    }

    private void updateKakaoLoginUi() {
        // 카카오 UI 가져오는 메소드 (로그인 시 정보를 가져옴)
        UserApiClient.getInstance().me((user, throwable) -> {
            // 로그인이 되었다면
            if (user!=null){
                String platformId = String.valueOf(user.getId());
                userInfoAPI.getUserInfo(platformId).enqueue(new Callback<UserInfoDTO>() {
                    @Override
                    public void onResponse(@NonNull Call<UserInfoDTO> call, @NonNull Response<UserInfoDTO> response) {
                        if(response.isSuccessful()) {
                            Intent intent;
                            if(response.body() != null) {
                                Log.i("api 여부", "통신 성공했고 값이 있다.");
                                UserInfoDTO userInfo = response.body();
                                // 정보가 있으면 로그아웃하기 전까지 자동으로 로그인할 수 있도록 sp에 값을 저장
                                sp.setPlatformLogin(true);
                                sp.setUserPlatformId(userInfo.getPlatformId());
                                sp.setUserName(userInfo.getUserName());
                                sp.setUserId(userInfo.getUserId());
                                sp.setUserPw(userInfo.getUserPw());
                                sp.setUserNickname(userInfo.getUserNickname());
                                sp.setUserPhone(userInfo.getUserPhone());
                                sp.setUserBirth(userInfo.getUserBirth());
                                sp.setUserSex(userInfo.getUserSex());
                                sp.setAutoLogin(false);
                                sp.setIdSave(false);
                                // 메인 화면으로 이동
                                intent = new Intent(Activity_Login.this, Activity_Main.class);
                            } else {
                                Log.i("api 여부", "통신 성공했지만 값이 없다.");
                                // 조회된 정보가 없으면 회원가입으로 넘어가 가입하여 로그인하도록 유인
                                intent = new Intent(Activity_Login.this, Activity_Register.class);
                                intent.putExtra("platformId", platformId);
                                intent.putExtra("userId", Objects.requireNonNull(user.getKakaoAccount()).getEmail());
                                intent.putExtra("userNickname", Objects.requireNonNull(user.getKakaoAccount().getProfile()).getNickname());
                                intent.putExtra("userName", Objects.requireNonNull(user.getKakaoAccount().getLegalName()));
                                intent.putExtra("cnd", 0);
                            }
                            loading.dismiss();
                            startActivity(intent);
                            finish();
                        } else {
                            Log.i("Activity_Login", "Kakao Login is not successful");
                            Toast.makeText(Activity_Login.this, "로그인 실패\n잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserInfoDTO> call, @NonNull Throwable t) {
                        loading.dismiss();
                        Toast.makeText(Activity_Login.this, "로그인 실패\n잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        Log.i("error msg : ", t.getLocalizedMessage());
                    }
                });

            } else {
                // 로그인이 안되었을 때
                loading.dismiss();
                Toast.makeText(this, "로그인을 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.i("로그인 실패", throwable.getLocalizedMessage());
            }
            return null;
        });
    }

    private void changeLayout() {
        Intent intent = new Intent(Activity_Login.this, Activity_Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(this, "로그인을 성공했습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        bs.onBackPressed("'뒤로가기'를 한번 더 누르면 종료됩니다.");
    }
}