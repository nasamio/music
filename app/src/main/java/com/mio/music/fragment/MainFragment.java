package com.mio.music.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lxj.statelayout.StateLayout;
import com.mio.basic.BaseFragment;
import com.mio.music.Constants;
import com.mio.music.MainActivity;
import com.mio.music.R;
import com.mio.music.bean.MusicBean;
import com.mio.music.databinding.FragmentMainBinding;
import com.mio.music.databinding.ItemMusicMainBinding;
import com.mio.music.manager.MusicManager;
import com.mio.music.utils.DensityUtils;
import com.mio.music.utils.LiveDataBus;
import com.mio.music.utils.ToastUtils;
import com.mio.utils.LogUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.sql.NClob;
import java.util.List;

public class MainFragment extends BaseFragment<FragmentMainBinding> {

    private BaseQuickAdapter<MusicBean, BaseViewHolder> adapter;
    private StateLayout rvState;

    @Override
    protected void initView() {
        initRv();
        initObserver();
        checkPermission();
    }

    private void initRv() {
        rvState = new StateLayout(mContext)
                .wrap(mDataBinding.rv);
        rvState.showLoading();
        mDataBinding.rv.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        adapter = new BaseQuickAdapter<MusicBean, BaseViewHolder>(R.layout.item_music_main) {
            @Override
            protected void convert(BaseViewHolder helper, MusicBean item) {
                ItemMusicMainBinding itemBinding = DataBindingUtil.bind(helper.itemView);
                itemBinding.tvTitle.setText(item.getTitle());
                itemBinding.tvArtist.setText(item.getArtist());

                LiveDataBus.get().with(Constants.playingPath, String.class)
                        .observe((LifecycleOwner) mContext, s -> {
                            boolean isPlayingItem = s != null && s.equals(item.getPath());
                            int highColor = getResources().getColor(R.color.highlightTextColor);
                            int normalColor = getResources().getColor(R.color.textColor);
                            itemBinding.tvTitle.setTextColor(isPlayingItem ? highColor : normalColor);
                            itemBinding.tvArtist.setTextColor(isPlayingItem ? highColor : normalColor);
                        });

                int position = helper.getAdapterPosition();
                itemBinding.getRoot().setOnLongClickListener(v -> {
                    LogUtils.d("long click : " + position);
                    return true;
                });
                itemBinding.getRoot().setOnClickListener(v -> {
                    LogUtils.d("short click : " + position);

                    String playingPath = LiveDataBus.get().with(Constants.playingPath, String.class).getValue();
                    if (playingPath != null && playingPath.equals(item.getPath())) {
                        // 已经播放 再次点击
                        LiveDataBus.get().with(Constants.showPlay).postValue(true);
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MusicManager.getInstance().addToPlayList(adapter.getData());
                                MusicManager.getInstance().play(position);
                            }
                        }).start();
                    }
                });
            }
        };
        mDataBinding.rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
                int margin = DensityUtils.dp2px(mContext, 5);
                if (itemPosition == 0) {
                    outRect.set(margin, margin, margin, margin);
                } else {
                    outRect.set(margin, 0, margin, margin);
                }
            }
        });
        mDataBinding.rv.setAdapter(adapter);
    }

    private void initObserver() {
        // 使用live data bus 的消息总线模式来接收结果
        LiveDataBus.get().with(Constants.scanMusic, List.class)
                .observe((LifecycleOwner) mContext, list -> {
                    mDataBinding.getRoot().postDelayed(() -> {
                        if (list != null && !list.isEmpty()) {
                            adapter.setNewData(list);
                            rvState.showContent();
                        } else {
                            rvState.showEmpty();
                        }
                    }, 200); // 这个延时是用来确保接收数据晚于扫描完成 实测模拟器上扫描只需要200ms...
                });
    }

    @SuppressLint("WrongConstant")
    private void checkPermission() {
        // 检查读写q权限
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(permissions -> {
                    // Storage permission are allowed.
                    MusicManager.getInstance().init(mContext).scanMusic();
                })
                .onDenied(permissions -> {
                    ToastUtils.showShortToast(mContext, "应用需要文件权限，不然无法正常使用...");
                    mDataBinding.getRoot().postDelayed(() -> checkPermission(), 1_000);
                })
                .start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }
}
