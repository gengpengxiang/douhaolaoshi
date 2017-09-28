package com.bj.eduteacher.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by he on 2016/12/4.
 */

public class BitmapUtils {
    /**
     * view 转换成 bitmap 通过 DrawingCache的方法
     *
     * @param view
     * @return
     */
    public static Bitmap getBitmapFromViewByDrawingCache(Context context, View view) {
        // 启用绘图信息
        view.setDrawingCacheEnabled(true);
        //调用下面这个方法非常重要，如果没有调用这个方法，得到的bitmap为null
        view.measure(View.MeasureSpec.makeMeasureSpec(DensityUtils.dp2px(context, 57), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(DensityUtils.dp2px(context, 70), View.MeasureSpec.EXACTLY));
        //这个方法也非常重要，设置布局的尺寸和位置
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        //获得绘图缓存中的Bitmap
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * view 转换成 bitmap 通过 canvas
     *
     * @param view
     * @return
     */
    public static Bitmap getBitmapFromViewByCanvas(View view) {
        //调用下面这个方法非常重要，如果没有调用这个方法，得到的bitmap为null
        view.measure(View.MeasureSpec.makeMeasureSpec(256, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(256, View.MeasureSpec.EXACTLY));
        //这个方法也非常重要，设置布局的尺寸和位置
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        //生成bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.RGB_565);
        //利用bitmap生成画布
        Canvas canvas = new Canvas(bitmap);
        //把view中的内容绘制在画布上
        view.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap 转换成 二进制
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        byte[] data = baos.toByteArray();
        baos.close();
        inStream.close();
        return data;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    private static Bitmap compressImage(Bitmap image, Bitmap.CompressFormat format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(format, 100, baos);
        int options = 90;
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            image.compress(format, options, baos);
            options -= 10;
            if (options < 10) { // 小于10就退出
                break;
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    /**
     * 图片按比例大小压缩方法（根据路径获取图片并压缩）
     *
     * @param srcPath
     * @return
     */
    private static Bitmap getImage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap, getBitmapCompressFormat(srcPath));// 压缩好比例大小后再进行质量压缩
    }

    /**
     * 将压缩的bitmap保存到SDCard卡临时文件夹，用于上传
     *
     * @param filename
     * @param bit
     * @return
     */
    private static String saveMyBitmap(String filename, Bitmap bit, Bitmap.CompressFormat format) {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/laopai/";
        String filePath = baseDir + filename;
        File dir = new File(baseDir);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File f = new File(filePath);
        try {
            f.createNewFile();
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(f);
            bit.compress(format, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return filePath;
    }

    private static Bitmap.CompressFormat getBitmapCompressFormat(String path) {
        if (path.substring(path.lastIndexOf(".") + 1).toLowerCase().equals("png")) {
            return Bitmap.CompressFormat.PNG;
        } else if (path.substring(path.lastIndexOf(".") + 1).toLowerCase().equals("webp")) {
            return Bitmap.CompressFormat.WEBP;
        } else {
            return Bitmap.CompressFormat.JPEG;
        }
    }

    /**
     * 压缩上传路径
     *
     * @param path
     * @return
     */
    public static String compressImageUpload(String path) {
        String filename = path.substring(path.lastIndexOf("/") + 1);
        Bitmap image = getImage(path);
        return saveMyBitmap(filename, image, getBitmapCompressFormat(path));
    }


    /**
     * 清除缓存文件
     */
    public static void deleteCacheFile() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/laopai/");
        RecursionDeleteFile(file);
    }

    /**
     * 递归删除
     */
    private static void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }

    /**
     * 按照指定标准修改图片的尺寸和大小
     *
     * @param filePath
     * @return
     */
    public static String changeFileSize(String filePath) {
        // 读取图片
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, newOpts); // 此时bitmap为null
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        newOpts.inSampleSize = w / 100;// 设置缩放比例
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_4444;    // 默认是Bitmap.Config.ARGB_88887 i
        bitmap = BitmapFactory.decodeFile(filePath, newOpts);
        // 设置图片尺寸为280px
        bitmap = Bitmap.createScaledBitmap(bitmap, 280, 280, true);
        // 修改图片的清晰度
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap.CompressFormat format = getBitmapCompressFormat(filePath);
        bitmap.compress(format, 100, baos);
        int options = 90;
        while (baos.toByteArray().length / 1024 > 20) {
            baos.reset();
            bitmap.compress(format, options, baos);
            Log.i("way", "清晰度为：" + options + " 图片大小为：" + baos.toByteArray().length + "B");
            options -= 10;
            if (options < 10) { // 小于10就退出
                Log.i("way", "清晰度已经小于10");
                break;
            }
        }
        // 保存图片
        String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImageSelector/";
        String newFilePath = baseDir + filename;
        File dir = new File(baseDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File f = new File(newFilePath);
        try {
            f.createNewFile();
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(f);
            bitmap.compress(format, options, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        // 返回暂存文件地址
        return newFilePath;
    }
}
