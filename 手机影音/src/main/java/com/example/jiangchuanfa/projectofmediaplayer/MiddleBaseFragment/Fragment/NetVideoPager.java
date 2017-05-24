package com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jiangchuanfa.projectofmediaplayer.Activity.SystemVideoPlayerActivity;
import com.example.jiangchuanfa.projectofmediaplayer.Adapter.NetVideoAdapter;
import com.example.jiangchuanfa.projectofmediaplayer.DoMain.MediaItem;
import com.example.jiangchuanfa.projectofmediaplayer.DoMain.MoveInfo;
import com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.BaseFragment;
import com.example.jiangchuanfa.projectofmediaplayer.R;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by crest on 2017/5/19.
 */

public class NetVideoPager extends BaseFragment {

    public static final String uri = "http://api.m.mtime.cn/PageSubArea/TrailerList.api";//默认网址链接

    private ArrayList<MediaItem> mediaItems;

    private NetVideoAdapter adapter;

    private ListView lv;
    private TextView tv_nodata;

    private SharedPreferences sp;//缓存数据用于打开在没网的情况下加载页面

    @Override
    public View initView() {
        sp = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
        View view = View.inflate(context, R.layout.fragment_net_video_pager, null);
        lv = (ListView) view.findViewById(R.id.lv);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MoveInfo.TrailersBean item = adapter.getItem(position);

//                Intent intent = new Intent(context, SystemVideoPlayerActivity.class);
//                intent.setDataAndType(Uri.parse(item.getUrl()), "video/*");
//                startActivity(intent);
                Intent intent = new Intent(context, SystemVideoPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videolist", mediaItems);
                intent.putExtra("position", position);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        String savaJson = sp.getString(uri, "");//注意：这里的空字符串并不等于空，空是空、空字符串是空字符串
        if (!TextUtils.isEmpty(savaJson)) {
            processData(savaJson);//解析已经存在内存中的缓存数据；
        }


        getDataFromNet();
    }

    private void getDataFromNet() {
        final RequestParams request = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                sp.edit().putString(uri,result).commit();
                Log.e("TAG", "xUtils联网成功==" + result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "xUtils联网失败==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }
//    private String name;
//    private long duration;
//    private long size;
//    private String data;


    private void processData(String json) {
        MoveInfo moveInfo = new Gson().fromJson(json, MoveInfo.class);
        List<MoveInfo.TrailersBean> datas = moveInfo.getTrailers();
        Log.e("TAG", "xUtils联网成功==" + datas.size());
        mediaItems = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            MediaItem mediaItem = new MediaItem();

            // mediaItem.setDuration(datas.get(i).getVideoLength());
            //mediaItem.setSize(datas.get(i).getId());
            mediaItem.setData(datas.get(i).getUrl());
            mediaItem.setName(datas.get(i).getMovieName());
            //mediaItems = (List<MediaItem>) new MediaItem(name,duration,size,data);
            Log.e("TAG", "xUtils联网成功==" + datas.get(i).getHightUrl());
            mediaItems.add(mediaItem);
        }
        Log.e("TAG", "xUtils联网成功==" + datas);
        if (datas != null && datas.size() > 0) {
            tv_nodata.setVisibility(View.GONE);
            //有数据-适配器
            adapter = new NetVideoAdapter(context, datas);
            lv.setAdapter(adapter);
        } else {
            tv_nodata.setVisibility(View.VISIBLE);
        }

    }
}
