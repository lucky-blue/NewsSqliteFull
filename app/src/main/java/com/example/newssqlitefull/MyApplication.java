package com.example.newssqlitefull;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import org.litepal.LitePal;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApplication extends Application {
    public static MyApplication Instance;
    public Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;
        LitePal.initialize(this);//初始化LitePal数据库
        initRetrofit();
    }

    private void initRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(createHttpLogInterceptor())
                .build();
        //构建Retrofit实例
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                //设置网络请求BaseUrl地址
                .baseUrl("https://api.jisuapi.com/")
                //设置数据解析器
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }


    private Activity mMainActivity;

    public Activity getMainActivity() {
        return mMainActivity;
    }

    public void setMainActivity(Activity mainActivity) {
        mMainActivity = mainActivity;
    }
    private HttpLoggingInterceptor createHttpLogInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(
                new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Log.i("HTTP", "" + message);
                    }
                });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }
}
