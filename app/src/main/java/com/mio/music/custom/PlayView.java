package com.mio.music.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.lxj.xpopup.core.BottomPopupView;
import com.mio.music.Constants;
import com.mio.music.R;
import com.mio.music.bean.MusicBean;
import com.mio.music.databinding.ItemVpPlayBinding;
import com.mio.music.databinding.ViewPlayBinding;
import com.mio.music.manager.MusicManager;
import com.mio.music.utils.DrawableUtils;
import com.mio.music.utils.LiveDataBus;
import com.mio.music.utils.PlayStatusUtils;

import java.util.List;

import lombok.Getter;

@Getter
public class PlayView extends BottomPopupView {
    private static final String TAG = "PlayView";

    private ViewPlayBinding mDataBinding;
    private BasePageAdapter<MusicBean, ItemVpPlayBinding> pageAdapter;

    public PlayView(Context context) {
        super(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void addInnerContent() {
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.view_play, bottomPopupContainer, false);

        bottomPopupContainer.addView(mDataBinding.getRoot());

        pageAdapter = new BasePageAdapter<MusicBean, ItemVpPlayBinding>(
                getContext(), R.layout.item_vp_play) {
            @Override
            protected void bind(MusicBean bean, int position, ItemVpPlayBinding binding) {
                if (bean == null) return;
                binding.tvTitle.setText(bean.getTitle());

                // 准备一个纯色的 Drawable，可以是一个颜色块
                Drawable drawable = DrawableUtils.getRandomDrawable(bean.getTitle().hashCode() % 10);

                // Glide 加载纯色圆形图片
                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new CircleCrop());

                Glide.with(getContext())
                        .load(drawable)
                        .apply(requestOptions)
                        .into(binding.imgRecord);

                LiveDataBus.get().with(Constants.musicProgress, Float.class)
                        .observe(PlayView.this, progress -> {
                            float rotation = position == MusicManager.getInstance().getCurrentIndex()
                                    ? progress * 18 : 0;

                            binding.tvTitle.setRotation(rotation);
                            binding.imgRecord.setRotation(rotation);
                        });
            }
        };
        mDataBinding.vp.setAdapter(pageAdapter);
//        mDataBinding.vp.setVerticalHandle(mDataBinding.getRoot());

        // 实现vp区域可以上下及左右同时拖动 仍然抖动
        mDataBinding.vp.setOnTouchListener(new OnTouchListener() {
            private static final float MIN_DELTA = 10;
            private float startY;
            private float startX;
            private int direction = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != MotionEvent.ACTION_UP
                        && event.getAction() != MotionEvent.ACTION_CANCEL) {
                    if (direction == 0) {
                        return false;
                    } else if (direction == 1) {
                        bottomPopupContainer.onTouchEvent(event);
                        return true;
                    }
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bottomPopupContainer.onTouchEvent(event);
                        startX = event.getX();
                        startY = event.getY();
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getX() - startX;
                        float deltaY = event.getY() - startY;

                        Log.d(TAG, "onTouchEvent: dx : " + deltaX + ",dy : " + deltaY);
                        if (Math.abs(deltaY) > Math.abs(deltaX) && Math.abs(deltaY) >= MIN_DELTA) {
                            bottomPopupContainer.onTouchEvent(event);
                            direction = 1;
                            return true;
                        } else if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) >= MIN_DELTA) {
                            direction = 0;
                            return false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        direction = -1;
                        startX = 0;
                        startY = 0;
                        bottomPopupContainer.onTouchEvent(event);
                        break;
                }
                return false;
            }
        });

        mDataBinding.imgNext.setOnClickListener(v -> mDataBinding.vp.setCurrentItem(mDataBinding.vp.getCurrentItem() + 1));
        mDataBinding.imgPre.setOnClickListener(v -> mDataBinding.vp.setCurrentItem(mDataBinding.vp.getCurrentItem() - 1));

        // 这里用于不可见的时候同步状态
        LiveDataBus.get().with(Constants.playingPath, String.class).observe(this, s -> {
            if (isShow()) return;
            List<MusicBean> data = pageAdapter.getData();
            for (int i = 0; i < data.size(); i++) {
                mDataBinding.vp.setCurrentItem(i, false);
                currentIndex = i;
            }
        });
        mDataBinding.vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override // 仅仅处理可见的时候 页面发生变化的时候切歌
            public void onPageSelected(int position) {
                if (!isShow()) return;
                if (MusicManager.getInstance().getCurrentIndex() != position)
                    MusicManager.getInstance().play(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        PlayStatusUtils.bind(this,
                mDataBinding.imgPlay,
                R.drawable.full_play,
                R.drawable.full_pause);


    }

    private int currentIndex = -1;

    @Override
    protected void beforeShow() {
        super.beforeShow();
        if (currentIndex == -1) {
            // 显示的时候获取播放列表及当前的播放下标 把它刷到界面里
            pageAdapter.setData(MusicManager.getInstance().getPlayList());
            mDataBinding.vp.setCurrentItem(MusicManager.getInstance().getCurrentIndex(),
                    false);
        }
    }

    @Override
    protected void onShow() {
        super.onShow();

    }


}