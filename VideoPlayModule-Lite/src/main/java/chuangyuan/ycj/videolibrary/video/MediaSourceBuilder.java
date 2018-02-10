

package chuangyuan.ycj.videolibrary.video;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.DynamicConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

import chuangyuan.ycj.videolibrary.factory.JDefaultDataSourceFactory;
import chuangyuan.ycj.videolibrary.listener.DataSourceListener;
import chuangyuan.ycj.videolibrary.listener.ItemVideo;

/**
 * author yangc
 * date 2017/2/28
 * E-Mail:1007181167@qq.com
 * Description：数据源处理类
 */
public class MediaSourceBuilder {
    private static final String TAG = MediaSourceBuilder.class.getName();
    protected Context context;
    protected Handler mainHandler = null;
    private MediaSource mediaSource;
    protected DataSourceListener listener;
    protected AdaptiveMediaSourceEventListener sourceEventListener;
    private int indexType = -1;
    private List<String> videoUri;

    /***
     * 初始化
     *
     * @param context 上下文
     **/
    public MediaSourceBuilder(@NonNull Context context) {
        this(context, null);
    }

    /***
     * 初始化
     *
     * @param context 上下文
     * @param listener 自定义数源工厂接口
     **/
    public MediaSourceBuilder(@NonNull Context context, @Nullable DataSourceListener listener) {
        this.listener = listener;
        this.context = context.getApplicationContext();
        mainHandler = new Handler();
    }

    /****
     * 初始化
     *
     * @param uri     视频的地址
     ***/
    void setMediaUri(@NonNull Uri uri) {
        mediaSource = initMediaSource(uri);
    }

    /****
     * 初始化
     *
     * @param uris     视频的地址列表
     ***/
    public void setMediaUri(@NonNull Uri... uris) {
        MediaSource[] firstSources = new MediaSource[uris.length];
        int i = 0;
        for (Uri item : uris) {
            firstSources[i] = initMediaSource(item);
            i++;
        }
        mediaSource = new ConcatenatingMediaSource(firstSources);
    }


    /****
     * 初始化多个视频源，无缝衔接
     *
     * @param firstVideoUri  第一个视频， 例如例如广告视频
     * @param secondVideoUri 第二个视频
     ***/
    public void setMediaUri(@NonNull Uri firstVideoUri, @NonNull Uri secondVideoUri) {
        setMediaUri(0, firstVideoUri, secondVideoUri);
    }

    /****
     * 初始化多个视频源，无缝衔接
     *
     * @param firstVideoUri  第一个视频， 例如例如广告视频
     * @param secondVideoUri 第二个视频
     ***/
    public void setMediaUri(@Size(min = 0) int indexType, int switchIndex, @NonNull Uri firstVideoUri, @NonNull List<String> secondVideoUri) {
        this.videoUri = secondVideoUri;
        this.indexType = indexType;
        setMediaUri(indexType, firstVideoUri, Uri.parse(secondVideoUri.get(switchIndex)));
    }

    /****
     * @param  indexType  设置当前索引视频屏蔽进度
     * @param firstVideoUri  预览的视频
     * @param secondVideoUri 第二个视频

     **/
    public void setMediaUri(@Size(min = 0) int indexType, @NonNull Uri firstVideoUri, @NonNull Uri secondVideoUri) {
        this.indexType = indexType;
        DynamicConcatenatingMediaSource source = new DynamicConcatenatingMediaSource();
        source.addMediaSource(initMediaSource(firstVideoUri));
        source.addMediaSource(initMediaSource(secondVideoUri));
        mediaSource = source;
    }


    /**
     * 设置多线路播放
     *
     * @param videoUri 视频地址
     * @param index    选中播放线路
     **/
    public void setMediaSwitchUri(@NonNull List<String> videoUri, int index) {
        this.videoUri = videoUri;
        setMediaUri(Uri.parse(videoUri.get(index)));
    }

    /****
     * 初始化
     *
     * @param uris     视频的地址列表\
     * @param <T>    你的实体类
     ***/
    public <T extends ItemVideo> void setMediaUri(@NonNull List<T> uris) {
        MediaSource[] firstSources = new MediaSource[uris.size()];
        int i = 0;
        for (T item : uris) {
            if (item.getVideoUri() != null) {
                firstSources[i] = initMediaSource(Uri.parse(item.getVideoUri()));
            }
            i++;
        }
        mediaSource = new ConcatenatingMediaSource(firstSources);
    }

    /**
     * 返回循环播放实例
     *
     * @return LoopingMediaSource
     ***/
    LoopingMediaSource setLooping(@Size(min = 1) int loopCount) {
        return new LoopingMediaSource(mediaSource, loopCount);
    }

    /***
     * 获取视频数据源
     **/
    MediaSource getMediaSource() {
        return mediaSource;
    }

    /***
     * 设置自定义视频数据源
     * @param mediaSource 你的数据源
     **/
    public void setMediaSource(MediaSource mediaSource) {
        this.mediaSource = mediaSource;
    }

    /***
     * 初始化数据源工厂
     * @return DataSource.Factory
     **/
    public DataSource.Factory getDataSource() {
        if (listener != null) {
            return listener.getDataSourceFactory();
        } else {
            return new JDefaultDataSourceFactory(context);
        }
    }

    /***
     * 移除多媒体
     * **/
    void removeMediaSource() {
        if (mediaSource instanceof DynamicConcatenatingMediaSource) {
            DynamicConcatenatingMediaSource source = (DynamicConcatenatingMediaSource) mediaSource;
            if (source.getSize() > 2) {
                source.getMediaSource(source.getSize() - 1).releaseSource();
                source.removeMediaSource(source.getSize() - 1);
                removeMediaSource();
            }
        }
    }

    /****
     * 释放资源
     **/
    public void release() {
        if (mediaSource != null) {
            mediaSource.releaseSource();
        }
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(context);
            mainHandler = null;
        }
    }

    /****
     * 销毁资源
     **/
    public void destroy() {
        release();
        indexType = -1;
        videoUri = null;
        listener = null;
    }

    /**
     * 获取视频所在索引
     *
     * @return int
     **/
    public int getIndexType() {
        return indexType;
    }

    /**
     * 设置视频所在索引
     *
     * @param indexType 值
     **/
    public void setIndexType(@Size(min = 0) int indexType) {
        this.indexType = indexType;
    }

    /**
     * 获取视频线路地址
     *
     * @return List<String>
     **/
    List<String> getVideoUri() {
        return videoUri;
    }

    /**
     * 用于通知自适应的回调接口获取视频线路名称
     *
     * @param sourceEventListener 实例
     **/
    public void setAdaptiveMediaSourceEventListener(AdaptiveMediaSourceEventListener sourceEventListener) {
        this.sourceEventListener = sourceEventListener;
    }

    /****
     * 初始化视频源，无缝衔接
     *
     * @param uri        视频的地址
     * @return MediaSource
     ***/
    public MediaSource initMediaSource(Uri uri) {
        int streamType = Util.inferContentType(uri);
        switch (streamType) {
            case C.TYPE_OTHER:
                Log.d(TAG, "TYPE_OTHER");
                return new ExtractorMediaSource(uri, getDataSource(), new DefaultExtractorsFactory(), mainHandler, null, uri.getPath());
            default:
                throw new IllegalStateException("你的MediaSource 为空 当前视频类型,或者实现类型" + streamType);
        }
    }

}
