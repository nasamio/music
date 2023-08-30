package com.mio.music.manager;

import java.util.Random;

public class MusicHelper {
    private static MusicHelper instance;

    private MusicHelper() {
    }

    public static MusicHelper getInstance() {
        if (instance == null) instance = new MusicHelper();
        return instance;
    }

    private Random random = new Random();

    /**
     * 从0到size-1 返回一个不等于current index 的随机数
     */
    public int getRandom(int size, int currentIndex) {
        if (size <= 1) {
            throw new IllegalArgumentException("Size must be greater than 1.");
        }

        if (currentIndex < 0 || currentIndex >= size) {
            throw new IllegalArgumentException("Current index is out of range.");
        }

        int randomIndex;
        do {
            randomIndex = random.nextInt(size);
        } while (randomIndex == currentIndex);

        return randomIndex;
    }
}
