package com.bj.eduteacher.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.bj.eduteacher.api.HttpUtilService;
import com.bj.eduteacher.model.MySelfInfo;
import com.bj.eduteacher.presenter.viewinface.UploadView;
import com.bj.eduteacher.tool.SxbLog;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.StringUtils;
import com.tencent.cos.COSClient;
import com.tencent.cos.COSClientConfig;
import com.tencent.cos.common.COSEndPoint;
import com.tencent.cos.model.COSRequest;
import com.tencent.cos.model.COSResult;
import com.tencent.cos.model.PutObjectRequest;
import com.tencent.cos.model.PutObjectResult;
import com.tencent.cos.task.listener.IUploadTaskListener;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Cos人图片上传类
 */
public class UploadHelper extends Presenter {
    private final String TAG = "PublishHelper";
    private final String bucket = "sxbbucket";
    private final String appid = "1253488539";

    private final static int THREAD_GET_SIG = 1;
    private final static int THREAD_UPLAOD = 2;
    private final static int THREAD_GETSIG_UPLOAD = 3;

    private final static int MAIN_CALL_BACK = 1;
    private final static int MAIN_PROCESS = 2;

    private Context mContext;
    private UploadView mView;
    private HandlerThread mThread;
    private Handler mHandler;
    private Handler mMainHandler;

    public UploadHelper(Context context, UploadView view) {
        mContext = context;
        mView = view;
        mThread = new HandlerThread("upload");
        mThread.start();
        mHandler = new Handler(mThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case THREAD_GET_SIG:
                        doUpdateSig();
                        break;
                    case THREAD_UPLAOD:
                        doUploadCover((String) msg.obj, true);
                        break;
                    case THREAD_GETSIG_UPLOAD:
                        doUpdateSig();
                        doUploadCover((String) msg.obj, false);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        mMainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                SxbLog.d(TAG, "handleMessage id:" + msg.what);
                switch (msg.what) {
                    case MAIN_CALL_BACK:
                        if (null != mView)
                            mView.onUploadResult(msg.arg1, (String) msg.obj);
                        break;
                    case MAIN_PROCESS:
                        if (null != mView)
                            mView.onUploadProcess(msg.arg1);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private String createNetUrl() {
        return "/" + MySelfInfo.getInstance().getId() + "_" + System.currentTimeMillis() + ".jpg";
    }

    private void doUpdateSig() {
        String sig = UserServerHelper.getInstance().getCosSig();
        MySelfInfo.getInstance().setCosSig(sig);
        // SxbLog.d(TAG, "doUpdateSig->get sig: " + sig);
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public boolean copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            SxbLog.e(TAG, "copy file failed!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void doUploadCover(final String path, boolean bRetry) {
        String sig = MySelfInfo.getInstance().getCosSig();
        if (TextUtils.isEmpty(sig)) {
            if (bRetry) {
                Message msg = new Message();
                msg.what = THREAD_GETSIG_UPLOAD;
                msg.obj = path;

                mHandler.sendMessage(msg);
            }
            return;
        }

        String tmpPath = path;
        if ("Xiaomi".equals(android.os.Build.MANUFACTURER)) { // 复制到tmp文件再上传(小米5机器上无法占用文件)
            tmpPath = path + "_tmp";
            copyFile(path, tmpPath);
        }

        //创建COSClientConfig对象，根据需要修改默认的配置参数
        final COSClientConfig config = new COSClientConfig();
        //设置园区
        config.setEndPoint(COSEndPoint.COS_GZ);

        //创建COSlient对象，实现对象存储的操作
        COSClient cos = new COSClient(mContext, appid, config, null);

        SxbLog.d(TAG, "upload cover: " + tmpPath);

        //上传文件
        PutObjectRequest putObjectRequest = new PutObjectRequest();
        putObjectRequest.setBucket(bucket);
        putObjectRequest.setCosPath(createNetUrl());
        putObjectRequest.setSrcPath(tmpPath);
        putObjectRequest.setSign(sig);
        putObjectRequest.setListener(new IUploadTaskListener() {
            @Override
            public void onSuccess(COSRequest cosRequest, COSResult cosResult) {
                PutObjectResult result = (PutObjectResult) cosResult;
                if (result != null) {
                    SxbLog.i(TAG, "upload succeed: " + result.url);
                    Message msg = new Message();
                    msg.what = MAIN_CALL_BACK;
                    msg.arg1 = 0;
                    msg.obj = result.source_url;

                    mMainHandler.sendMessage(msg);
                }
            }

            @Override
            public void onFailed(COSRequest COSRequest, final COSResult cosResult) {
                SxbLog.w(TAG, "upload error code: " + cosResult.code + " msg:" + cosResult.msg);
                if (-96 == cosResult.code) {  // 签名过期重试
                    Message msg = new Message();
                    msg.what = THREAD_GETSIG_UPLOAD;
                    msg.obj = path;

                    mHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = MAIN_CALL_BACK;
                    msg.arg1 = cosResult.code;
                    msg.obj = cosResult.msg;

                    mMainHandler.sendMessage(msg);
                }
            }

            @Override
            public void onProgress(COSRequest cosRequest, final long currentSize, final long totalSize) {
                SxbLog.d(TAG, "onUploadProgress: " + currentSize + "/" + totalSize);
                Message msg = new Message();
                msg.what = MAIN_PROCESS;
                msg.arg1 = (int) (currentSize * 100 / totalSize);

                mMainHandler.sendMessage(msg);
            }

            @Override
            public void onCancel(COSRequest cosRequest, COSResult cosResult) {

            }
        });

        PutObjectResult putObjectResult = cos.putObject(putObjectRequest);

        if (0 != putObjectResult.code) {
            Message msg = new Message();
            msg.what = MAIN_CALL_BACK;
            msg.arg1 = -1;
            msg.obj = "upload failed";

            mMainHandler.sendMessage(msg);
        }
    }

    public void updateSig() {
        mHandler.sendEmptyMessage(THREAD_GET_SIG);
    }

    public void uploadCover(String path) {
        Message msg = new Message();
        msg.what = THREAD_UPLAOD;
        msg.obj = path;

        mHandler.sendMessage(msg);
    }

    public void uploadPicture(final String path) {
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                String result = HttpUtilService.postPictureByUrl("", path);
                LL.i("上传图片结果：" + result);
                JSONObject resultObj = new JSONObject(result);
                String[] dataArray = new String[3];
                String errCode = resultObj.optString("ret");
                String errMsg = resultObj.optString("msg");
                String data = resultObj.optString("data");
                dataArray[0] = errCode;
                dataArray[1] = errMsg;
                if (!StringUtils.isEmpty(errCode) && "1".equals(errCode) &&
                        !StringUtils.isEmpty(data)) {
                    String pictureUrl = (new JSONObject(data)).optString("img");
                    dataArray[2] = pictureUrl;
                }
                e.onNext(dataArray);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String[] result) {
                        mView.onUploadResult(Integer.valueOf(result[0]), result[2]);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onDestory() {
        mView = null;
        mContext = null;
    }
}
