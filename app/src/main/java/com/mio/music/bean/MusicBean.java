package com.mio.music.bean;

import java.util.Objects;

import lombok.Data;

@Data
public class MusicBean {
    private String path;//本地地址
    private String title;// 歌名
    private String artist;// 作者名
    private String album;// 预言家
    private long duration;// 时长

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicBean bean = (MusicBean) o;
        return Objects.equals(path, bean.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
