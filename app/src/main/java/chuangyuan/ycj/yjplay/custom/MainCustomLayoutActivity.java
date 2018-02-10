package chuangyuan.ycj.yjplay.custom;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ui.InterfaceUpdateProgress;
import com.google.android.exoplayer2.ui.PlaybackControlView;

import chuangyuan.ycj.videolibrary.listener.LoadModelType;
import chuangyuan.ycj.videolibrary.listener.OnGestureBrightnessListener;
import chuangyuan.ycj.videolibrary.listener.OnGestureProgressListener;
import chuangyuan.ycj.videolibrary.listener.OnGestureVolumeListener;
import chuangyuan.ycj.videolibrary.video.ManualPlayer;
import chuangyuan.ycj.videolibrary.video.VideoPlayerManager;
import chuangyuan.ycj.videolibrary.widget.VideoPlayerView;
import chuangyuan.ycj.yjplay.data.DataSource;
import chuangyuan.ycj.yjplay.R;


public class MainCustomLayoutActivity extends AppCompatActivity {

    private ManualPlayer exoPlayerManager;
    private VideoPlayerView videoPlayerView;
    public static final String VIEW_NAME_HEADER_IMAGE = "123";
    private static final String TAG = "OfficeDetailedActivity";
    private long currPosition = 0;
    private String url = "";
    TextView exo_video_dialog_pro_text;
    private ImageView videoAudioImg, videoBrightnessImg;
    /***显示音频和亮度***/
    private ProgressBar videoAudioPro, videoBrightnessPro;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mPlaybackControlView.updateProgress();
            handler.sendEmptyMessage(1);
        }
    };
    private PlaybackControlView mPlaybackControlView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_coutom);
        currPosition = getIntent().getLongExtra("currPosition", 0);
        url = getIntent().getStringExtra("uri");
        videoPlayerView = (VideoPlayerView) findViewById(R.id.exo_play_context_id);
        exo_video_dialog_pro_text = (TextView) findViewById(R.id.exo_video_dialog_pro_text);
        videoAudioImg = (ImageView) findViewById(R.id.exo_video_audio_img);
        videoAudioPro = (ProgressBar) findViewById(R.id.exo_video_audio_pro);
        videoBrightnessImg = (ImageView) findViewById(R.id.exo_video_brightness_img);
        videoBrightnessPro = (ProgressBar) findViewById(R.id.exo_video_brightness_pro);
        ViewCompat.setTransitionName(videoPlayerView, VIEW_NAME_HEADER_IMAGE);
        exoPlayerManager = new ManualPlayer(this, videoPlayerView, new DataSource(getApplication()));
        videoPlayerView.setOpenLock(false);
        exoPlayerManager.setPosition(currPosition);
        exoPlayerManager.setTitle("自定义视频标题");
        //设置加载显示模式
        exoPlayerManager.setLoadModel(LoadModelType.PERCENR);
        exoPlayerManager.setPlayUri(url);
        // exoPlayerManager.setPlayUri("http://ytnbin.oss-cn-beijing.aliyuncs.com/2017/12/video/1512631425851.mp4");
        mPlaybackControlView = videoPlayerView.getPlaybackControlView();

        mPlaybackControlView.updateProgress();
        handler.sendEmptyMessage(0);
        mPlaybackControlView.setOnUpdateProgressListener(new InterfaceUpdateProgress() {
            @Override
            public void updateProgress(long currentProgress) {
                Log.e("progress","视频当前播放进度:"+currentProgress);
            }

            @Override
            public void updateDuration(long duration) {
                Log.e("progress","视频总时长"+duration);
            }
        });

        exoPlayerManager.setOnPlayClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainCustomLayoutActivity.this, "定义点击播放事件", Toast.LENGTH_LONG).show();
                //处理业务操作 完成后，
                exoPlayerManager.startPlayer();//开始播放
            }
        });
        Glide.with(this)
                .load(getString(R.string.uri_test_image))
                .placeholder(R.mipmap.test)
                .fitCenter()
                .into(videoPlayerView.getPreviewImage());

        //自定义布局使用
        videoPlayerView.getReplayLayout().findViewById(R.id.replay_btn_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainCustomLayoutActivity.this, "自定义分享", Toast.LENGTH_SHORT).show();
            }
        });
        videoPlayerView.getErrorLayout().findViewById(R.id.exo_player_error_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainCustomLayoutActivity.this, "自定义错误", Toast.LENGTH_SHORT).show();
            }
        });
        videoPlayerView.getPlayHintLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainCustomLayoutActivity.this, "自定义提示", Toast.LENGTH_SHORT).show();
            }
        });
        //重写自定义手势监听事件，
        exoPlayerManager.setOnGestureBrightnessListener(new OnGestureBrightnessListener() {
            @Override
            public void setBrightnessPosition(int mMax, int currIndex) {
                //显示你的布局
                videoPlayerView.getGestureBrightnessLayout().setVisibility(View.VISIBLE);
                //为你布局显示内容自定义内容
                videoBrightnessPro.setMax(mMax);
                videoBrightnessImg.setImageResource(chuangyuan.ycj.videolibrary.R.drawable.ic_brightness_6_white_48px);
                videoBrightnessPro.setProgress(currIndex);
            }
        });
        //重写自定义手势监听事件，
        exoPlayerManager.setOnGestureProgressListener(new OnGestureProgressListener() {
            @Override
            public void showProgressDialog(long seekTimePosition, long duration, String seekTime, String totalTime) {
                //显示你的布局
                videoPlayerView.getGestureProgressLayout().setVisibility(View.VISIBLE);
                exo_video_dialog_pro_text.setTextColor(Color.RED);
                exo_video_dialog_pro_text.setText(seekTime + "/" + totalTime);
            }
        });
        //重写自定义手势监听事件，
        exoPlayerManager.setOnGestureVolumeListener(new OnGestureVolumeListener() {
            @Override
            public void setVolumePosition(int mMax, int currIndex) {
                //显示你的布局
                videoPlayerView.getGestureAudioLayout().setVisibility(View.VISIBLE);
                //为你布局显示内容自定义内容
                videoAudioPro.setMax(mMax);
                videoAudioPro.setProgress(currIndex);
                videoAudioImg.setImageResource(currIndex == 0 ? R.drawable.ic_volume_off_white_48px : R.drawable.ic_volume_up_white_48px);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        exoPlayerManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        exoPlayerManager.onPause();
    }


    @Override
    protected void onDestroy() {
        exoPlayerManager.onDestroy();
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        exoPlayerManager.onConfigurationChanged(newConfig);//横竖屏切换
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (exoPlayerManager.onBackPressed()) {//使用播放返回键监听
            Toast.makeText(MainCustomLayoutActivity.this, "返回", Toast.LENGTH_LONG).show();
            finish();
        }

    }


}
