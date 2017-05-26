package com.example.jiangchuanfa.projectofmediaplayer.CustomView;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.jiangchuanfa.projectofmediaplayer.DoMain.Lyric;

import java.util.ArrayList;

/**
 * Created by crest on 2017/5/26.
 */


public class LyricShowView extends TextView {
    private Paint paintGreen;
    private Paint paintWhite;
    private int width;
    private int height;
    private ArrayList<Lyric> lyrics;
    private int index = 0;
    private int textHeight = 65;
    private int currentPosition;


    public LyricShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
//绿色画笔
        paintGreen = new Paint();
        paintGreen.setTextSize(50);
        paintGreen.setColor(Color.GREEN);
        paintGreen.setTextAlign(Paint.Align.CENTER);
        paintGreen.setAntiAlias(true);


//        白色画笔
        paintWhite = new Paint();
        paintWhite.setTextSize(50);
        paintWhite.setColor(Color.WHITE);
        paintWhite.setTextAlign(Paint.Align.CENTER);
        paintWhite.setAntiAlias(true);

//        //准备歌词(这个是伪歌词用于显示效果)
//        lyrics = new ArrayList<>();
//        Lyric lyric = new Lyric();
//        for (int i = 0; i < 10000; i++) {
//            //不同歌词
//            lyric.setContent("aaaaaaaaaaaa_" + i);
//            lyric.setSleepTime(2000);
//            lyric.setTimePoint(2000 * i);
//            //添加到集合
//            lyrics.add(lyric);
//            //重新创建新对象
//            lyric = new Lyric();
//        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lyrics != null && lyrics.size() > 0) {
            //绘制当前句歌词
            String currentContent = lyrics.get(index).getContent();
            canvas.drawText(currentContent, width / 2, height / 2, paintGreen);
            float tempY = height / 2;
            //绘制前半部分
            for (int i = index - 1; i >= 0; i--) {
                String preContent = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(preContent, width / 2, tempY, paintWhite);
            }
            tempY = height / 2;

            //绘制后面部分
            for (int i = index + 1; i < lyrics.size(); i++) {
                //得到后一部分多月的歌词内容
                String nextContent = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }
                //绘制内容
                canvas.drawText(nextContent, width / 2, tempY, paintWhite);
            }
        } else {
            canvas.drawText("没有找到歌词....", width / 2, height / 2, paintGreen);
        }
    }

    public void setNextShowLyric(int currentPosition) {
        this.currentPosition = currentPosition;
        if (lyrics == null || lyrics.size() == 0)
            return;

        for (int i = 1; i < lyrics.size(); i++) {

            if (currentPosition < lyrics.get(i).getTimePoint()) {
                int tempIndex = i - 1;
                if (currentPosition >= lyrics.get(tempIndex).getTimePoint()) {
                    //中间高亮显示的哪一句
                    index = tempIndex;
                }
            }
        }
        //什么方法导致onDraw
        invalidate();
    }

    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }
}
