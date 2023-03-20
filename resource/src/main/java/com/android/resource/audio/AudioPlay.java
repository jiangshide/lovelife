package com.android.resource.audio;

import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.android.cache.HttpProxyCacheServer;
import com.android.network.NetWork;
import com.android.player.audio.AudioVisualConverter;
import com.android.resource.vm.blog.data.Blog;
import com.android.utils.AppUtil;
import com.android.utils.LogUtil;
import com.android.widget.ZdToast;

import java.io.File;
import java.util.ArrayList;

/**
 * created by jiangshide on 2020/4/22.
 * email:18311271399@163.com
 */
public class AudioPlay implements MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private static final AudioPlay
            INSTANCE = new AudioPlay();
    private static final float DEFAULT_PLAY_SPEED = 1.0f;

    private MediaPlayer mediaPlayer;
    private Equalizer equalizer;
    private PlayStateListener mPlayStateListener;
    private Visualizer.OnDataCaptureListener mOnDataCaptureListener;

    private Visualizer visualizer;
    private AudioVisualConverter audioVisualConverter;

    private float playSpeed = DEFAULT_PLAY_SPEED;

    public boolean isPreparing;

    public ArrayList<Blog> mData;
    public int index;
    public static final int LOOP_LIST = 0;
    public static final int LOOP_SINGLE = 1;
    public static final int SEQUENCE = 2;
    public static final int RANDOM = 3;
    public static final int SINGLE = 4;
    public int mMode = LOOP_LIST;
    private HttpProxyCacheServer httpProxyCacheServer;

    public String getProxyUrl(String url) {
        if (TextUtils.isEmpty(url)) return url;
        if (url.contains("http") || url.contains("https")) {
            if (httpProxyCacheServer == null) {
                httpProxyCacheServer = new HttpProxyCacheServer(AppUtil.getApplicationContext());
            }
            return httpProxyCacheServer.getProxyUrl(url);
        }
        return url;
    }

    /**
     * 获取MediaPlayerId
     * 可视化类Visualizer需要此参数
     *
     * @return MediaPlayerId
     */
    public int getMediaPlayerId() {
        return mediaPlayer.getAudioSessionId();
    }

    public static AudioPlay getInstance() {
        return INSTANCE;
    }

    private AudioPlay() {
    }

    public synchronized AudioPlay raw(final int raw) {
        return doPlay(raw, null);
    }

    public synchronized AudioPlay play(final File file) {
        return play(file.getAbsolutePath());
    }

    public synchronized AudioPlay play(final String path) {
        String proxy = getProxyUrl(path);
        boolean isNet = NetWork.Companion.getInstance().isNetworkAvailable();
        if (!TextUtils.isEmpty(proxy) && (proxy.contains("http") || proxy.contains("https")) && !isNet){
            proxy = "";
            ZdToast.txt("无网络，无缓存,暂不能播放!");
        }
        return play(Uri.parse(proxy));
    }

    public synchronized AudioPlay play(final Uri uri) {
        return doPlay(0, uri);
    }

    public boolean isPlaying() {
        if (mediaPlayer == null) return false;
        return mediaPlayer.isPlaying();
    }

    public void start() {
        if (mediaPlayer == null) {
            if (mData != null && mData.size() > 0) {
                play();
            }
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            notifyState(PlayState.STATE_PAUSE);
        } else {
            resume();
        }
    }

    public synchronized void stop() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            notifyState(PlayState.STATE_IDLE);
        }
    }

    public synchronized void pause() {
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            return;
        }
        notifyState(PlayState.STATE_PAUSE);
        mediaPlayer.pause();
    }

    public void resume() {
        if (mediaPlayer == null || mediaPlayer.isPlaying()) {
            return;
        }
        notifyState(PlayState.STATE_RESUME);
        mediaPlayer.start();
    }

    public synchronized void release() {
        if (mediaPlayer == null) {
            return;
        }
        playSpeed = DEFAULT_PLAY_SPEED;
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void changePlayerSpeed(final float speed) {
        playSpeed = speed;
        if (mediaPlayer == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
            } else {
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
                mediaPlayer.pause();
            }
        }
    }

    public AudioPlay setData(ArrayList<Blog> data) {
        this.mData = data;
        return this;
    }

    public int setData(Blog data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        this.mData.add(0, data);
        index = 0;
        play(data);
        return index;
    }

    public AudioPlay update(Blog data) {
        int count = count();
        for (int i = 0; i < count; i++) {
            if (mData.get(i) == data) {
                mData.get(i).setLrcZh(data.getLrcZh());
                mData.get(i).setLrcEn(data.getLrcEn());
            }
        }
        return this;
    }

    public int count() {
        if (mData == null) return 0;
        return mData.size();
    }

    public AudioPlay setPlayStateListener(PlayStateListener playStateListener) {
        this.mPlayStateListener = playStateListener;
        return this;
    }

    public AudioPlay setDataCaptureListener(Visualizer.OnDataCaptureListener listener) {
        this.mOnDataCaptureListener = listener;
        return this;
    }

    public AudioPlay setMode(int mode) {
        this.mMode = mode;
        return this;
    }

    public AudioPlay prev() {
        if (mData != null) {
            index--;
            if (index < 0) {
                index = mData.size() - 1;
            }
            play();
        } else {
            stop();
        }
        return this;
    }

    public AudioPlay play() {
        if (mData != null && mData.size() > 0) {
            if (index > mData.size() - 1) {
                index = 0;
            }
            play(mData.get(index).getUrl());
        } else {
            stop();
        }
        return this;
    }

    public AudioPlay play(int position) {
        if (mData == null || mData.size() == 0 || mData.size() - 1 < position) return this;
        index = position;
        return play(mData.get(position).getUrl());
    }

    public AudioPlay play(Blog blog) {
        if (mData != null && mData.size() > 0) {
            int size = mData.size() - 1;
            for (int i = 0; i < size; i++) {
                if (mData.get(i).getUrl().equals(blog.getUrl())) {
                    index = i;
                }
            }
            play();
        } else {
            play(blog.getUrl());
        }
        return this;
    }

    public AudioPlay next() {
        if (mData != null) {
            index++;
            if (index > mData.size() - 1) {
                index = 0;
            }
            play();
        } else {
            stop();
        }
        return this;
    }

    public int setMode() {
        if (mMode == LOOP_LIST) {
            mMode = LOOP_SINGLE;
        } else if (mMode == LOOP_SINGLE) {
            mMode = SEQUENCE;
        } else if (mMode == SEQUENCE) {
            mMode = RANDOM;
        } else if (mMode == RANDOM) {
            mMode = SINGLE;
        } else {
            mMode = LOOP_LIST;
        }
        return mMode;
    }

    /**
     * 播放音频
     *
     * @param raw 资源文件id
     * @param uri url
     */
    private synchronized AudioPlay doPlay(final int raw, final Uri uri) {
        notifyState(PlayState.STATE_INITIALIZING);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        try {
            if (raw == 0) {
                mediaPlayer = MediaPlayer.create(AppUtil.getApplicationContext(), uri);
            } else {
                mediaPlayer = MediaPlayer.create(AppUtil.getApplicationContext(), raw);
            }
            if (mediaPlayer == null) {
                LogUtil.e("mediaPlayer is null");
                return this;
            }

            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            stop();
            release();
            mediaPlayer = null;
        }
        return this;
    }

    public int currentPosition() {
        if (mediaPlayer == null) return 0;
        return mediaPlayer.getCurrentPosition();
    }

    public int duration() {
        if (mediaPlayer == null) return 0;
        return mediaPlayer.getDuration();
    }

    public float progress() {
        if (mediaPlayer == null) return 0;
        float curr = mediaPlayer.getCurrentPosition();
        float duration = mediaPlayer.getDuration();
        float p = curr / duration;
        float progress = p * 100;
        return p;
    }

    public Blog getData() {
        if (mData == null || mData.size() == 0 || mData.size() - 1 < index) return null;
        return mData.get(index);
    }

    public void seekTo(int progress) {
        if (mediaPlayer == null) return;
        mediaPlayer.seekTo(progress);
    }

    /**
     * 注：此方法在子线程中
     *
     * @param state 播放状态
     */
    private void notifyState(final PlayState state) {
        if (state == PlayState.STATE_INITIALIZING) {
            isPreparing = true;
        } else {
            isPreparing = false;
            if (state == PlayState.STATE_PLAYING && mOnDataCaptureListener != null) {
                initVisualizer();
            }
        }
        if (mPlayStateListener != null) {
            mPlayStateListener.onStateChange(state);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        notifyState(PlayState.STATE_IDLE);
        if (mMode == LOOP_LIST || mMode == SEQUENCE) {
            next();
        } else if (mMode == RANDOM) {
            index = (int) Math.round(Math.random() * (mData.size() - 1 - 0) + 0);
            play();
        }
        if (mMode == LOOP_SINGLE) {
            play();
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        notifyState(PlayState.STATE_IDLE);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        if (playSpeed != DEFAULT_PLAY_SPEED) {
            changePlayerSpeed(playSpeed);
        }
        notifyState(PlayState.STATE_PLAYING);
    }

    public enum PlayState {
        /**
         * 播放器初始化中，不可用
         */
        STATE_INITIALIZING,
        /**
         * 空闲状态,可用
         */
        STATE_IDLE,
        /**
         * 正在播放
         */
        STATE_PLAYING,
        /**
         * 暂停状态
         */
        STATE_PAUSE,
        /**
         * 恢复播放
         */
        STATE_RESUME
    }

    public interface PlayStateListener {
        void onStateChange(PlayState state);
    }

    private void initVisualizer() {
        audioVisualConverter = new AudioVisualConverter();
        try {
            int mediaPlayerId = AudioPlay.getInstance().getMediaPlayerId();
            LogUtil.i("mediaPlayerId: %s", mediaPlayerId);
            if (visualizer != null) {
                visualizer.release();
            }
            visualizer = new Visualizer(mediaPlayerId);

            int captureSize = Visualizer.getCaptureSizeRange()[1];
            int captureRate = Visualizer.getMaxCaptureRate() * 3 / 4;
            LogUtil.d("精度: %s", captureSize);
            LogUtil.d("刷新频率: %s", captureRate);

            visualizer.setCaptureSize(captureSize);
            visualizer.setDataCaptureListener(mOnDataCaptureListener, captureRate, true, true);
            visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
            visualizer.setEnabled(true);
        } catch (Exception e) {
            LogUtil.e("请检查录音权限");
        }
    }
}
