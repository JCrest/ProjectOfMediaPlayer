package com.example.jiangchuanfa.projectofmediaplayer.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiangchuanfa.projectofmediaplayer.CustomView.VitamioVideoView;
import com.example.jiangchuanfa.projectofmediaplayer.DoMain.MediaItem;
import com.example.jiangchuanfa.projectofmediaplayer.R;
import com.example.jiangchuanfa.projectofmediaplayer.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

/**
 * Created by crest on 2017/5/20.
 */

public class VitamioVideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PROGRESS = 0;
    private static final int HIDE_MEDIA_CONTROLLER = 1;
    private static final int SHOW_NET_SPEED = 2;


    private static final int DEFUALT_SCREEN = 0;
    private static final int FULL_SCREEN = 1;
    private VitamioVideoView vv;
    private Uri uri;
    private ArrayList<MediaItem> mediaItems;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnPre;
    private Button btnStartPause;
    private Button btnNext;
    private Button btnSwitchScreen;
    private Utils utils;
    private MyBroadCastReceiver receiver;
    private int position;

    //声明手势识别器
    private GestureDetector detector;
    //声明是否隐藏视频播放控制器
    private boolean isShowMediaController = false;
    //默认自适应播放（根据视频本身自动适应屏幕）
    private boolean isFullScreen = false;
    //屏幕的高
    private int screenHeight;
    //屏幕的宽
    private int screenWidth;
    private int videoWidth;
    private int videoHeight;
    //设置当前的音量
    private int currentVoice;
    private AudioManager am;
    //设置最大音量
    private int maxVoice;
    //是否静音
    private boolean isMute = false;
    private boolean isNetUri = true;
    private LinearLayout ll_buffering;
    private TextView tv_net_speed;
    private LinearLayout ll_loading;
    private TextView tv_loading_net_speed;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-05-20 11:01:51 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        //初始化解码器
        Vitamio.isInitialized(getApplicationContext());
        setContentView(R.layout.activity_vitamio_video_player);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnSwitchPlayer = (Button) findViewById(R.id.btn_switch_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnPre = (Button) findViewById(R.id.btn_pre);
        btnStartPause = (Button) findViewById(R.id.btn_start_pause);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnSwitchScreen = (Button) findViewById(R.id.btn_switch_screen);
        vv = (VitamioVideoView) findViewById(R.id.vv);
        ll_buffering = (LinearLayout) findViewById(R.id.ll_buffering);
        tv_net_speed = (TextView) findViewById(R.id.tv_net_speed);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_loading_net_speed = (TextView) findViewById(R.id.tv_loading_net_speed);


        btnVoice.setOnClickListener(this);
        btnSwitchPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSwitchScreen.setOnClickListener(this);
        //关联seekbar;设置最大音量、和当前音量（即实时音量）注：在此处这两行的代码是不可以调换的
        //音量的取值范围在（0-15）之间取值（姑且认为其为16进制的吧）
        seekbarVoice.setMax(maxVoice);
        seekbarVoice.setProgress(currentVoice);
        //发消息开始显示网速
        handler.sendEmptyMessage(SHOW_NET_SPEED);
    }


    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-05-20 11:01:51 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            isMute = !isMute;
            updateVoice(isMute);


            // Handle clicks for btnVoice
        } else if (v == btnSwitchPlayer) {
            // Handle clicks for btnSwitchPlayer
            switchPlayer();
        } else if (v == btnExit) {
            finish();
            // Handle clicks for btnExit
        } else if (v == btnPre) {
            setPreVedio();
            // Handle clicks for btnPre
        } else if (v == btnStartPause) {
            setStartOrPause();

            // Handle clicks for btnStartPause
        } else if (v == btnNext) {
            setNextVedio();
            // Handle clicks for btnNext
        } else if (v == btnSwitchScreen) {
            // Handle clicks for btnSwitchScreen
            if (isFullScreen) {

                setVideoType(DEFUALT_SCREEN);

            } else {
                setVideoType(FULL_SCREEN);
            }
        }
        //当点击按钮的时候移除消息，然后重新发消息重新计时，就不会出现在点击按钮的时候控制面板突然消失的尴尬
        handler.removeMessages(1);
        handler.sendEmptyMessageDelayed(1, 5000);
    }

    private void switchPlayer() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("如果当前为万能播放器播放，当播放有色块，播放质量不好，请切换到系统播放器播放")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startSystemPlayer();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void startSystemPlayer() {
        if(vv != null){
            vv.stopPlayback();
        }
        Intent intent = new Intent(this, SystemVideoPlayerActivity.class);
        if(mediaItems != null && mediaItems.size() >0){
            Bundle bunlder = new Bundle();
            bunlder.putSerializable("videolist",mediaItems);
            intent.putExtra("position",position);
            //放入Bundler
            intent.putExtras(bunlder);
        }else if(uri != null){
            intent.setData(uri);
        }
        startActivity(intent);
        finish();
    }

    private void updateVoice(boolean isMute) {
        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);//最后一个0、1表示是否启动系统调节
            seekbarVoice.setProgress(0);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
            seekbarVoice.setProgress(currentVoice);
        }

    }


    private void setVideoType(int videoTypt) {
        switch (videoTypt) {
            case 1:
                isFullScreen = true;
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_default_selector);
                vv.setVideoSize(screenWidth, screenHeight);
                break;
            case 0:
                isFullScreen = false;
                btnSwitchScreen.setBackgroundResource(R.drawable.btn_switch_screen_full_selector);
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoWidth;
                int width = screenWidth;
                int height = screenHeight;

                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                vv.setVideoSize(width, height);
                break;
        }

    }

    private void setStartOrPause() {
        if (vv.isPlaying()) {
            vv.pause();
            btnStartPause.setBackgroundResource(R.drawable.btn_start_selector);
        } else {
            vv.start();
            btnStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_NET_SPEED:
                    if (isNetUri) {
                        String netSpeed = utils.getNetSpeed(VitamioVideoPlayerActivity.this);
                        tv_loading_net_speed.setText("正在加载中...." + netSpeed);

                        tv_net_speed.setText("正在缓冲...." + netSpeed);
                        sendEmptyMessageDelayed(SHOW_NET_SPEED, 1000);
                    }
                    break;

                case PROGRESS:
                    //得到当前进度
                    int currentPosition = (int) vv.getCurrentPosition();
                    //让SeekBar进度更新
                    seekbarVideo.setProgress(currentPosition);

                    //设置文本当前的播放进度
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));
                    //得到系统时间
                    tvSystemTime.setText(getSystemTime());

                    //循环发消息
                    sendEmptyMessageDelayed(PROGRESS, 1000);

                    break;
                //处理消息隐藏控制面板
                case 1:
                    hideMediaController();
                    break;
            }
        }
    };

    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // utils = new Utils();
        initData();
        findViews();
        getData();


        setListener();
        setData();

    }

    private void setData() {

        if (mediaItems != null && mediaItems.size() > 0) {

            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            vv.setVideoPath(mediaItem.getData());

        } else if (uri != null) {
            //设置播放地址
            vv.setVideoURI(uri);
            tvName.setText(uri.toString());
        }
        //一进来变设置按钮的状态
        setButtonStatus();

    }

    private void getData() {
        //得到播放地址
        uri = getIntent().getData();
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);

    }

    private void initData() {
        utils = new Utils();

        //注册监听电量变化广播
        receiver = new MyBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //监听电量变化
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);
        //利用手势识别器实现长按、双击、单击功能（实例化手势识别器）
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                //Toast.makeText(SystemVideoPlayerActivity.this, "长按了", Toast.LENGTH_SHORT).show();
                setStartOrPause();
                super.onLongPress(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Toast.makeText(VitamioVideoPlayerActivity.this, "双击了", Toast.LENGTH_SHORT).show();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // Toast.makeText(SystemVideoPlayerActivity.this, "单击了", Toast.LENGTH_SHORT).show();
                if (isShowMediaController) {
                    hideMediaController();
                    handler.removeMessages(1);
                } else {
                    showMediaController();
                    handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 5000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });
        DisplayMetrics mertrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mertrics);
        screenHeight = mertrics.heightPixels;
        screenWidth = mertrics.widthPixels;

        //初始化声音相关的选项
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    }


