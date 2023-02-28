package com.example.wheremystore.api_zip;

import com.example.wheremystore.dto.StoreInfoDTO;
import com.example.wheremystore.dto.UserInfoDTO;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface UserInfoAPI {
    // select 영역
    @GET("user/info")
    Call<UserInfoDTO> getUserInfo (
            @Query("platformId") String platformId
    );
    @GET("user/id/check")
    Call<UserInfoDTO> checkId(
            @Query("userId") String userId
    );
    @GET("user/registerInfo") // 내가 등록한 글 혹은 즐겨찾기해둔 목록 가져옴
    Call<List<StoreInfoDTO>> getRegisterInfo(
            @Query("userId") String userId,
            @Query("cnd") int cnd
    );
    @GET("user/login")
    Call<UserInfoDTO> login(
            @Query("userId") String userId,
            @Query("userPw") String userPw
    );
    @GET("user/info/check/1")
    Call<UserInfoDTO> userInfoCheck1(
            @Query("userId") String userId,
            @Query("phone") String phone
    );
    @GET("user/info/check/2")
    Call<List<UserInfoDTO>> userInfoCheck2(
            @Query("userName") String userName,
            @Query("userPhone") String userPhone
    );



    // insert 영역
    @Multipart
    @POST("user/signup")
    Call<Integer> signUp (
            @PartMap HashMap<String, String > data
    );



    // update 영역
    @PUT("user/change/password")
    Call<Integer> passwordChange(
            @Query("userId") String userId,
            @Query("userPw") String userPw,
            @Query("userPhone") String userPhone
    );
}
