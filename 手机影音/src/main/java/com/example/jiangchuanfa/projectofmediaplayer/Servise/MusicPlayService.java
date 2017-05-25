package com.example.jiangchuanfa.projectofmediaplayer.Servise;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.jiangchuanfa.projectofmediaplayer.DoMain.MediaItem;
import com.example.jiangchuanfa.projectofmediaplayer.IMusicPlayService;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by crest on 2017/5/25.
 */

public class MusicPlayService extends Service {

    private IMusicPlayService.Stub stub = new IMusicPlayService.Stub() {
        MusicPlayService service = MusicPlayService.this;

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);

        }

        @Override
        public void start() throws RemoteException {
            service.start();

        }

        @Override
        public void pause() throws RemoteException {
            service.pause();

        }

        @Override
        public String artistName() throws RemoteException {
            return service.artistName();
        }

        @Override
        public String getAudioName() throws RemoteException {
            return service.getAudioName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);

        }

        @Override
        public void next() throws RemoteException {
            service.next();

        }

        @Override
        public void pre() throws RemoteException {
            service.pre();

        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mediaPlayer.isPlaying();
        }
    };
    private ArrayList<MediaItem> mediaItems;
    private MediaPlayer mediaPlayer;
    private int position;
    private MediaItem mediaItem;
    public static final String OPEN_COMPLETE = "com.example.jiangchuanfa.OPEN_COMPLETE";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG","MusicPlayService-------------------onCreate()");
        //加载数据列表，为甚不加载布局；因为service和activity不一样她没有布局，她是后台运行的没有布局
        getData();

    }

    private void getData() {
        new Thread() {
            public void run() {
                mediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//音频在sdcard上的名称
                        MediaStore.Audio.Media.DURATION,//音频的时长
                        MediaStore.Audio.Media.SIZE,//音频文件的大小
                        MediaStore.Audio.Media.DATA,//音频播放地址
                        MediaStore.Audio.Media.ARTIST,//艺术家
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        Log.e("TAG", "name==" + name + ",duration==" + duration + ",data===" + data + ",artist==" + artist);
                        if (duration > 30 * 1000) {
                            MediaItem mediaItem = new MediaItem(name, duration, size, data, artist);
                            mediaItems.add(mediaItem);
                            //mediaItems.add(new MediaItem(name, duration, size, data));
                        }

                    }
                    cursor.close();
                }
            }
        }.start();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    public void openAudio(int position) {
        this.position = position;
        if (mediaItems != null && mediaItems.size() > 0 && position < mediaItems.size()) {
            mediaItem = mediaItems.get(position);
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer = null;
            }
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(mediaItem.getData());
                //设置mediaPlayer的三个监听状态：准备好播放、播放出错、播放完成；
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                //在这里运用两种方法1、直接创建内部类2、new一个接口对比
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        next();//播放完成也是继续下一个
                    }
                });
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MusicPlayService.this, "音频文件没有加载完成", Toast.LENGTH_SHORT).show();
        }
    }

    public void start() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public String artistName() {
        return "";
    }

    public String getAudioName() {
        return "";
    }

    public String getAudioPath() {
        return "";
    }

    public int getDuration() {
        return 0;
    }

    public int getCurrentPosition() {
        return 0;
    }

    public void seekTo(int position) {
    }

    public void next() {
    }

    public void pre() {
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            notifyChange(OPEN_COMPLETE );
            start();
        }
    }
    //发送广播
    private void notifyChange(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();//如果播放出错直接播放下一个
            return true;
        }
    }
}
