package com.example.newssqlitefull.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newssqlitefull.Api.NewsAPi;
import com.example.newssqlitefull.MyApplication;
import com.example.newssqlitefull.R;
import com.example.newssqlitefull.bean.BaseBean;
import com.example.newssqlitefull.bean.News;
import com.example.newssqlitefull.bean.NewsList;
import com.example.newssqlitefull.util.MySqliteOpenHelper;
import com.example.newssqlitefull.util.SPUtils;
import com.example.newssqlitefull.util.StatusBarUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 开屏页面
 */
public class OpeningActivity extends AppCompatActivity {
    private Activity myActivity;
    MySqliteOpenHelper helper = null;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String[] state = {"0", "1", "2", "3", "4", "5", "6", "7"};
    private String[] title = {"头条", "科技", "娱乐", "体育", "军事", "汽车", "健康", "财经"};
    private Boolean isFirst;
    private SQLiteDatabase db;
    private int apiConnectNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myActivity = this;
        helper = new MySqliteOpenHelper(this);
        //设置页面布局
        setContentView(R.layout.activity_opening);
        isFirst = (Boolean) SPUtils.get(myActivity, SPUtils.IF_FIRST, true);
        db = helper.getWritableDatabase();
        try {
            initView();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        if (isFirst)
            initNews();
    }

    private void initNews() {
        //创建网络请求接口对象实例
        NewsAPi api = MyApplication.Instance.retrofit.create(NewsAPi.class);
        //对发送请求进行封装
        for (int i = 0; i < title.length; i++) {
            Call<BaseBean<NewsList>> dataCall = api.getNews(title[i], 0, 40, "9157f190e74a24ae");
            //异步请求
            dataCall.enqueue(new Callback<BaseBean<NewsList>>() {
                //请求成功回调
                @Override
                public void onResponse(Call<BaseBean<NewsList>> call, Response<BaseBean<NewsList>> response) {
                    apiConnectNum++;
                    if (response.isSuccessful() && response.body().getStatus() == 0) {
                        new Thread(() -> {
                            addNewsBean(response);
                            Log.d("TAG", "onResponse: " + apiConnectNum + "-----" + title.length);
                        }).start();
                    }
                }

                //请求失败回调
                @Override
                public void onFailure(Call<BaseBean<NewsList>> call, Throwable t) {
                    apiConnectNum++;
                }
            });

        }
    }

    private synchronized void addNewsBean(Response<BaseBean<NewsList>> response) {
        for (int j = 0; j < title.length; j++) {
            if (response.body().getResult().getChannel().equals(title[j])) {
                int typeId = Integer.parseInt(state[j]);
                for (int k = response.body().getResult().getList().size() - 1; k >= 0; k--) {
                    String title = response.body().getResult().getList().get(k).getTitle();
                    String img = response.body().getResult().getList().get(k).getPic();
                    String content = response.body().getResult().getList().get(k).getContent();
                    String issuer = response.body().getResult().getList().get(k).getSrc();
                    String date = response.body().getResult().getList().get(k).getTime();
                    String insertSql = "insert into news(typeId,title,img,content,issuer,date) values(?,?,?,?,?,?)";
                    db.execSQL(insertSql, new Object[]{typeId, title, img, content, issuer, date});
                }
            }
        }
    }

    private void initView() throws IOException, JSONException {
        StatusBarUtil.setStatusBar(myActivity, true);//设置当前界面是否是全屏模式（状态栏）
        StatusBarUtil.setStatusBarLightMode(myActivity, true);//状态栏文字颜色
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
                    finish();
                    return;
                }
                Integer userId = (Integer) SPUtils.get(myActivity, SPUtils.USER_ID, 0);
                if (isFirst) {//第一次进来  初始化本地数据
                    SPUtils.put(myActivity, SPUtils.IF_FIRST, false);//第一次
                    //初始化数据
                    //获取json数据
                    String rewardJson = "";
                    String rewardJsonLine;
                    //assets文件夹下db.json文件的路径->打开db.json文件
                    BufferedReader bufferedReader = null;
                    try {
                        bufferedReader = new BufferedReader(new InputStreamReader(myActivity.getAssets().open("db.json")));
                        while (true) {
                            if (!((rewardJsonLine = bufferedReader.readLine()) != null)) break;
                            rewardJson += rewardJsonLine;
                        }
                        JSONObject jsonObject = new JSONObject(rewardJson);
                        JSONArray newsList = jsonObject.getJSONArray("news");//获得新闻列表
                        //把物品列表保存到本地
                        for (int i = 0, length = newsList.length(); i < length; i++) {//初始化新闻
                            JSONObject o = newsList.getJSONObject(i);
                            int typeId = o.getInt("typeId");
                            String title = o.getString("title");
                            String img = o.getString("img");
                            String content = o.getString("content");
                            String issuer = o.getString("issuer");
//                            String date = sf.format(new Date());
                            String date = o.getString("date");
                            String insertSql = "insert into news(typeId,title,img,content,issuer,date) values(?,?,?,?,?,?)";
                            db.execSQL(insertSql, new Object[]{typeId, title, img, content, issuer, date});
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                //两秒后跳转到主页面
                Intent intent = new Intent();
                if (userId > 0) {//已登录
                    intent.setClass(OpeningActivity.this, MainActivity.class);
                } else {
                    intent.setClass(OpeningActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 3000);
    }


    @Override
    public void onBackPressed() {

    }
}
