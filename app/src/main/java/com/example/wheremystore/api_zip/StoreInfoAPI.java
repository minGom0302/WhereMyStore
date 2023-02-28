package com.example.wheremystore.api_zip;

import com.example.wheremystore.dto.ImgDTO;
import com.example.wheremystore.dto.StoreInfoDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface StoreInfoAPI {
    // select 영역
    @GET("store/markers")
    Call<List<StoreInfoDTO>> getMarker(
            @Query("lat") String lat,
            @Query("lng") String lng,
            @Query("distance") String distance,
            @Query("option") String option
    );
    @GET("store/info")
    Call<StoreInfoDTO> getStoreInfo(
            @Query("seq") int seq,
            @Query("userId") String userId
    );
    @GET("store/img/info")
    Call<List<ImgDTO>> getImgInfo(
            @Query("storeSeq") int storeSeq
    );


    // insert 영역
    @Multipart
    @POST("store/register")
    Call<Integer> register (
            @PartMap HashMap<String, String > data,
            @Part ArrayList<MultipartBody.Part> files
    );
    @POST("store/register/bookmark")
    Call<Integer> registerBookmark(
            @Query("seq") int seq,
            @Query("userId") String userId
    );



    // delete 영역
    @DELETE("store/delete")
    Call<Integer> deleteStore(
        @Query("seq") int seq
    );
    @DELETE("store/delete/bookmark")
    Call<Integer> deleteBookmark(
        @Query("seq") int seq
    );
}
