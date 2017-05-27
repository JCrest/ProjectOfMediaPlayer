package com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.Fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.BaseFragment;

/**
 * Created by crest on 2017/5/19.
 *
 *
 */

public class NetAudioPager extends BaseFragment {

    private TextView textView;

    @Override
    public View initView() {
        Log.e("TAG","NetAudioPager-initView");
        textView = new TextView(context);
        textView.setTextColor(Color.BLUE);
        textView.setTextSize(30);
        textView.setGravity(Gravity.CENTER);
        return this.textView;
    }

    @Override
    public void initData() {
        Log.e("TAG","NetAudioPager-initData");
        textView.setText("网络音频内容");
    }
}
