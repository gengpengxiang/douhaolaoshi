package com.bj.eduteacher.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.bj.eduteacher.zzokhttp.OkHttpUtils;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by zz379 on 2017/4/18.
 */

public class FrescoImagePipelineConfigFactory {
    private static final String IMAGE_PIPELINE_CACHE_DIR = "imagepipeline_cache";
    private static final String IMAGE_PIPELINE_CACHE_DIR_SMALL = "small_cache";

    private static ImagePipelineConfig sImagePipelineConfig;
    private static ImagePipelineConfig sOkHttpImagePipelineConfig;

    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();

    public static final int MAX_DISK_CACHE_SIZE = 300 * ByteConstants.MB;
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 3;

    /**
     * Creates config using android http stack as network backend.
     */
    public static ImagePipelineConfig getImagePipelineConfig(Context context) {
        if (sImagePipelineConfig == null) {
            ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context);
            configureCaches(configBuilder, context);
            sImagePipelineConfig = configBuilder.build();
        }
        return sImagePipelineConfig;
    }

    /**
     * Creates config using OkHttp as network backed.
     */
    public static ImagePipelineConfig getOkHttpImagePipelineConfig(Context context) {
        if (sOkHttpImagePipelineConfig == null) {
            ImagePipelineConfig.Builder configBuilder = OkHttpImagePipelineConfigFactory
                    .newBuilder(context, OkHttpUtils.getInstance().getOkHttpClient());
            configureCaches(configBuilder, context);
            sOkHttpImagePipelineConfig = configBuilder.build();
        }
        return sOkHttpImagePipelineConfig;
    }

    /**
     * Configures disk and memory cache not to exceed common limits
     */
    private static void configureCaches(ImagePipelineConfig.Builder configBuilder, Context context) {
        //这里是添加Fresco的日志
        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());

        //当内存紧张时采取的措施
        MemoryTrimmableRegistry memoryTrimmableRegistry = NoOpMemoryTrimmableRegistry.getInstance();
        memoryTrimmableRegistry.registerMemoryTrimmable(new MemoryTrimmable() {
            @Override
            public void trim(MemoryTrimType trimType) {
                final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();
                Log.e("Fresco", String.format("onCreate suggestedTrimRatio : %d", suggestedTrimRatio));
                if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio
                        ) {
                    //清除内存缓存
                    Fresco.getImagePipeline().clearMemoryCaches();
//                Fresco.getImagePipeline().clearCaches();
                }
            }
        });

        //小图片的磁盘配置,用来储存用户头像之类的小图
        DiskCacheConfig diskSmallCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(context.getCacheDir())//缓存图片基路径
                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR_SMALL)//文件夹名
                .setMaxCacheSize(20 * ByteConstants.MB)//默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(10 * ByteConstants.MB)//缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(5 * ByteConstants.MB)//缓存的最大大小,当设备极低磁盘空间
                .build();

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(getExternalCacheDir(context))
                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                .build();

        configBuilder
                .setDownsampleEnabled(true)
                .setResizeAndRotateEnabledForNetwork(true)
                .setRequestListeners(requestListeners)
                .setBitmapMemoryCacheParamsSupplier(new FrescoLolipopBitmapMemoryCacheSupplier((ActivityManager) context.getSystemService(ACTIVITY_SERVICE)))
                .setMemoryTrimmableRegistry(memoryTrimmableRegistry)
                .setMainDiskCacheConfig(diskCacheConfig)
                .setSmallImageDiskCacheConfig(diskSmallCacheConfig)
                .setBitmapsConfig(Bitmap.Config.RGB_565);

    }

    public static File getExternalCacheDir(final Context context) {
        if (hasExternalCacheDir())
            return context.getExternalCacheDir();

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return createFile(Environment.getExternalStorageDirectory().getPath() + cacheDir, "");
    }

    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static File createFile(String folderPath, String fileName) {
        File destDir = new File(folderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return new File(folderPath, fileName);
    }
}
