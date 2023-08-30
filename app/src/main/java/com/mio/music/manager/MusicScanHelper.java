package com.mio.music.manager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.util.Log;

import com.mio.music.bean.MusicBean;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MusicScanHelper {

    public static List<String> mergeAndRemoveDuplicates(List<String> list1, List<String> list2) {
        Set<String> mergedSet = new HashSet<>(list1);
        mergedSet.addAll(list2);
        return new ArrayList<>(mergedSet);
    }

    private static MusicBean extractMusicInfo(String filePath) {
        MusicBean bean = new MusicBean();
        File musicFile = new File(filePath);
        MediaExtractor extractor = new MediaExtractor();

        if (musicFile.exists()) {
            try {
                extractor.setDataSource(filePath);

                int trackCount = extractor.getTrackCount();
                for (int i = 0; i < trackCount; i++) {
                    MediaFormat format = extractor.getTrackFormat(i);
                    String mime = format.getString(MediaFormat.KEY_MIME);

                    if (mime != null && mime.startsWith("audio/")) {
                        String title = musicFile.getName(); // 把这个换成获取 filePath 最后一个/后面并且在.之前的内容
//                        String artist = format.getString(MediaFormat.KEY_ARTIST);
//                        String album = format.getString(MediaFormat.KEY_ALBUM);
                        long duration = format.getLong(MediaFormat.KEY_DURATION);

                        bean.setPath(filePath);
                        bean.setTitle(title);
//                        bean.setArtist(artist);
//                        bean.setAlbum(album);
                        bean.setDuration(duration);

                        Log.d("MusicInfoExtractor", "Title: " + title +/* ", Artist: " + artist
                                + ", Album: " + album +*/ ", Duration: " + duration);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                extractor.release();
            }
        } else {
            Log.d("MusicInfoExtractor", "File not found: " + filePath);
        }
        return bean;
    }

    public static MusicBean requireMusicBean(@NotNull String filePath) {
        MusicBean bean = new MusicBean();
        File musicFile = new File(filePath);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        if (musicFile.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(musicFile);
                FileDescriptor fileDescriptor = inputStream.getFD();
                retriever.setDataSource(fileDescriptor);

                String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                bean.setPath(filePath);
                bean.setTitle(title);
                bean.setArtist(artist);
                bean.setAlbum(album);
                bean.setDuration(Long.parseLong(duration));

                Log.d("MusicInfoPrinter", "Title: " + title + ", Artist: " + artist
                        + ", Album: " + album + ", Duration: " + duration);
            } catch (RuntimeException | FileNotFoundException e) { // 到这里说明扫描这首歌出意外了
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                retriever.release();
            }
        } else {
            Log.d("MusicInfoPrinter", "File not found: " + filePath);
        }
        return bean;
    }

    private static final String[] MUSIC_PROJECTION = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
    };

    public static List<MusicBean> scanMusicByContentProvider(@NotNull Context context) {
        List<MusicBean> musicFileList = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                MUSIC_PROJECTION,
                MediaStore.Audio.Media.IS_MUSIC + " != 0",
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                // 这里你可以对文件后缀名进行过滤，如 mp3、flac 等
                if (filePath.toLowerCase().endsWith(".mp3") || filePath.toLowerCase().endsWith(".flac")) {
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                    MusicBean bean = new MusicBean();
                    bean.setPath(filePath);
                    bean.setTitle(title);
                    bean.setArtist(artist);
                    bean.setAlbum(album);
                    bean.setDuration(duration);

                    // 如果是音乐文件，将其添加到列表中
                    musicFileList.add(bean);
                }
            }
            cursor.close();
        }

        return musicFileList;
    }

    private List<MusicBean> scanMusicByContentProviderAndReturnMusicBean() {
        return null;
    }

    /**
     * 在指定目录下查找所有带有指定后缀名的文件
     *
     * @param directoryPath 目录
     * @param extensions    后缀名
     */
    public static List<String> findFilesWithExtensions(String directoryPath, String... extensions) {
        List<String> matchingFiles = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String filePath = file.getAbsolutePath();
                        for (String extension : extensions) {
                            if (filePath.toLowerCase().endsWith("." + extension.toLowerCase())) {
                                matchingFiles.add(filePath);
                                break;
                            }
                        }
                    } else if (file.isDirectory()) {
                        matchingFiles.addAll(findFilesWithExtensions(file.getAbsolutePath(), extensions));
                    }
                }
            }
        }

        return matchingFiles;
    }

    public static List<String> wrap(List<MusicBean> list) {
        List<String> l = new ArrayList<>();
        for (MusicBean bean : list) {
            l.add(bean.getPath());
        }
        return l;
    }

}
