package com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.Fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jiangchuanfa.projectofmediaplayer.Activity.AudioPlayerActivity;
import com.example.jiangchuanfa.projectofmediaplayer.Adapter.LocalVideoAdapter;
import com.example.jiangchuanfa.projectofmediaplayer.DoMain.MediaItem;
import com.example.jiangchuanfa.projectofmediaplayer.MiddleBaseFragment.BaseFragment;
import com.example.jiangchuanfa.projectofmediaplayer.R;

import java.util.ArrayList;

/**
 * Created by crest on 2017/5/19.
 */

public class LocalAudioPager extends BaseFragment {
    private ListView lv;
    private TextView tv_nodata;
    private ArrayList<MediaItem> mediaItems;
    private LocalVideoAdapter adapter;

    //重写视图
    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_local_pager, null);
        lv = (ListView) view.findViewById(R.id.lv);
        tv_nodata = (TextView) view.findViewById(R.id.tv_nodata);
        //设置item的点击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //得到点击item对应的对象
//                MediaItem mediaItem = mediaItems.get(position);
//                Log.e("TAG", "mediaItem==" + mediaItem);
//
//                MediaItem item = adapter.getItem(position);
//                //Toast.makeText(context, ""+item.toString(), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(context,SystemVideoPlayerActivity.class);
//                intent.setDataAndType(Uri.parse(item.getData()),"video/*");


                Intent intent = new Intent(context, AudioPlayerActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("videolist",mediaItems);
//                Log.e("TAG","*****************mediaItems********************"+mediaItems);
                intent.putExtra("position",position);
//                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG", "LocalVideoPager-initData");
        getData();

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(mediaItems != null && mediaItems.size() >0){
                //有数据,隐藏一开始显示的页面
                tv_nodata.setVisibility(View.GONE);
                //设置适配器，让数据显示出来来
                adapter = new LocalVideoAdapter(context,mediaItems,false );
                lv.setAdapter(adapter);
            }else{
                //没有数据
                tv_nodata.setVisibility(View.VISIBLE);
            }
        }
    };

    private void getData() {
        new Thread() {
            public void run() {
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频在sdcard上的名称
                        MediaStore.Audio.Media.DURATION,//视频的时长
                        MediaStore.Audio.Media.SIZE,//视频文件的大小
                        MediaStore.Audio.Media.DATA,//视频播放地址
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        if(duration > 30*1000){
                            MediaItem mediaItem = new MediaItem(name, duration, size, data);
                            mediaItems.add(mediaItem);
                            //mediaItems.add(new MediaItem(name, duration, size, data));
                        }
                        handler.sendEmptyMessage(0);//使用handler发送消息设置适配器，显示数据
                    }
                    cursor.close();
                }
            }
        }.start();

    }

}
