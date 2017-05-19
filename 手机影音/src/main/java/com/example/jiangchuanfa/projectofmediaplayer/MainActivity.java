package com.example.jiangchuanfa.projectofmediaplayer;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.Fragment.LocalAudioPager;
import com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.Fragment.LocalVideoPager;
import com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.Fragment.NetAudioPager;
import com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.Fragment.NetVideoPager;

import java.util.ArrayList;

/**
 * Created by crest on 2017/5/19.
 */

public class MainActivity extends AppCompatActivity {

    private RadioGroup rg_main;
    private ArrayList<Fragment> fragments;//因为这四个控件具有相同的属性所以用集合来管理
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
        rg_main = (RadioGroup) findViewById(R.id.rg_main);
        initFragment();
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_main.check(R.id.rb_local_video);//这个语句必须放在点击事件的下方，否则刚打开软件的时候不能不能显示内容
    }


    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new LocalVideoPager());
        fragments.add(new LocalAudioPager());
        fragments.add(new NetAudioPager());
        fragments.add(new NetVideoPager());


    }

    private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {


        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.rb_local_video:
                    position = 0;
                    break;
                case R.id.rb_local_audio:
                    position = 1;
                    break;
                case R.id.rb_net_audio:
                    position = 2;
                    break;
                case R.id.rb_net_video:
                    position = 3;
                    break;
            }
            addFragment();
        }
    }


    private void addFragment() {
        //根据位置得到BaseFragment的视图
        Fragment baseFragment = fragments.get(position);
        //得到FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        //开启事务
        FragmentTransaction ft = fm.beginTransaction();
        //替换内容
        ft.replace(R.id.fl_content, baseFragment);
        //提交事务
        ft.commit();
    }
}
