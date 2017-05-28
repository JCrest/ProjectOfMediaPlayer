package com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.Fragment;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jiangchuanfa.projectofmediaplayer.Adapter.NetAudioAdapter;
import com.example.jiangchuanfa.projectofmediaplayer.DoMain.NetAudioInfo;
import com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.BaseFragment;
import com.example.jiangchuanfa.projectofmediaplayer.R;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by crest on 2017/5/19.
 */

public class NetAudioPager extends BaseFragment {

    private static final String TAG = NetAudioPager.class.getSimpleName();
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.progressbar)
    ProgressBar progressbar;
    @Bind(R.id.tv_nomedia)
    TextView tvNomedia;
    private List<NetAudioInfo.ListBean> listDatas;
    private NetAudioAdapter myAdapter;

    @Override
    public View initView() {
        Log.e(TAG, "网络音频UI被初始化了");
        View view = View.inflate(context, R.layout.fragment_net_audio, null);
        ButterKnife.bind(this, view);

        getDataFromNet();
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        Log.e(TAG, "网络音频数据初始化了");


    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams("http://s.budejie.com/topic/list/jingxuan/1/budejie-android-6.2.8/0-20.json?market=baidu&udid=863425026599592&appname=baisibudejie&os=4.2.2&client=android&visiting=&mac=98%3A6c%3Af5%3A4b%3A72%3A6d&ver=6.2.8");
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "网络音乐请求成功" + result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "网络音乐请求失败" + ex.getMessage());

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }

    private void processData(String json) {
        NetAudioInfo netAudioInfo = new Gson().fromJson(json, NetAudioInfo.class);
        listDatas = netAudioInfo.getList();
        Log.e("TAG","解决成功=="+listDatas.get(0).getText());

        if(listDatas != null && listDatas.size() >0){
            //有视频
            tvNomedia.setVisibility(View.GONE);
            //设置适配器
            myAdapter = new NetAudioAdapter(context,listDatas);
            listview.setAdapter(myAdapter);
        }else{
            //没有视频
            tvNomedia.setVisibility(View.VISIBLE);
        }

        progressbar.setVisibility(View.GONE);


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}


