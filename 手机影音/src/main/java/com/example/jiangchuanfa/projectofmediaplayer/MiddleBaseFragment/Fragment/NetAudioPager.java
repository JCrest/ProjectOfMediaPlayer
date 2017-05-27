package com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.Fragment;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.BaseFragment;
import com.example.jiangchuanfa.projectofmediaplayer.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by crest on 2017/5/19.
 *
 *
 */

public class NetAudioPager extends BaseFragment {

    private static final String TAG = NetAudioPager.class.getSimpleName();
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.progressbar)
    ProgressBar progressbar;
    @Bind(R.id.tv_nomedia)
    TextView tvNomedia;

    @Override
    public View initView() {
        Log.e(TAG, "网络音频UI被初始化了");
        View view = View.inflate(context, R.layout.fragment_net_audio, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        Log.e(TAG, "网络音频数据初始化了");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}


