package com.example.jiangchuanfa.startallvideoplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btn_BoFang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_BoFang = (Button)findViewById(R.id.btn_BoFang);
    }
    public void startAllVideoPlayer(View v) {
        //把系统的播放器调起来
        Intent intent = new Intent();
        intent.setDataAndType(Uri.parse("http://vfx.mtime.cn/Video/2017/02/16/mp4/170216103715670994_480.mp4"),"video/*");
        startActivity(intent);

    }
}
