package com.bj.eduteacher.community.publish.view;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.community.main.view.CustomPopDialog;
import com.bj.eduteacher.community.publish.model.ArticleContent;
import com.bj.eduteacher.community.publish.presenter.PublishPresenter;
import com.bj.eduteacher.community.publish.richeditor.SEditorData;
import com.bj.eduteacher.community.publish.richeditor.SortRichEditor;
import com.bj.eduteacher.community.utils.Base64Util;
import com.bj.eduteacher.community.utils.SharedPreferencesUtil;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.zzimgselector.utils.ScreenUtils;
import com.bj.eduteacher.zzimgselector.view.ImageSelectorActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

import static com.bj.eduteacher.zzimgselector.view.ImageSelectorActivity.REQUEST_IMAGE;

public class PublishActivity extends BaseActivity implements IViewPublish {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_share)
    TextView tvShare;
    @BindView(R.id.header_tv_title)
    TextView headerTvTitle;
    @BindView(R.id.header_ll_right)
    LinearLayout headerLlRight;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_addpic)
    Button btnAddpic;
    @BindView(R.id.richEditor)
    SortRichEditor richEditor;
    private String teacherPhoneNumber;
    private int maxPicNum = 10;
    private List<SEditorData> imgList = new ArrayList<>();
    private boolean canPublish = false;
    private long currTimeMillin = 0;
    private Unbinder unbind;
    private PublishPresenter presenter;
    private String type;
    private Observable<SEditorData> observable;
    private Observer<SEditorData> observer;
    private List<SEditorData> newEditList;
    private String unionid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        unbind = ButterKnife.bind(this);
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        presenter = new PublishPresenter(this, this);
        initToolBar();

        //initFirstEditText();
        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        observer = new Observer<SEditorData>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(SEditorData sEditorData) {


                for (int i = 0; i < richEditor.containerLayout.getChildCount(); i++) {
                    View view = richEditor.containerLayout.getChildAt(i);
                    if (i == richEditor.containerLayout.getChildCount() - 1) {
                        ViewGroup.LayoutParams lp = view.getLayoutParams();
                        lp.width = view.getWidth();
                        lp.height = 300;
                        view.setLayoutParams(lp);
                    } else {
//                        ViewGroup.LayoutParams lp = view.getLayoutParams();
//                        lp.width = ScreenUtils.getScreenWidth(PublishActivity.this);
//                        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                        view.setLayoutParams(lp);

                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
                        //lp.width = ScreenUtils.getScreenWidth(PublishActivity.this);
                        lp.width = view.getWidth();
                        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        view.setLayoutParams(lp);
                    }
                }
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onComplete() {
            }
        };
    }

    private void initFirstEditText() {
//        int index = richEditor.containerLayout.indexOfChild(richEditor.firstEdit);
//
////        if (richEditor.lastEditText == null && richEditor.containerLayout.getChildCount() == 1) {
//        if (index == richEditor.containerLayout.getChildCount() - 1) {
//            if (richEditor.firstEdit.hasFocus()) {
//                ViewGroup.LayoutParams lp = richEditor.firstEdit.getLayoutParams();
//                lp.width = ScreenUtils.getScreenWidth(PublishActivity.this);
//                //lp.width = richEditor.firstEdit.getWidth();
//                lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                richEditor.firstEdit.setLayoutParams(lp);
//            } else {
//                ViewGroup.LayoutParams lp = richEditor.firstEdit.getLayoutParams();
//                lp.width = ScreenUtils.getScreenWidth(PublishActivity.this);
//                lp.height = 300;
//                richEditor.firstEdit.setLayoutParams(lp);
//            }
//        }
    }

    @Override
    protected void initToolBar() {
        headerTvTitle.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        tvCancel.setVisibility(View.VISIBLE);
        headerLlRight.setVisibility(View.VISIBLE);
        tvShare.setVisibility(View.VISIBLE);

        tvCancel.setText("取消");
        tvTitle.setText("逗号老师");
        tvShare.setText("发布");
        //改变底部导航栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        }
    }

    @OnClick({R.id.tv_cancel, R.id.tv_share, R.id.btn_addpic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                cancelRemind();
                break;
            case R.id.tv_share:
                //T.showShort(this, "发布");
                List<SEditorData> editList = richEditor.buildEditData();
                Log.e("数据个数", editList.size() + "");
                canPublish = false;
                for (int i = 0; i < editList.size(); i++) {
                    if (editList.get(i).getInputStr() != null && editList.get(i).getInputStr().trim().length() > 9) {
                        canPublish = true;
                    }
                }

                if (canPublish) {
                    //T.showShort(this, "可以发布");
                    if (System.currentTimeMillis() - currTimeMillin < 1000) {
                        currTimeMillin = System.currentTimeMillis();
                    } else {
                        currTimeMillin = System.currentTimeMillis();
                        //T.showShort(this, "可以发布");
                        dealEditData(editList);
                    }

                } else {
                    T.showShort(this, "输入内容少于10个");
                }
                break;
            case R.id.btn_addpic:
                RxPermissions rxPermissions = new RxPermissions(this);
                rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull Boolean success) {
                                if (success) {
                                    actionSelectPhoto();
                                } else {
                                    T.showShort(PublishActivity.this, "未获取到相机、读写权限");
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });

                break;
        }
    }

    private void actionSelectPhoto() {
        if (richEditor.etTitle.hasFocus()) {
            T.showShort(this, "标题只能输入文字哦");
        } else {
            getPicNum();
            if (maxPicNum == 0) {
                T.showShort(this, "您最多只能选择10张照片");
            } else {
                ImageSelectorActivity.start(this, maxPicNum, ImageSelectorActivity.MODE_MULTIPLE
                        , true, false, false);
            }
        }
    }

    private void cancelRemind() {
        List<SEditorData> editList0 = richEditor.buildEditData();
        if (editList0.size() == 1 && editList0.get(0).getInputStr().trim().length() == 0 && richEditor.getTitleData().length() == 0) {
            finish();
        } else {
            if (System.currentTimeMillis() - currTimeMillin > 1000) {
                CustomPopDialog.Builder dialogBuild = new CustomPopDialog.Builder(PublishActivity.this);
                final CustomPopDialog dialog = dialogBuild.create2(R.layout.dialog_publish_cancel);
                dialog.setCanceledOnTouchOutside(false);
                TextView tv = (TextView) dialog.findViewById(R.id.title_text);
                tv.setText("确认要放弃发布？");
                dialog.findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        finish();
                    }
                });
                dialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
                currTimeMillin = System.currentTimeMillis();
            }
        }
    }

    private void getPicNum() {
        imgList.clear();
        List<SEditorData> editList = richEditor.buildEditData();
        for (int i = 0; i < editList.size(); i++) {
            if (editList.get(i).getImagePath() != null) {
                imgList.add(editList.get(i));
            }
        }
        maxPicNum = 10 - imgList.size();
    }

    /**
     * 负责处理编辑数据提交等事宜，请自行实现
     */
    private void dealEditData(List<SEditorData> editList) {

        String title = richEditor.getTitleData();

        if (TextUtils.isEmpty(title)) {
            T.showShort(this, "请输入标题");
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("usercode", teacherPhoneNumber);
            map.put("title", Base64Util.encode(title));
            //add
            map.put("unionid", unionid);

            String info = JSON.toJSONString(map);

            List<ArticleContent> contentList = new ArrayList<>();

            for (int i = 0; i < editList.size(); i++) {
                if (editList.get(i).getInputStr() != null) {

                    if (!TextUtils.isEmpty(editList.get(i).getInputStr())) {
                        contentList.add(new ArticleContent(Base64Util.encode(editList.get(i).getInputStr()), "", "", "1", String.valueOf(i + 1)));
                    }
                }
                if (editList.get(i).getImagePath() != null) {

                    String address = (String) SharedPreferencesUtil.get(getApplicationContext(), editList.get(i).getImagePath(), "erroraddress");
                    contentList.add(new ArticleContent("", address, address, "2", String.valueOf(i + 1)));
                }
            }
            String newsContent = constructNewsContent(contentList);
            presenter.publishArticle(info, newsContent, type);
        }
    }

    public String constructNewsContent(List<ArticleContent> content) {
        return JSON.toJSONString(content);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestory();
        unbind.unbind();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            cancelRemind();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE) {

            final ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            Collections.reverse(images);
            richEditor.addImageList(PublishActivity.this, images);

            presenter.uploadPic(images);

            observable = Observable.fromIterable(richEditor.buildEditData());
            //observable.subscribe(observer);
        }
    }


    @Override
    public void uploadPicSuccess() {
        //richEditor.addImageList(images);
        //T.showShort(this,"图片插入成功");
    }

    @Override
    public void uploadPicFail() {
        T.showShort(this, "图片上传失败");
    }

    @Override
    public void publishSuccess() {
        EventBus.getDefault().post(new MsgEvent("publishsuccess", type));
        finish();
    }

    @Override
    public void publishFail() {
        T.showShort(this, "发布失败");
    }
}
