package com.example.jiangchuanfa.projectofmediaplayer.TopView;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiangchuanfa.projectofmediaplayer.Activity.SearchActivity;
import com.example.jiangchuanfa.projectofmediaplayer.R;

/**
 * Created by crest on 2017/5/19.
 */

public class TitleBar extends LinearLayout implements View.OnClickListener {
    private Context context;
    private TextView tv_sousuo;
    private RelativeLayout rl_game;
    private ImageView iv_record;


    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_sousuo = (TextView) getChildAt(1);
        rl_game = (RelativeLayout) getChildAt(2);
        iv_record = (ImageView) getChildAt(3);

        //设置点击事件
        tv_sousuo.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.tv_sousuo:
                Intent intent = new Intent(context,SearchActivity.class);
                context.startActivity(intent);
                break;
            case  R.id.rl_game:
                Toast.makeText(context, "游戏", Toast.LENGTH_SHORT).show();
                break;
            case  R.id.iv_record:
                Toast.makeText(context, "记录", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
