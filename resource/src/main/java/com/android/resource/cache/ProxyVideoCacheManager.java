package com.android.resource.cache;

import android.content.Context;
import com.android.cache.HttpProxyCacheServer;
import com.android.cache.StorageUitls;
import java.io.File;

public class ProxyVideoCacheManager {

    private static HttpProxyCacheServer sharedProxy;

    private ProxyVideoCacheManager() {
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        return sharedProxy == null ? (sharedProxy = newProxy(context)) : sharedProxy;
    }

    private static HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(512 * 1024 * 1024)       // 512MB for cache
                //缓存路径，不设置默认在sd_card/Android/data/[app_package_name]/cache中
//                .cacheDirectory()
                .build();
    }


    /**
     * 删除所有缓存文件
     * @return 返回缓存是否删除成功
     */
    public static boolean clearAllCache(Context context) {
        getProxy(context);
        return StorageUitls.deleteFiles(sharedProxy.getCacheRoot());
    }

    /**
     * 删除url对应默认缓存文件
     * @return 返回缓存是否删除成功
     */
    public static boolean clearDefaultCache(Context context, String url) {
        getProxy(context);
        File pathTmp = sharedProxy.getTempCacheFile(url);
        File path = sharedProxy.getCacheFile(url);
        return StorageUitls.deleteFile(pathTmp.getAbsolutePath()) &&
            StorageUitls.deleteFile(path.getAbsolutePath());

    }
}