package com.mio.music;

import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.mio.basic.BaseBottomActivity;
import com.mio.music.custom.MiniView;
import com.mio.music.custom.PlayView;
import com.mio.music.fragment.Fragment2;
import com.mio.music.fragment.MainFragment;
import com.mio.music.fragment.MineFragment;
import com.mio.music.utils.DensityUtils;
import com.mio.music.utils.LiveDataBus;

public class MainActivity extends BaseBottomActivity {


    private MiniView miniView;
    private BasePopupView playView;

    @Override
    protected void initFragmentList() {
        addFragment(R.id.main, new MainFragment());
        addFragment(R.id.music, new Fragment2());
        addFragment(R.id.mine, new MineFragment());
        mDataBinding.vp.setOffscreenPageLimit(2);

        initMini();
        initObserver();
    }

    private void initMini() {
        mDataBinding.getRoot().postDelayed(() -> {
            miniView = new MiniView(mContext);
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT

            );
            layoutParams.bottomToBottom = mDataBinding.vp.getId();
            miniView.setLayoutParams(layoutParams);
            ConstraintLayout constraintLayout = (ConstraintLayout) mDataBinding.bnv.getParent();
            constraintLayout.addView(miniView);
            miniView.setVisibility(View.GONE);
            miniView.setAlpha(0);
        }, 200);

        LiveDataBus.get().with(Constants.playingPath, String.class).observe(this, s -> {
            if (s != null) {
                miniView.setVisibility(View.VISIBLE);
                ObjectAnimator.ofFloat(miniView, "alpha",
                        miniView.getAlpha(), 1)
                        .setDuration(2_000)
                        .start();
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        playView = new XPopup.Builder(this)
                .asCustom(new PlayView(this));
        playView.popupInfo.isDismissOnTouchOutside = false;
        playView.popupInfo.animationDuration = 800;
    }

    private void initObserver() {
        LiveDataBus.get().with(Constants.showPlay, Boolean.class).observe(this, show -> {
            if (show) {
                playView.show();
            } else {
                playView.smartDismiss();
            }
        });
    }

    @Override
    protected void onSwiftFragment(int index) {
        mDataBinding.vp.setCurrentItem(index, true);
    }

    @Override
    protected int getMenuRes() {
        return R.menu.menu_bottom_navigation;
    }
}