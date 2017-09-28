package com.bj.eduteacher.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bj.eduteacher.tool.UIUtils;

/**
 * Glide图像裁剪
 */
public class GlideCircleTransform extends BitmapTransformation {
    public GlideCircleTransform(Context context){
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return UIUtils.createCircleImage(toTransform, 0);
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
