package com.android.cache;

import android.content.Context;
import android.os.Environment;

import com.android.utils.LogUtil;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * created by jiangshide on 2019-06-15.
 * email:18311271399@163.com
 */
public final class StorageUitls {
    protected static final String INDIVIDUAL_DIR_NAME = "video-cache";

    public static File getIndividualCacheDirectory(Context context){
        File cacheDir = getCacheDirectory(context,true);
        return new File(cacheDir,INDIVIDUAL_DIR_NAME);
    }

    private static File getCacheDirectory(Context context,boolean preferExternal){
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        }catch (NullPointerException e){
            externalStorageState = "";
        }
        if(preferExternal && MEDIA_MOUNTED.equals(externalStorageState)){
            appCacheDir = getExternalCacheDir(context);
        }
        if(appCacheDir == null){
            appCacheDir = context.getCacheDir();
        }
        if(appCacheDir == null){
            String cacheDirPath = "/data/data/"+context.getPackageName()+"/cache/";
            LogUtil.w("Can't define system cache directory! '"+cacheDirPath+"%s' will be used.");
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context){
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(),"Android"),"Data");
        File appCacheDir = new File(new File(dataDir,context.getPackageName()),"cache");
        if(!appCacheDir.exists()){
            if(!appCacheDir.mkdir()){
                LogUtil.w("Unable to create external cache directory");
                return null;
            }
        }
        return appCacheDir;
    }

    /**
     * delete directory
     */
    public static boolean deleteFiles(File root) {
        File[] files = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.isDirectory() && f.exists()) { // 判断是否存在
                    if (!f.delete()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * delete file
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                return file.delete();
            } else {
                String[] filePaths = file.list();

                if (filePaths != null) {
                    for (String path : filePaths) {
                        deleteFile(filePath + File.separator + path);
                    }
                }
                return file.delete();
            }
        }
        return true;
    }
}
