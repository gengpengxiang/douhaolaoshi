package com.bj.eduteacher.utils;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.Priority;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;

/**
 * Created by zz379 on 2017/4/18.
 */

public class FrescoUtils {
    private static final String TAG = FrescoUtils.class.getSimpleName();

    private FrescoUtils() {

    }

    public static void TrimMemory(int level) {
        try {
            if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) { // 60
                ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
            }
        } catch (Exception e) {

        }

    }

    public static void clearAllMemoryCaches() {
        ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
    }

    //Fresco
    public static void display(SimpleDraweeView draweeView, String url) {
        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "display: error the url is empty");
            return;
        }
        draweeView.setImageURI(url);
    }

    public static void display(SimpleDraweeView draweeView, File file) {
        if (file == null) {
            Log.e(TAG, "display: error the file is empty");
            return;
        }
        Uri uri = Uri.fromFile(file);
        if (uri == null) return;
        draweeView.setImageURI(uri);
    }

    public static void display(SimpleDraweeView draweeView, Uri uri) {
        if (uri == null) {
            Log.e(TAG, "display: error the url is empty");
            return;
        }
        draweeView.setImageURI(uri);
    }

    public static void display(SimpleDraweeView draweeView, Uri uri, int width, int height) {
        if (uri == null) {
            Log.e(TAG, "display: error the url is empty");
            return;
        }
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setLocalThumbnailPreviewsEnabled(true)
                .build();

        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(draweeView.getController())
                .setImageRequest(request)
                .build();

        draweeView.setController(controller);
    }


    public static void display(SimpleDraweeView draweeView, String url, int width, int height) {
        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "display: error the url is empty");
            return;
        }
        Uri uri = Uri.parse(url);
        display(draweeView, uri, width, height);
    }

    public static void display(SimpleDraweeView draweeView, Uri uri, boolean isSmall) {
        if (uri == null) {
            Log.e(TAG, "display: error the url is empty");
            return;
        }
        ImageRequest request;
        if (isSmall) {
            request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();
        } else {
            request = ImageRequestBuilder.newBuilderWithSource(uri)

                    .setLocalThumbnailPreviewsEnabled(true)
                    .build();
        }

        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(draweeView.getController())
                .setImageRequest(request)
                .build();

        draweeView.setController(controller);
    }

    public static void display(SimpleDraweeView draweeView, String url, boolean isSmall) {
        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "display: error the url is empty");
            return;
        }
        Uri uri = Uri.parse(url);
        display(draweeView, uri, isSmall);
    }

    public static void prefetchPhoto(Context context, Uri uri, int width, int height) {
        if (uri == null) {
            Log.e(TAG, "display: error the url is empty");
            return;
        }
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setRequestPriority(Priority.LOW)
                .setLocalThumbnailPreviewsEnabled(true)
                .build();

        Fresco.getImagePipeline().prefetchToDiskCache(request, context);
    }

    public static void prefetchPhoto(Context context, String url, int width, int height) {
        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "display: error the url is empty");
            return;
        }
        Uri uri = Uri.parse(url);
        prefetchPhoto(context, uri, width, height);
    }

    /**
     * 获取Fresco磁盘缓存中的图片
     *
     * @param cacheKey
     * @return
     */
    public static File obtainCachedPhotoFile(CacheKey cacheKey) {
        File localFile = null;
        if (cacheKey != null) {
            if (ImagePipelineFactory.getInstance().getMainFileCache().hasKey(cacheKey)) {
                BinaryResource binaryResource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);

                localFile = ((FileBinaryResource) binaryResource).getFile();
            } else if (ImagePipelineFactory.getInstance().getSmallImageFileCache().hasKey(cacheKey)) {
                BinaryResource binaryResource = ImagePipelineFactory.getInstance().getSmallImageFileCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) binaryResource).getFile();
            }
        }
        return localFile;
    }

    // Fresco暂停与继续加载图片
    // Fresco.getImagePipeline().resume();
    // Fresco.getImagePipeline().pause();
    // ImagePipeline imagePipeline = Fresco.getImagePipeline();
    // 清空内存缓存（包括Bitmap缓存和未解码图片的缓存）
    // imagePipeline.clearMemoryCaches();
    // 清空硬盘缓存，一般在设置界面供用户手动清理
    // imagePipeline.clearDiskCaches();
    // 同时清理内存缓存和硬盘缓存
    // imagePipeline.clearCaches();

    /**
     * 下载图片
     *
     * @param context
     * @param picUrl
     * @param resizeOptions
     * @param bitmapDataSubscriber
     */
    public static void loadImage(Context context, String picUrl, ResizeOptions resizeOptions,
                                 BaseBitmapDataSubscriber bitmapDataSubscriber) {
        if (StringUtils.isEmpty(picUrl)) {
            return;
        }
        Uri uri = Uri.parse(picUrl);
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                .setLocalThumbnailPreviewsEnabled(true)
                .setResizeOptions(resizeOptions)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setProgressiveRenderingEnabled(false)
                .build();

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(bitmapDataSubscriber, CallerThreadExecutor.getInstance());
    }

    /**
     * BaseBitmapDataSubscriber 事例
     */
    /*BaseBitmapDataSubscriber subscriber = new
            BaseBitmapDataSubscriber() {
                @Override
                protected void onNewResultImpl(Bitmap bitmap) {
                    ViewSinglePhotoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadPb.setVisibility(View.GONE);
                        }
                    });
                    if (bitmap != null && photoView != null) {
                        final Bitmap arg2 = Bitmap.createBitmap(bitmap);
                        ImageCache.getInstance().put(picUrl, arg2);
                        ViewSinglePhotoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                photoView.setImageBitmap(arg2);
                            }
                        });
                    }
                }

                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                    ViewSinglePhotoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadPb.setVisibility(View.GONE);
                            photoView.setImageResource(R.drawable.default_pic);
                        }
                    });
                }
            };*/
}
