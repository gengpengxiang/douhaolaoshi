package com.bj.eduteacher.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018/6/13 0013.
 */

public class PermissionUtils {

    public static Context context;
    public static boolean flag = false;

    public static boolean checkCameraPermission(){
        RxPermissions rxPermissions = new RxPermissions((Activity) context);
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Boolean success) {
                        if (success) {
                            flag = true;
                        } else {
//                            T.showShort(getActivity(), "未获取到相机权限");
                            flag = false;
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        return flag;
    }
}
