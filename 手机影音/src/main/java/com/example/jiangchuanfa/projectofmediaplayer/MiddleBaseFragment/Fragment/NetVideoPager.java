package com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.Fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.BaseFragment;

/**
 * Created by crest on 2017/5/19.
 */

public class NetVideoPager extends BaseFragment {
    private TextView textView;
    @Override
    public View initView() {
        Log.e("TAG","NetVideoPager-initView");
        textView =new TextView(context);
        textView.setTextSize(30);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    @Override
    public void initData() {
        Log.e("TAG","NetVideoPager-initData");
        textView.setText("网络视频内容");

    }
}
