package com.example.newssqlitefull.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.newssqlitefull.MyApplication;
import com.example.newssqlitefull.R;
import com.example.newssqlitefull.enums.UserTypeEnum;
import com.example.newssqlitefull.ui.fragment.NewsFragment;
import com.example.newssqlitefull.ui.fragment.UserFragment;
import com.example.newssqlitefull.ui.fragment.UserManageFragment;
import com.example.newssqlitefull.util.SPUtils;
import com.example.newssqlitefull.widget.ActionBar;


/**
 * 主页面
 */
public class MainActivity extends Activity {
    private ActionBar mActionBar;//标题栏
    private Activity myActivity;
    private LinearLayout llContent;
    private RadioButton rbNews;
    private RadioButton rbUserManage;
    private RadioButton rbUser;
    private Fragment[] fragments = new Fragment[]{null, null,null};//存放Fragment
    private Integer userType;//是否管理员
    private String mAccount;//账号

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myActivity = this;
        setContentView(R.layout.activity_main);
        MyApplication.Instance.setMainActivity(myActivity);
        llContent =  (LinearLayout) findViewById(R.id.ll_main_content);
        rbNews = (RadioButton) findViewById(R.id.rb_main_news);
        rbUserManage = (RadioButton) findViewById(R.id.rb_main_setting);
        rbUser = (RadioButton) findViewById(R.id.rb_main_user);
        mActionBar = (ActionBar) findViewById(R.id.myActionBar);
        //侧滑菜单
        mActionBar.setData(myActivity,"新闻", 0, 0, 0, getResources().getColor(R.color.colorPrimary), new ActionBar.ActionBarClickListener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
            }
        });
        initView();
        setViewListener();
    }

    private void setViewListener() {
        rbNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setTitle("新闻");
                switchFragment(0);
            }
        });
        rbUserManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setTitle("用户管理");
                switchFragment(1);
            }
        });
        rbUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(mAccount)) {////未登录,跳转到登录页面
                    MyApplication.Instance.getMainActivity().finish();
                    startActivity(new Intent(myActivity, LoginActivity.class));
                }else {//已经登录
                    mActionBar.setTitle("我的");
                    switchFragment(userType.intValue() == UserTypeEnum.Admin.getCode() ?2:1);
                }
            }
        });
    }

    private void initView() {
        userType = (Integer) SPUtils.get(myActivity,SPUtils.USER_TYPE,0);
        //设置导航栏图标样式
        Drawable iconNews=getResources().getDrawable(R.drawable.selector_main_rb_news);//设置主页图标样式
        iconNews.setBounds(0,0,68,68);//设置图标边距 大小
        rbNews.setCompoundDrawables(null,iconNews,null,null);//设置图标位置
        rbNews.setCompoundDrawablePadding(5);//设置文字与图片的间距
        Drawable iconSetting=getResources().getDrawable(R.drawable.selector_main_rb_manage);//设置主页图标样式
        iconSetting.setBounds(0,0,60,60);//设置图标边距 大小
        rbUserManage.setCompoundDrawables(null,iconSetting,null,null);//设置图标位置
        rbUserManage.setCompoundDrawablePadding(5);//设置文字与图片的间距
        Drawable iconUser=getResources().getDrawable(R.drawable.selector_main_rb_user);//设置主页图标样式
        iconUser.setBounds(0,0,55,55);//设置图标边距 大小
        rbUser.setCompoundDrawables(null,iconUser,null,null);//设置图标位置
        rbUser.setCompoundDrawablePadding(5);//设置文字与图片的间距
        rbUserManage.setVisibility(userType.intValue() == UserTypeEnum.Admin.getCode() ? View.VISIBLE: View.GONE);
        switchFragment(0);
        rbNews.setChecked(true);
    }
    /**
     * 方法 - 切换Fragment
     *
     * @param fragmentIndex 要显示Fragment的索引
     */
    private void switchFragment(int fragmentIndex) {
        //在Activity中显示Fragment
        //1、获取Fragment管理器 FragmentManager
        FragmentManager fragmentManager = this.getFragmentManager();
        //2、开启fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //懒加载 - 如果需要显示的Fragment为null，就new。并添加到Fragment事务中
        if (fragments[fragmentIndex] == null) {
            if (userType.intValue() == UserTypeEnum.Admin.getCode()){//管理员
                switch (fragmentIndex) {
                    case 0://NewsFragment
                        fragments[fragmentIndex] = new NewsFragment();
                        break;
                    case 1://UserManageFragment
                        fragments[fragmentIndex] = new UserManageFragment();
                        break;
                    case 2://UserFragment
                        fragments[fragmentIndex] = new UserFragment();
                        break;
                }
            }else {
                switch (fragmentIndex) {
                    case 0://NewsFragment
                        fragments[fragmentIndex] = new NewsFragment();
                        break;
                    case 1://UserFragment
                        fragments[fragmentIndex] = new UserFragment();
                        break;
                }
            }

            //==添加Fragment对象到Fragment事务中
            //参数：显示Fragment的容器的ID，Fragment对象
            transaction.add(R.id.ll_main_content, fragments[fragmentIndex]);
        }

        //隐藏其他的Fragment
        for (int i = 0; i < fragments.length; i++) {
            if (fragmentIndex != i && fragments[i] != null) {
                //隐藏指定的Fragment
                transaction.hide(fragments[i]);
            }
        }
        //4、显示Fragment
        transaction.show(fragments[fragmentIndex]);

        //5、提交事务
        transaction.commit();
    }
    /**
     * 双击退出
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
        }

        return false;
    }

    private long time = 0;

    public void exit() {
        if (System.currentTimeMillis() - time > 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(myActivity,"再点击一次退出应用程序", Toast.LENGTH_LONG).show();
        } else {
            finish();
        }
    }
}
