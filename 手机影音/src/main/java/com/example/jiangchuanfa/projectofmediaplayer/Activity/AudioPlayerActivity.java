package com.example.jiangchuanfa.projectofmediaplayer.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
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

import com.example.jiangchuanfa.projectofmediaplayer.IMusicPlayService;
import com.example.jiangchuanfa.projectofmediaplayer.R;
import com.example.jiangchuanfa.projectofmediaplayer.Servise.MusicPlayService;
import com.example.jiangchuanfa.projectofmediaplayer.Utils.Utils;

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

    private Utils utils;

    private final static int PROGRESS = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
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
                    service.openAudio(position);
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

        btnPlaymode.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnLyrics.setOnClickListener(this);
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
        } else if (v == btnPre) {
            // Handle clicks for btnPre
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
        } else if (v == btnLyrics) {
            // Handle clicks for btnLyrics
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

    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setViewData();

        }
    }

    private void setViewData() {
        try {
            tvArtist.setText(service.getArtistName());
            tvAudioName.setText(service.getAudioName());
            int duration = service.getDuration();
            seekbarAudio.setMax(duration);
            Log.e("TAG", "----------duration----------------" + duration);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessage(0);


    }

    private void getData() {
        position = getIntent().getIntExtra("position", 0);

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
        super.onDestroy();
    }

    private void startAndBindService() {
        Intent intent = new Intent(this, MusicPlayService.class);
//        intent.setAction("com.example.jiangchuanfa.projectofmediaplayer.Servise.MUSICPLAYSERVICE");
        bindService(intent, conon, BIND_AUTO_CREATE);
        startService(intent);//bind和start方法一起写目的防止多次启动服务
    }
}
