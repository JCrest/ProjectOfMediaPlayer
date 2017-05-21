package com.example.jiangchuanfa.projectofmediaplayer.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;


/**
 * Created by crest on 2017/5/21.
 */

public class VideoView extends android.widget.VideoView {
    //构造方法选择第二个
    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //重写测量方法
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //保存测量的结果  this method must be called by {@link #onMeasure(int, int)} to store the measured width and measured height.
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    //设置视频的宽和高
    public void setVideoSize(int width, int height) {
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;
        setLayoutParams(params);
    }


}
