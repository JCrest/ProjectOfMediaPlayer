package com.example.jiangchuanfa.projectofmediaplayer.Servise;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.jiangchuanfa.projectofmediaplayer.Activity.AudioPlayerActivity;
import com.example.jiangchuanfa.projectofmediaplayer.DoMain.MediaItem;
import com.example.jiangchuanfa.projectofmediaplayer.IMusicPlayService;
import com.example.jiangchuanfa.projectofmediaplayer.R;

import org.greenrobot.eventbus.EventBus;

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

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void start() throws RemoteException {
            service.start();

        }

        @Override
        public void pause() throws RemoteException {
            service.pause();

        }

        @Override
        public String getArtistName() throws RemoteException {
            return service.getArtistName();
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

        @Override
        public int getPlaymode() throws RemoteException {
            return service.getPlaymode();
        }

        @Override
        public void setPlaymode(int playmode) throws RemoteException {
            service.setPlaymode(playmode);

        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return mediaPlayer.getAudioSessionId();
        }
    };
    private ArrayList<MediaItem> mediaItems;
    private MediaPlayer mediaPlayer;
    private int position;
    private MediaItem mediaItem;
    public static final String OPEN_COMPLETE = "com.example.jiangchuanfa.OPEN_COMPLETE";
    private NotificationManager nm;

    public final static int REPEAT_NORMAL = 1;//顺序播放
    public final static int REPEAT_SINGLE = 2;//单曲循环
    public final static int REPEAT_ALL = 3;//全部循环

    private int playmode = REPEAT_NORMAL;
    //添加字段是否是正常播放完成：false表示人为让其播放下一个；true 表示正常播放播放完成
    private boolean isCompletion = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG", "MusicPlayService-------------------onCreate()");
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
        Log.e("TAG", "-----mediaItems----" + mediaItems);
        Log.e("TAG", "-----mediaItems.size()----" + mediaItems.size());
        Log.e("TAG", "-----position----" + position);
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
                        isCompletion = true;
                        next();//播放完成也是继续下一个
                    }
                });
                mediaPlayer.prepareAsync();
                if (playmode == MusicPlayService.REPEAT_SINGLE) {
                    isCompletion = false;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MusicPlayService.this, "音频文件没有加载完成", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void start() {
        mediaPlayer.start();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra("notification", true);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notifation = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("手机影音")
                .setContentText("正在播放:" + getAudioName())
                .setContentIntent(pi)
                .build();
        nm.notify(1, notifation);
    }

    public void pause() {
        mediaPlayer.pause();
        nm.cancel(1);
    }

    public String getArtistName() {
        return mediaItem.getArtist();
    }

    public String getAudioName() {
        return mediaItem.getName();
    }

    public String getAudioPath() {
        return mediaItem.getData();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    public void next() {
        setNextPosition();
        openNextPosition();

    }

    private void openNextPosition() {
        int playmode = getPlaymode();
        if (playmode == MusicPlayService.REPEAT_NORMAL) {
            if (position < mediaItems.size()) {
                openAudio(position);
            } else {
                position = mediaItems.size() - 1;
            }
        } else if (playmode == MusicPlayService.REPEAT_SINGLE) {
            if (position < mediaItems.size()) {
                openAudio(position);
            } else {
                position = mediaItems.size() - 1;
//                position = 0;
//                openAudio(position);
            }
        } else if (playmode == MusicPlayService.REPEAT_ALL) {
            openAudio(position);
        }

    }

    private void setNextPosition() {
        int playmode = getPlaymode();
        if (playmode == MusicPlayService.REPEAT_NORMAL) {
            position++;
        } else if (playmode == MusicPlayService.REPEAT_SINGLE) {
            if (!isCompletion) {
                position++;
            }


        } else if (playmode == MusicPlayService.REPEAT_ALL) {
            if (position < mediaItems.size() - 1) {
                position++;
            } else {
                position = 0;
            }
        }
    }

    public void pre() {
    }

    public int getPlaymode() {
        return playmode;
    }

    public void setPlaymode(int playmode) {
        this.playmode = playmode;
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onPrepared(MediaPlayer mp) {
            //notifyChange(OPEN_COMPLETE);
            EventBus.getDefault().post(mediaItem);
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