//    private float startY;
//
//    private int touchRang = 0;
//
//    private int mVol;
//
//    //手势识别器一般往往是和触摸事件成对出现的（把事件交给手势识别器去解析）
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        super.onTouchEvent(event);
//        detector.onTouchEvent(event);
//
//
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            //1.按下
//            //按下的时候记录起始坐标，最大的滑动区域（屏幕的高），当前的音量
//            startY = event.getY();
//            touchRang = Math.min(screenHeight, screenWidth);//screeHeight
//            mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
//            //把消息移除
//            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
//        }else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//            float endY = event.getY();
//            //屏幕滑动的距离
//            float distanceY = startY - endY;
//            //滑动屏幕的距离 ： 总距离  = 改变的声音 ： 总声音
//
//            //改变的声音 = （滑动屏幕的距离 / 总距离)*总声音
//            float delta = (distanceY/touchRang) * maxVoice;
//            // 设置的声音  = 原来记录的 + 改变的声音
//            int volue = (int) Math.min(Math.max(mVol + delta,0),maxVoice);
//            //判断
//            if(delta != 0){
//                updateVoiceProgress(volue);
//            }
////            startY = event.getY();//不能添加
//        } else if (event.getAction() == MotionEvent.ACTION_UP) {
//            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,5000);
//        }
//        return true;
//    }

    //记录坐标
    private float dowY;
    //滑动的初始声音
    private int mVol;
    //滑动的最大区域
    private float touchRang;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把事件交给手势识别器解析
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //1.记录相关参数
                dowY = event.getY();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight, screenWidth);//screenHeight
                handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE:
                //2.滑动的时候来到新的位置
                float endY = event.getY();
                //3.计算滑动的距离
                float distanceY = dowY - endY;
                //原理：在屏幕滑动的距离： 滑动的总距离 = 要改变的声音： 最大声音
                //要改变的声音 = （在屏幕滑动的距离/ 滑动的总距离）*最大声音;
                float delta = (distanceY / touchRang) * maxVoice;


                if (delta != 0) {
                    //最终声音 = 原来的+ 要改变的声音
                    int mVoice = (int) Math.min(Math.max(mVol + delta, 0), maxVoice);
                    //0~15

                    updateVoiceProgress(mVoice);
                }


                //注意不要重新赋值
