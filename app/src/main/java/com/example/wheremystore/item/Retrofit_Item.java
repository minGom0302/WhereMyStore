package com.example.wheremystore.item;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit_Item {
    static String URL = "my server address";
    static Gson gson;
    static Retrofit retrofit;

    public static void setGsonAndRetrofit() {
        // 로그인할 때 retrofit 객체를 만들어 모든 곳에서 활용 > 내가 주로 통신할 메인 서버
        gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static  String getURL() { return URL; }

    // retrofit을 이용해 통신할 때 Null값일 경우 'End of input at line 1 column 1 path $' 라는 에러가 발생하는데 이를 해결하고자 하는 클레스 추가
    static class NullOnEmptyConverterFactory extends Converter.Factory {

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(@NonNull Type type, @NonNull Annotation[] annotations, Retrofit retrofit)
        {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return (Converter<ResponseBody, Object>) body -> {
                if (body.contentLength() == 0) {
                    return null;
                }
                return delegate.convert(body);
            };
        }
    }
}
