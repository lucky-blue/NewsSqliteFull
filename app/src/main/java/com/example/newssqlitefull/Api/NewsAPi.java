package com.example.newssqlitefull.Api;

import com.example.newssqlitefull.bean.BaseBean;
import com.example.newssqlitefull.bean.NewsList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsAPi {
    // @GET注解的作用:采用Get方法发送网络请求
    // getNews(...) = 接收网络请求数据的方法
    // 其中返回类型为Call<News>，News是接收数据的类（即上面定义的News类）
    // 如果想直接获得Responsebody中的内容，可以定义网络请求返回值为Call<ResponseBody>
    @GET("news/get")
    public Call<BaseBean<NewsList>> getNews(@Query("channel") String channel, @Query("start") int start, @Query("num") int num, @Query("appkey") String appkey);
}
