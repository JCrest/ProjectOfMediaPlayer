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
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
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
    private MaterialRefreshLayout materialRefreshLayout;
    private boolean isLoadMore = false;
    private List<MoveInfo.TrailersBean> datas;

    private SharedPreferences sp;//缓存数据用于打开在没网的情况下加载页面

    @Override
    public View initView() {
        sp = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
        View view = View.inflate(context, R.layout.fragment_net_video_pager, null);
        lv = (ListView) view.findViewById(R.id.lv);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        materialRefreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.refresh);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                isLoadMore = false;
                getDataFromNet();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                isLoadMore = true;
                getMoreData();
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MoveInfo.TrailersBean item = adapter.getItem(position);


                mediaItems = new ArrayList<>();
                for (int i = 0; i < datas.size(); i++) {
                    MediaItem mediaItem = new MediaItem();
                    mediaItem.setData(datas.get(i).getUrl());
                    mediaItem.setName(datas.get(i).getMovieName());
                    mediaItems.add(mediaItem);

                }


//                Intent intent = new Intent(context, SystemVideoPlayerActivity.class);
//                intent.setDataAndType(Uri.parse(item.getUrl()), "video/*");
//                startActivity(intent);
                Intent intent = new Intent(context, SystemVideoPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videolist", mediaItems);
                Log.e("TAG", "mediaItems++++++++++++++++++++++++++++++++++++" + mediaItems);
                intent.putExtra("position", position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    private void getMoreData() {
        final RequestParams request = new RequestParams("http://api.m.mtime.cn/PageSubArea/TrailerList.api");
        x.http().get(request, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                sp.edit().putString(uri, result).commit();
                Log.e("TAG", "xUtils联网成功==" + result);
                processData(result);
                Toast.makeText(context, "加载了50条数据", Toast.LENGTH_SHORT).show();
                materialRefreshLayout.finishRefreshLoadMore();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "xUtils联网失败==" + ex.getMessage());
                Toast.makeText(context, "无法加载更多请检查网络", Toast.LENGTH_SHORT).show();
                materialRefreshLayout.finishRefreshLoadMore();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
            }
        });
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
                sp.edit().putString(uri, result).commit();
                Log.e("TAG", "xUtils联网成功=" + result);
                processData(result);
                Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show();
                materialRefreshLayout.finishRefresh();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "xUtils联网失败==" + ex.getMessage());
                Toast.makeText(context, "刷新失败，请检查网络", Toast.LENGTH_SHORT).show();
                materialRefreshLayout.finishRefresh();
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    //加工数据
    private void processData(String json) {
        MoveInfo moveInfo = new Gson().fromJson(json, MoveInfo.class);
        //mediaItems = new ArrayList<>();

        if (!isLoadMore) {
            datas = moveInfo.getTrailers();
//            MediaItem mediaItem = new MediaItem();
//            for (int i = 0; i < datas.size(); i++) {
//
//                mediaItem.setData(datas.get(i).getUrl());
//                mediaItem.setName(datas.get(i).getMovieName());
//                mediaItems.add(mediaItem);
//                mediaItem = new MediaItem();
//                Log.e("TAG","mediaItems=========================="+mediaItems);
//
//            }
            if (datas != null && datas.size() > 0) {
                tv_nodata.setVisibility(View.GONE);
                //有数据-适配器
                adapter = new NetVideoAdapter(context, datas);
                lv.setAdapter(adapter);
            } else {
                tv_nodata.setVisibility(View.VISIBLE);
            }
        } else {
            List<MoveInfo.TrailersBean> trailersBeanList = moveInfo.getTrailers();
            Log.e("TAG", "**********************************************************************");
//            MediaItem mediaItem = new MediaItem();
//            for (int i = 0; i < trailersBeanList.size(); i++) {
//
//                mediaItem.setData(trailersBeanList.get(i).getUrl());
//                mediaItem.setName(trailersBeanList.get(i).getMovieName());
//                Log.e("TAG", "xUtils联网成功===================================" + trailersBeanList.get(i).getHightUrl());
//                mediaItem = new MediaItem();
//                mediaItems.add(mediaItem);
//            }
            datas.addAll(trailersBeanList);
            Log.e("TAG", "===========================================");
            adapter.notifyDataSetChanged();
        }
    }
}