//                dowY = event.getY();


                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
                break;
        }
        return super.onTouchEvent(event);
    }

    //重写keydown方法或keyon方法控制按键调节音量的大小
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //改变音量值
            currentVoice--;
            updateVoiceProgress(currentVoice);
            if (currentVoice < 0) {
                currentVoice = 0;
            }
            //移除消息
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            //发消息
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 5000);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVoice++;
            updateVoiceProgress(currentVoice);
            if (currentVoice > maxVoice) {
                currentVoice = maxVoice;
            }
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 5000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }    //自己写的方法隐藏控制视频设置

    private void hideMediaController() {
        llBottom.setVisibility(View.GONE);
        llTop.setVisibility(View.GONE);
        isShowMediaController = false;//默认视频设置的布局是隐藏的
    }

    private void showMediaController() {
        llBottom.setVisibility(View.VISIBLE);
        llTop.setVisibility(View.VISIBLE);
        isShowMediaController = true;

    }


    class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);//主线程
            Log.e("TAG", "level==" + level);
            setBatteryView(level);

        }
    }

    private void setBatteryView(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private void setListener() {
        //设置播放器三个监听：播放准备好的监听，播放完成的监听，播放出错的监听
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            //底层准备播放完成的时候回调
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();
                //得到视频的总时长
                int duration = (int) vv.getDuration();
                seekbarVideo.setMax(duration);
                //设置文本总时间
                tvDuration.setText(utils.stringForTime(duration));
                //vv.seekTo(100);
                vv.start();//开始播放
                //发消息开始更新播放进度
                handler.sendEmptyMessage(PROGRESS);
                //隐藏加载效果画面
                ll_loading.setVisibility(View.GONE);
                //进入播放模式先隐藏控制的布局
                hideMediaController();
                //设置默认屏幕
                setVideoType(0);
                if(vv.isPlaying()){
                    btnStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
                }else {
                    btnStartPause.setBackgroundResource(R.drawable.btn_start_selector);
                }
            }
        });

        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
