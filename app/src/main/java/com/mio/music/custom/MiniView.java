package com.mio.music.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.mio.basic.BaseView;
import com.mio.music.Constants;
import com.mio.music.R;
import com.mio.music.bean.MusicBean;
import com.mio.music.databinding.VMiniBinding;
import com.mio.music.manager.MusicManager;
import com.mio.music.utils.LiveDataBus;
import com.mio.music.utils.PlayStatusUtils;

public class MiniView extends BaseView<VMiniBinding> {
    public MiniView(Context context) {
        this(context, null);
    }

    public MiniView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {
        LiveDataBus.get().with(Constants.playingPath, String.class)
                .observe((LifecycleOwner) mContext, s -> {
                    MusicBean bean = MusicManager.getInstance().scanItemByPath(s);
                    String text = bean.getTitle();
                    if (bean.getArtist() != null && !bean.getArtist().isEmpty()) {
                        text += "(" + bean.getArtist() + ")";
                    }
                    mDataBinding.tvText.setText(text);
                });
        LiveDataBus.get().with(Constants.musicProgress, Float.class)
                .observe((LifecycleOwner) mContext, f ->
                        mDataBinding.miniProgress.setProgress(f / 100.f));

        mDataBinding.miniProgress.setProgress(0);
        mDataBinding.btnPre.setOnClickListener(v -> MusicManager.getInstance().previous());
        mDataBinding.btnNext.setOnClickListener(v -> MusicManager.getInstance().next());
        mDataBinding.btnPlay.setOnClickListener(v -> {
            if (MusicManager.getInstance().getPlayStatus()) {
                MusicManager.getInstance().pause();
            } else {
                MusicManager.getInstance().play();
            }
        });
        mDataBinding.getRoot().setOnClickListener(v ->
                LiveDataBus.get().with(Constants.showPlay).postValue(true));
        PlayStatusUtils.bind((LifecycleOwner) mContext, mDataBinding.btnPlay, R.drawable.play, R.drawable.pause);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.v_mini;
    }
}
