package com.bj.eduteacher.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.Util;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.List;

/**
 * Created by zz379 on 2017/9/27.
 */

public class ShareHelp {

    private IWXAPI api;
    private Context appContext;
    private static ShareHelp instance;

    public synchronized static ShareHelp getInstance() {
        if (null == instance) {
            instance = new ShareHelp();
        }
        return instance;
    }

    private ShareHelp() {

    }

    public void init(Context context) {
        appContext = context;
        // 获取IWXAPI 的实例
        api = WXAPIFactory.createWXAPI(context, MLProperties.APP_DOUHAO_TEACHER_ID, true);
        // 将应用的app ID注册到微信
        api.registerApp(MLProperties.APP_DOUHAO_TEACHER_ID);
        // api = WXAPIFactory.createWXAPI(context, MLProperties.APP_DOUHAO_TEACHER_ID);
    }

    public void showShareDialog(Context context, View popView) {

        final ScrollView mScrollView = (ScrollView) popView.findViewById(R.id.mScrollView);
        final AlertDialog shareDialog = new AlertDialog.Builder(context).create();
        shareDialog.setCanceledOnTouchOutside(false);
        shareDialog.show();
        shareDialog.getWindow().setContentView(popView);
        shareDialog.getWindow().findViewById(R.id.iv_shareSession).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToSession(shareDialog, mScrollView);
            }
        });
        shareDialog.getWindow().findViewById(R.id.iv_shareTimeline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToTimeline(shareDialog, mScrollView);
            }
        });
        shareDialog.getWindow().findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();
            }
        });
    }

    /**
     * 分享到微信
     */
    private void shareToSession(AlertDialog shareDialog, ScrollView mScrollView) {
        if (!isWeixinAvilible(appContext)) {
            Toast.makeText(appContext, "抱歉！您还没有安装微信", Toast.LENGTH_SHORT).show();
            return;
        }
        if (shareDialog.isShowing() && mScrollView != null) {
            Bitmap shareBmp = ScreenUtils.compressImage(ScreenUtils.getBitmapByView(mScrollView));
            shareDialog.dismiss();

            WXImageObject imgObject = new WXImageObject(shareBmp);
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObject;
            // 设置缩略图
            if (shareBmp != null && !shareBmp.isRecycled()) {
                Bitmap thumbBmp = Bitmap.createScaledBitmap(shareBmp, shareBmp.getWidth() / 10,
                        shareBmp.getHeight() / 10, true);
                // msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
                shareBmp.recycle();
            }
            // 构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            // transaction 字段用于唯一标示一个请求
            req.transaction = buildTransaction("img");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;

            // 调用api接口发送数据到微信
            api.sendReq(req);
        }
    }

    /**
     * 分享到朋友圈
     */
    private void shareToTimeline(AlertDialog shareDialog, ScrollView mScrollView) {
        if (!isWeixinAvilible(appContext)) {
            Toast.makeText(appContext, "抱歉！您还没有安装微信", Toast.LENGTH_SHORT).show();
            return;
        }
        if (shareDialog.isShowing() && mScrollView != null) {
            Bitmap shareBmp = ScreenUtils.compressImage(ScreenUtils.getBitmapByView(mScrollView));
            shareDialog.dismiss();

            WXImageObject imgObject = new WXImageObject(shareBmp);
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObject;
            // 设置缩略图
            if (shareBmp != null && !shareBmp.isRecycled()) {
                Bitmap thumbBmp = Bitmap.createScaledBitmap(shareBmp, 150,
                        shareBmp.getHeight() * 150 / shareBmp.getWidth(), true);
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
                shareBmp.recycle();
            }
            // 构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            // transaction 字段用于唯一标示一个请求
            req.transaction = buildTransaction("img");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneTimeline;

            // 调用api接口发送数据到微信
            api.sendReq(req);
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /***
     * 检查是否安装了微信
     * @param context
     * @return
     */
    private boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }
}