//                Toast.makeText(SystemVideoPlayerActivity.this, "播放出错了哦", Toast.LENGTH_SHORT).show();
                showErrorDialog();


                return true;
            }
        });

        //设置监听播放完成
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                Toast.makeText(SystemVideoPlayerActivity.this, "视频播放完成", Toast.LENGTH_SHORT).show();
//                finish();//退出当前页面
                setNextVedio();
            }
        });

        //设置Seekbar状态改变的监听（表示视频播放进程）
        seekbarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    vv.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //当一触摸SeekBar的时候移除隐藏的消息，保证在手指触碰seekbar期间控制面板不会被隐藏
                handler.removeMessages(1);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(1, 5000);//当手指移开的时候再重新发送消息（隐藏控制面板）

            }
        });
        //监听拖动声音(通过拖动seekbar)用来改变声音的大小
        seekbarVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateVoiceProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //当一触摸SeekBar的时候移除隐藏的消息，保证在手指触碰seekbar期间控制面板不会被隐藏
                handler.removeMessages(1);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.sendEmptyMessageDelayed(1, 5000);//当手指移开的时候再重新发送消息（隐藏控制面板）
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            vv.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        //拖动卡，缓存卡
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            ll_buffering.setVisibility(View.VISIBLE);
                            break;
                        //拖动卡，缓存卡结束
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            ll_buffering.setVisibility(View.GONE);
                            break;
                    }

                    return true;
                }
            });
        }


    }

    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("当前视频不可播放，请检查网络或者视频文件是否有损坏！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    private void startVitamioPlayer() {


    }

    //具体的实时方法：（声音随这不断拖动不断变化）
    private void updateVoiceProgress(int progress) {
        currentVoice = progress;
        //真正改变声音
        am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
        //改变进度条
        seekbarVoice.setProgress(currentVoice);
        if (currentVoice <= 0) {
            isMute = true;
        } else {
            isMute = false;
        }
    }

    //设置播放下一个
    private void setNextVedio() {
        position++;//在这加号在前于灾后都是一样的
        if (position < mediaItems.size()) {
            MediaItem mediaItem = mediaItems.get(position);
            isNetUri = utils.isNetUri(mediaItem.getData());
            ll_loading.setVisibility(View.VISIBLE);

            vv.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
            setButtonStatus();
        } else {
            Toast.makeText(this, "退出播放器", Toast.LENGTH_SHORT).show();
            finish();//position不在范围内是越界退出播放器
        }


    }

    //设置播放上一个
    private void setPreVedio() {
        position--;
        if (position >= 0) {
            MediaItem mediaItem = mediaItems.get(position);
            isNetUri = utils.isNetUri(mediaItem.getData());
            ll_loading.setVisibility(View.VISIBLE);
            vv.setVideoPath(mediaItem.getData());
            tvName.setText(mediaItem.getName());
            setButtonStatus();


        }

    }

    //设置按钮的状态
    private void setButtonStatus() {
        if (mediaItems != null && mediaItems.size() > 0) {
            setEnable(true);//一进来设置两个播放按钮均可点
            if (position == 0) {
                //如果是第一个视频设置上一个按钮不可点为灰色并且不可点击
                btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnPre.setEnabled(false);
            }
            if (position == mediaItems.size() - 1) {
                //如果是最后一个视频设置下一个按钮为灰色并且不可点击
                btnNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnNext.setEnabled(false);
            }//修改一个小bug当只有一个地址的时候两个按钮都是灰色的
        } else if (uri != null) {
            //如果播放地址为空（即没有视频可播两个按钮均不可点击）
            setEnable(false);
        }
    }


    //设置按钮是否可点（即可点击时为正常状态、不可点时为灰色）
    private void setEnable(boolean b) {
        if (b) {
            btnPre.setBackgroundResource(R.drawable.btn_pre_selector);
            btnNext.setBackgroundResource(R.drawable.btn_next_selector);
        } else {
            btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnNext.setBackgroundResource(R.drawable.btn_next_gray);
        }
        btnPre.setEnabled(b);
        btnNext.setEnabled(b);
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            //把所有消息移除
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

        //取消注册
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        super.onDestroy();
    }


}

