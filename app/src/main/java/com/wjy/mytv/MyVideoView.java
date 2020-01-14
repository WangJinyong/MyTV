package com.wjy.mytv;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by wjy.
 * Date: 2020/1/13
 * Time: 14:08
 * Describe: 自定义VideoView  设置全屏显示
 */
public class MyVideoView extends VideoView {

    public MyVideoView(Context context) {
        super(context);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 作用是返回一个默认的值，如果MeasureSpec没有强制限制的话则使用提供的大小.否则在允许范围内可任意指定大小
     * 第一个参数size为提供的默认大小，第二个参数为测量的大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=getDefaultSize(0,widthMeasureSpec);
        int height=getDefaultSize(0,heightMeasureSpec);
        Log.e("tag","width="+ Application.screenWidth);
        Log.e("tag","height="+Application.screenHeigh);
        setMeasuredDimension(Application.screenHeigh,Application.screenWidth);
    }

    @Override
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        super.setOnPreparedListener(l);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
