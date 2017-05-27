package com.example.jiangchuanfa.projectofmediaplayer.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.jiangchuanfa.projectofmediaplayer.CustomView.BaseVisualizerView;
import com.example.jiangchuanfa.projectofmediaplayer.CustomView.LyricShowView;
import com.example.jiangchuanfa.projectofmediaplayer.DoMain.Lyric;
import com.example.jiangchuanfa.projectofmediaplayer.DoMain.MediaItem;
import com.example.jiangchuanfa.projectofmediaplayer.IMusicPlayService;
import com.example.jiangchuanfa.projectofmediaplayer.R;
import com.example.jiangchuanfa.projectofmediaplayer.Servise.MusicPlayService;
import com.example.jiangchuanfa.projectofmediaplayer.Utils.LyricsUtils;
import com.example.jiangchuanfa.projectofmediaplayer.Utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import static com.example.jiangchuanfa.projectofmediaplayer.R.id.iv_icon;

/**
 * Created by crest on 2017/5/25.
 */

public class AudioPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvAudioName;
    private LinearLayout llBottom;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnPlaymode;
    private Button btnPre;
    private Button btnStartPause;
    private Button btnNext;
    private Button btnLyrics;
    private IMusicPlayService service;
    private int position;
    private MyReceiver receiver;
    private LyricShowView lyric_show_view;


    private Utils utils;

    private final static int PROGRESS = 0;
    private static final int SHOW_LYRIC = 1;
    private boolean notification;

    private BaseVisualizerView visualizerview;
    private Visualizer mVisualizer;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_LYRIC:
                    try {
                        int currentPosition = service.getCurrentPosition();
                        lyric_show_view.setNextShowLyric(currentPosition);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    removeMessages(SHOW_LYRIC);
                    sendEmptyMessage(SHOW_LYRIC);
                    break;
                case 0:
                    try {
                        int currentPosition = service.getCurrentPosition();
                        seekbarAudio.setProgress(currentPosition);

                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getDuration()));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    removeMessages(0);
                    sendEmptyMessageDelayed(0, 1000);


                    break;
            }

        }
    };


    private ServiceConnection conon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBind) {
            service = IMusicPlayService.Stub.asInterface(iBind);
            if (service != null) {
                try {
                    if (!notification) {
                        service.openAudio(position);
                    }
//                    if(notification){
//
//                    } else {
//                        service.openAudio(position);
//                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-25 16:26:55 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_audio_player);
        //初始化控件
        ivIcon = (ImageView) findViewById(iv_icon);
        ivIcon.setBackgroundResource(R.drawable.animation_bg);
        AnimationDrawable background = (AnimationDrawable) ivIcon.getBackground();
        background.start();
        ivIcon = (ImageView) findViewById(iv_icon);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvAudioName = (TextView) findViewById(R.id.tv_audioName);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        btnPlaymode = (Button) findViewById(R.id.btn_playmode);
        btnPre = (Button) findViewById(R.id.btn_pre);
        btnStartPause = (Button) findViewById(R.id.btn_start_pause);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnLyrics = (Button) findViewById(R.id.btn_lyrics);
        lyric_show_view = (LyricShowView) findViewById(R.id.lyric_show_view);
        visualizerview = (BaseVisualizerView)findViewById(R.id.visualizerview);

        btnPlaymode.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnLyrics.setOnClickListener(this);

        //设置seekbar状态改变的监听事件，以内部类接口的方式实现，与视频的设置形成对比可以比较两种方法优缺点
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());


    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }


    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-05-25 16:26:55 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnPlaymode) {
            // Handle clicks for btnPlaymode
            setPlayMode();

        } else if (v == btnPre) {
            // Handle clicks for btnPre
            try {
                service.pre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (v == btnStartPause) {
            // Handle clicks for btnStartPause
            try {
                if (service.isPlaying()) {
                    service.pause();
                    btnStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);

                } else {
                    service.start();
                    btnStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } else if (v == btnNext) {
            // Handle clicks for btnNext
            try {
                service.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (v == btnLyrics) {
            // Handle clicks for btnLyrics
        }
    }

    private void setPlayMode() {
        try {
            int playmode = service.getPlaymode();
            if (playmode == MusicPlayService.REPEAT_NORMAL) {
                playmode = MusicPlayService.REPEAT_SINGLE;

            } else if (playmode == MusicPlayService.REPEAT_SINGLE) {
                playmode = MusicPlayService.REPEAT_ALL;
            } else if (playmode == MusicPlayService.REPEAT_ALL) {
                playmode = MusicPlayService.REPEAT_NORMAL;
            }
            service.setPlaymode(playmode);
            setButtonImage();


        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void setButtonImage() {
        try {
            int playmode = service.getPlaymode();
            if (playmode == MusicPlayService.REPEAT_NORMAL) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_normal_selector);
            } else if (playmode == MusicPlayService.REPEAT_SINGLE) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_single_selector);
            } else if (playmode == MusicPlayService.REPEAT_ALL) {
                btnPlaymode.setBackgroundResource(R.drawable.btn_playmode_all_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();

        findViews();
        getData();
        startAndBindService();
    }

    private void initData() {
        //注册光播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayService.OPEN_COMPLETE);
        registerReceiver(receiver, intentFilter);
        utils = new Utils();

        //注册EventBus
        EventBus.getDefault().register(this);

    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setViewData(null);

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setViewData(MediaItem mediaItem) {
        try {
            tvArtist.setText(service.getArtistName());
            tvAudioName.setText(service.getAudioName());
            int duration = service.getDuration();
            seekbarAudio.setMax(duration);


            //解析歌词
            //1.得到歌词所在路径
            String audioPath = service.getAudioPath();//mnt/sdcard/audio/beijingbeijing.mp3

            String lyricPath = audioPath.substring(0, audioPath.lastIndexOf("."));//mnt/sdcard/audio/beijingbeijing
            File file = new File(lyricPath + ".lrc");
            if (!file.exists()) {
                file = new File(lyricPath + ".txt");
            }
            LyricsUtils lyricsUtils = new LyricsUtils();
            lyricsUtils.readFile(file);

            //2.传入解析歌词的工具类
            ArrayList<Lyric> lyrics = lyricsUtils.getLyrics();
            lyric_show_view.setLyrics(lyrics);

            //3.如果有歌词，就歌词同步

            if (lyricsUtils.isLyric()) {
                handler.sendEmptyMessage(SHOW_LYRIC);
            }


            Log.e("TAG", "----------duration----------------" + duration);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessage(0);
        //显示音乐频谱
        setupVisualizerFxAndUi();


    }

    private void setupVisualizerFxAndUi() {
        int audioSessionid = 0;
        try {
            audioSessionid = service.getAudioSessionId();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("audioSessionid==" + audioSessionid);
        mVisualizer = new Visualizer(audioSessionid);
        // 参数内必须是2的位数
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        // 设置允许波形表示，并且捕获它
        visualizerview.setVisualizer(mVisualizer);
        mVisualizer.setEnabled(true);
    }

    private void getData() {
        notification = getIntent().getBooleanExtra("notification", false);
        if (!notification) {
            position = getIntent().getIntExtra("position", 0);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            mVisualizer.release();
        }
    }

    @Override
    protected void onDestroy() {
        //解绑服务
        if (conon != null) {
            unbindService(conon);
            conon = null;
        }
        //取消注册广播
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        EventBus.getDefault().unregister(this);
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    private void startAndBindService() {
        Intent intent = new Intent(this, MusicPlayService.class);
//        intent.setAction("com.example.jiangchuanfa.projectofmediaplayer.Servise.MUSICPLAYSERVICE");
        bindService(intent, conon, BIND_AUTO_CREATE);
        startService(intent);//bind和start方法一起写目的防止多次启动服务
    }
}
