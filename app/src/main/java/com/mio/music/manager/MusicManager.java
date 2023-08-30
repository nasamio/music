package com.mio.music.manager;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;

import com.mio.music.Constants;
import com.mio.music.bean.MusicBean;
import com.mio.music.utils.LiveDataBus;
import com.mio.utils.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.mio.music.manager.MusicScanHelper.*;

public class MusicManager {
    private static MusicManager instance;
    private MediaPlayer mediaPlayer;

    private List<MusicBean> playList = new ArrayList<>();
    private int playMode = 2;// 播放模式 0: 列表循环 1：单曲循环 2：随机模式

    private int currentIndex = -1; // 当前播放曲目的下标

    private Handler handler;
    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                int progress = (int) (currentPosition * 100.f / duration);
                LogUtils.d("progress: " + progress);
                LiveDataBus.get().with(Constants.musicProgress).postValue(progress);
            }

            handler.postDelayed(progressRunnable, 200);
        }
    };

    private MusicManager() {
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    private Context mContext;

    public MusicManager init(@NotNull Context context) {
        mContext = context;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        handler = new Handler();
        handler.post(progressRunnable);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                switch (playMode) {
                    case 0:
                        next();
                        break;
                    case 1:
                        seekTo(0);
                        play(currentIndex);
                        break;
                    case 2:
                        // todo 随机
                        break;
                }
            }
        });
        return this;
    }

    private void seekTo(int mSec) {
        mediaPlayer.seekTo(mSec);
    }


    private void dispatcher(@NotNull List<MusicBean> list) {
        LiveDataBus.get().with(Constants.scanMusic).postValue(list);
    }

    /**
     * 把歌曲添加到现在的列表的后面
     */
    @TargetApi(Build.VERSION_CODES.N)
    public void addToPlayList(@NotNull List<MusicBean> list) {
        ArrayList<MusicBean> temp = new ArrayList<>();
        temp.addAll(playList);
        temp.addAll(list);
        List<MusicBean> collect = temp.stream().distinct().collect(Collectors.toList());
        synchronized (playList) {
            playList.clear();
            playList.addAll(collect);
        }
    }

    public void play(int index) {
        if (index == currentIndex) { // 说明操作的就是当前这首歌
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        } else { // 说明切换了歌曲
            MusicBean bean = playList.get(index);
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(bean.getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                currentIndex = index;
                LiveDataBus.get().with(Constants.playingPath).postValue(bean.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void next() {
        switch (playMode) {
            case 0: // 列表循环
            case 1: // 单曲循环
                if (currentIndex == playList.size() - 1) {
                    play(0);
                } else {
                    play(currentIndex + 1);
                }
                break;
            case 2: // 随机
                play(MusicHelper.getInstance().getRandom(playList.size(), currentIndex));
                break;
        }
    }

    public void previous() {
        switch (playMode){
            case 0:
            case 1:
                if (currentIndex == 0) {
                    play(playList.size() - 1);
                } else {
                    play(currentIndex - 1);
                }
                break;
            case 2:
                play(MusicHelper.getInstance().getRandom(playList.size(), currentIndex));
                break;
        }
    }

    public void scanMusic() {
        // 本地根据文件过滤
        new Thread(() -> {
            List<MusicBean> list = new ArrayList<>();
            // 高版本的直接从content provider拿数据
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                list = scanMusicByContentProvider(mContext);
            } else {
                List<String> scanListByFile = findFilesWithExtensions(Constants.scanDir, Constants.suffix);
                List<String> scanListByCont = wrap(scanMusicByContentProvider(mContext));
                // 合并两个列表并去重
                List<String> scanList = mergeAndRemoveDuplicates(scanListByFile, scanListByCont);

                for (String name : scanList) {
                    // 封装
                    MusicBean bean = requireMusicBean(name);// todo 这里在高api的手机上会崩溃
                    list.add(bean);
                }
                LogUtils.e("扫描出" + scanList.size() + "首歌，成功获取信息" + list.size() + "首歌");
            }
            dispatcher(list);
        }).run();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public MusicBean scanItemByPath(@NotNull String path) {
        List<MusicBean> list = playList.stream().filter(musicBean ->
                path.equals(musicBean.getPath())).collect(Collectors.toList());
        return list.size() > 0 ? list.get(0) : null;
    }
}
