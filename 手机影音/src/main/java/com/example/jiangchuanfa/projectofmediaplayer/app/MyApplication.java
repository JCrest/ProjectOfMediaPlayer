package com.example.jiangchuanfa.projectofmediaplayer.app;


import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Created by crest on 2017/5/22.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5928e7f4");

    }
}
