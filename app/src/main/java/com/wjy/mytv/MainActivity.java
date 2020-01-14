package com.wjy.mytv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;

/**
 * 集成Vitamio打造属于自己的万能播放器
 */
public class MainActivity extends Activity {

    private RelativeLayout rl_rootView;//根View
    private RelativeLayout rl_loading_layout,rl_stop_layout;//加载布局、暂停布局
    private LinearLayout ll_top_layout,ll_play_layout;//顶部panel、底部panel
    private ImageView iv_back,iv_play,img_stopAndPlay;//返回按钮、播放按钮、屏幕中间的播放暂停按钮
    private TextView tv_title,tv_systime;
    private MyVideoView videoView;
    private static final int RETRY_TIMES = 5;
    private int count = 0;
    private static final int AUTO_HIDE_TIME = 5000;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Handler timeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    tv_systime.setText(getCurrentTime());
                    break;
            }
        }
    };

    private String title;
    private String url = "http://218.77.102.118:1935/live/zonghe/playlist.m3u8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        Vitamio.isInitialized(getApplicationContext());
        if (!LibsChecker.checkVitamioLibs(this))
            return;
        setContentView(R.layout.activity_main);
        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");
        initView();
        initPlayer();
        new TimeThread().start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        count = 0;
        if (videoView != null){
            videoView.stopPlayback();
        }
    }

    private void initView() {
        rl_rootView = findViewById(R.id.rl_rootView);
        rl_loading_layout = findViewById(R.id.rl_loading_layout);
        rl_stop_layout = findViewById(R.id.rl_stop_layout);
        ll_top_layout = findViewById(R.id.ll_top_layout);
        ll_play_layout = findViewById(R.id.ll_play_layout);

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);

        tv_systime =findViewById(R.id.tv_systime);

        rl_rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_top_layout.getVisibility() == View.VISIBLE || ll_play_layout.getVisibility() == View.VISIBLE){
                    ll_top_layout.setVisibility(View.GONE);
                    ll_play_layout.setVisibility(View.GONE);
                    return;
                }
                if (videoView.isPlaying()){
                    iv_play.setImageResource(R.mipmap.icon_playing_normal);
                    rl_stop_layout.setVisibility(View.GONE);
                }else {
                    iv_play.setImageResource(R.mipmap.icon_pause_normal);
                    rl_stop_layout.setVisibility(View.VISIBLE);
                }
                ll_top_layout.setVisibility(View.VISIBLE);
                ll_play_layout.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ll_top_layout.setVisibility(View.GONE);
                        ll_play_layout.setVisibility(View.GONE);
                        rl_stop_layout.setVisibility(View.GONE);
                    }
                },AUTO_HIDE_TIME);
            }
        });

        iv_play = findViewById(R.id.iv_play);
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopOrPlay();
            }
        });

        img_stopAndPlay = findViewById(R.id.img_stopAndPlay);
        img_stopAndPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopOrPlay();
            }
        });
    }

    private void stopOrPlay(){
        if (videoView.isPlaying()){
            videoView.stopPlayback();
            iv_play.setImageResource(R.mipmap.icon_pause_normal);
            rl_stop_layout.setVisibility(View.VISIBLE);
            img_stopAndPlay.setImageResource(R.mipmap.icon_pause_normal);
        }else {
            videoView.setVideoPath(url);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoView.start();
                }
            });
            iv_play.setImageResource(R.mipmap.icon_playing_normal);
            rl_stop_layout.setVisibility(View.GONE);
            img_stopAndPlay.setImageResource(R.mipmap.icon_playing_normal);
        }
    }

    private void initPlayer() {
        videoView = findViewById(R.id.videoView);
        videoView.setVideoPath(url);
        //监听视频装载完成
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });

        //监听发生错误
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("tag","onError");
                if (count > RETRY_TIMES){
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("播放出错啦")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.this.finish();
                                }
                            }).setCancelable(false);
                }else {
                    videoView.stopPlayback();
                    videoView.setVideoPath(url);
                }
                count++;
                return false;
            }
        });
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what){
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START://MediaPlayer暂时暂停播放，以缓冲更多的数据
                        Log.e("tag 显示加载布局","显示加载布局");
                        rl_loading_layout.setVisibility(View.VISIBLE);//显示加载布局
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END://媒体播放器正在恢复播放后，填充缓冲区
                        Log.e("tag"," 隐藏加载布局 BUFFERING_END");
                    case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING://这个视频对解码器来说太复杂了:它解码帧的速度不够快。可能只有音频在这个阶段播放得很好。
                        Log.e("tag"," 隐藏加载布局 VIDEO_TRACK_LAGGING");
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                        Log.e("tag"," 隐藏加载布局 DOWNLOAD_RATE_CHANGED");
                        rl_loading_layout.setVisibility(View.GONE);//隐藏加载布局
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 获取系统当前时间
     * @return time
     */
    private String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = simpleDateFormat.format(calendar.getTime());
        return time;
    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;  //消息(一个整型值)
                    timeHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }
}
