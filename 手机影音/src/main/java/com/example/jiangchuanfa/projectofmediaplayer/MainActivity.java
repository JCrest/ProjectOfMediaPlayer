package com.example.jiangchuanfa.projectofmediaplayer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.Fragment.LocalAudioPager;
import com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.Fragment.LocalVideoPager;
import com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.Fragment.NetAudioPager;
import com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.Fragment.NetVideoPager;

import java.util.ArrayList;

/**
 * Created by crest on 2017/5/19.
 *
 *
 */

public class MainActivity extends AppCompatActivity {

    private RadioGroup rg_main;
    private ArrayList<Fragment> fragments;//因为这四个控件具有相同的属性所以用集合来管理
    private int position;
    private Fragment tempFragment;
    private boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isGrantExternalRW(this);
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
            //根据位置得到要显示的
            Fragment currentFragment = fragments.get(position);
            addFragment(currentFragment);
        }
    }


    private void addFragment(Fragment currentFragment) {
        if (tempFragment != currentFragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();//如果缓存的于当前的不相等的话开启事务
            //判断当前的Fragment是否被添加过--没有被添加过
            if (!currentFragment.isAdded()) {
                //判断缓存是否为空--不为空
                if (tempFragment != null) {
                    //将缓存的隐藏起来
                    ft.hide(tempFragment);
                }
                //将当前的Fragment给添加进来
                ft.add(R.id.fl_content, currentFragment);
                //判断当前的Fragment是否被添加过--有被添加过
            } else {
                //判断缓存是否为空--不为空
                if (tempFragment != null) {
                    //将缓存的隐藏起来
                    ft.hide(tempFragment);
                }
                //将当前的给显示出来
                ft.show(currentFragment);
            }
            ft.commit();
            tempFragment = currentFragment;
        }
    }
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode ==KeyEvent.KEYCODE_BACK){
            if(position!= 0){
                rg_main.check(R.id.rb_local_video);
                return true;
            }else if(!isExit){
                Toast.makeText(MainActivity.this, "再按一次退出软件", Toast.LENGTH_SHORT).show();
                isExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);

                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


}
