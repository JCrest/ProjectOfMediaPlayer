package com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.Fragment;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.BaseFragment;

/**
 * Created by crest on 2017/5/19.
 */

public class LocalVideoPager extends BaseFragment {
    private TextView textView;

    //重写视图
    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setTextSize(30);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.GREEN);
        return this.textView;
    }

    @Override
    public void initData() {
        textView.setText("本地视频的内容");

    }
}
