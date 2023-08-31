package com.mio.music.custom;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.mio.music.manager.MusicManager;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;

@Getter
public abstract class BasePageAdapter<O extends Object, T extends ViewDataBinding> extends PagerAdapter {
    private static final String TAG = "BasePageAdapter";
    private List<O> data = new ArrayList<>();
    private Context mContext;
    private int itemLayoutId;

    public BasePageAdapter(Context mContext, int itemLayoutId) {
        this.mContext = mContext;
        this.itemLayoutId = itemLayoutId;
    }

    public BasePageAdapter(List<O> data, Context mContext, int itemLayoutId) {
        this.data = data;
        this.mContext = mContext;
        this.itemLayoutId = itemLayoutId;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d(TAG, "instantiateItem: " + position + "data size : " + data.size());
        T binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), getItemLayoutId(), container, false);
        O bean = null;
        if (data.size() != 0) bean = data.get(position % data.size());
        bind(bean, position, binding);
        container.addView(binding.getRoot());
        return binding.getRoot();
    }

    public void setData(List<O> data) {
        Log.d(TAG, "setData: ");
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    protected abstract void bind(O bean, int position, T binding);

    public void setCurrentItem(@NonNull ViewPager vp, int index, boolean smooth) {
        int currentItem = vp.getCurrentItem();
        for (int i = currentItem - data.size() + 1; i < currentItem + data.size(); i++) {
            if (i < 0) continue;
            if (i % data.size() == index) {
                vp.setCurrentItem(i, smooth);
            }
        }
    }
}
