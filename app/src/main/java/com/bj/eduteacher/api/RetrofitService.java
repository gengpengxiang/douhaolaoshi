package com.bj.eduteacher.api;


import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;

/**
 * Created by Administrator on 2018/5/10 0010.
 */

public class RetrofitService {

    private static Converter.Factory fastJsonConverterFactory = FastJsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJava2CallAdapterFactory.create();
    static RetrofitApi retrofitApi;

    public static OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true);
    public static OkHttpClient client = builder.build();

    public static RetrofitApi getRetrofitApi() {
        if (retrofitApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(fastJsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            retrofitApi = retrofit.create(RetrofitApi.class);
        }
        return retrofitApi;
    }

}
