package com.bj.eduteacher.community.publish.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.community.publish.model.UploadResult;
import com.bj.eduteacher.community.publish.view.IViewPublish;
import com.bj.eduteacher.community.utils.SharedPreferencesUtil;
import com.bj.eduteacher.presenter.Presenter;
import com.bj.eduteacher.utils.FileSizeUtil;
import com.bj.eduteacher.utils.PicUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;
import static com.bj.eduteacher.api.Urls.NEWSADD;
import static com.bj.eduteacher.api.Urls.TIMG;

/**
 * Created by Administrator on 2018/4/27 0027.
 */

public class PublishPresenter extends Presenter {

    private Context context;
    private IViewPublish iView;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private String newPath;

    public PublishPresenter(Context context, IViewPublish iView) {
        this.context = context;
        this.iView = iView;
    }

    public void uploadPic(final List<String> pathList) {

        Observable observable = Observable.fromIterable(pathList);

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            //观察者接收到通知,进行相关操作
            public void onNext(final String aLong) {
                Log.e("源文件路径", aLong);
                Luban.with(context)
                        .load(aLong)//传入原图
                        .ignoreBy(100)//不压缩的阈值，单位为K

                        .setTargetDir("/storage/emulated/0/Android/data/")

//                        .setRenameListener(new OnRenameListener() {//压缩前重命名接口
//                            @Override
//                            public String rename(String filePath) {
//
//                                Log.e("重命名",filePath);
//                                return "abc";
//                            }
//                        })

                        .setCompressListener(new OnCompressListener() {//压缩回调接口
                            @Override
                            public void onStart() {
                            }
                            @Override
                            public void onSuccess(final File file) {
                                try {
                                    String size = FileSizeUtil.FormetFileSize(FileSizeUtil.getFileSize(file));
                                    Log.e("鲁班压缩后大小", size);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Log.e("压缩后文件路径", file.getPath());
                                Log.e("压缩后文件名", file.getName());
                                Log.e("压缩后文件路径头", file.getAbsolutePath());

                                //add

                                //add
                                long time = System.currentTimeMillis();
                                String newPath2 = PicUtils.renameFile(file.getPath(), file.getPath().substring(0, file.getPath().lastIndexOf("/") + 1) + time+".jpg");
                                Log.e("重命名后的name",newPath2);
                                OkGo.<String>post(BASE_URL + TIMG)
                                        .params("appkey", MLConfig.HTTP_APP_KEY)
                                        .params("userfile", new File(newPath2))
                                        //.params("userfile", file)
                                        .execute(new StringCallback() {
                                            @Override
                                            public void onSuccess(Response<String> response) {
                                                Log.e("图片上传返回結果", response.body().toString());
                                                String str = response.body().toString();
                                                //Gson gson = new Gson();
                                                //UploadResult result = gson.fromJson(str, UploadResult.class);
                                                UploadResult result = JSON.parseObject(str, new TypeReference<UploadResult>() {
                                                });
                                                if (result.getRet().equals("1")) {
                                                    String img = result.getData().getImg();
                                                    SharedPreferencesUtil.put(context, aLong, img);
                                                    iView.uploadPicSuccess();

                                                }
                                                if (result.getRet().equals("2")) {
                                                    iView.uploadPicFail();
                                                }

                                            }
                                        });
                            }
                            @Override
                            public void onError(Throwable e) {
                            }
                        })
                        .launch();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
        observable.subscribe(observer);

    }

    public static void saveJPG_After(Bitmap bitmap, String name) {
        File file = new File(name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertToJpg(String pngFilePath, String jpgFilePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(pngFilePath);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(jpgFilePath))) {
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos)) {
                bos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jpgFilePath;
    }


    public void publishArticle(final String info, final String content, final String type) {
        if (type.equals("find")) {//发现模块发布文章
            OkGo.<String>post(BASE_URL + NEWSADD)
                    .params("appkey", MLConfig.HTTP_APP_KEY)
                    .params("newsinfo", info)
                    .params("newscontent", content)
                    //.params("unionid",unionid)
                    .execute(new com.lzy.okgo.callback.StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            iView.publishSuccess();
                        }

                        @Override
                        public void onError(Response<String> response) {
                            iView.publishFail();
                        }
                    });
        } else {//小组话题发布文章
            OkGo.<String>post(BASE_URL + NEWSADD)
                    .params("appkey", MLConfig.HTTP_APP_KEY)
                    .params("newsinfo", info)
                    .params("newscontent", content)
                    .params("newsgroup", type)
                    //.params("unionid",unionid)

                    .execute(new com.lzy.okgo.callback.StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            iView.publishSuccess();
                        }

                        @Override
                        public void onError(Response<String> response) {
                            iView.publishFail();
                        }
                    });
        }

    }

    @Override
    public void onDestory() {
        context = null;
    }
}
