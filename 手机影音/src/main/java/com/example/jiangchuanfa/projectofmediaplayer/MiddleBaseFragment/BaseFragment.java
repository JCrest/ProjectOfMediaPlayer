package com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by crest on 2017/5/19.
 */

public abstract class BaseFragment extends Fragment {
    public Context context;

    /**
     * 当Fragment被创建的时候回调
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e("TAG","BaseFragment-onCreate");
        super.onCreate(savedInstanceState);
        context = getContext();//MainActiviry
    }

    /**
     * 当创建视图的时候回调
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("TAG","BaseFragment-onCreateView");
        return initView();

    }

    /**
     * 由子类实现的抽象方法，写不同的布局，不同的效果
     *
     * @return
     */
    public abstract View initView();//抽象的方法只能由public来修饰

    /**
     * 当依附的Activity被创建的时候回调
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("TAG","BaseFragment-onActivityCreated");
        initData();//在得到视图的基础上，设置数据
    }

    public void initData() {
    }


}
