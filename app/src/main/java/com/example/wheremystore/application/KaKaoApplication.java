package com.example.wheremystore.application;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KaKaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, "kakaoApplication key");
    }
}
