package com.bj.eduteacher.dialog;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bj.eduteacher.R;

public class UpdateAPPAlertDialog extends Dialog implements View.OnClickListener {

    private View mDialogView;
    private TextView mTitleTextView;
    private TextView mContentTextView;
    private String mTitleText;
    private String mContentText;
    private boolean mShowCancel;
    private String mCancelText;
    private String mConfirmText;
    private Button mConfirmButton;
    private Button mCancelButton;
    private FrameLayout mCloseButton;
    private OnSweetClickListener mCancelClickListener;
    private OnSweetClickListener mConfirmClickListener;
    private LinearLayout mContentMsgLayout;
    private LinearLayout mContentProgressLayout;
    private OnSweetClickListener mCancelDownloadListener;

    private TextView tvCancelDownload;
    private TextView tvDownloadProgress;
    private ProgressBar pbDownload;

    private AnimationSet mModalInAnim;
    private AnimationSet mModalOutAnim;
    private boolean mCloseFromCancel;
    public static final int NORMAL_TYPE = 0;

    private MyReceiver myReceiver;

    public static interface OnSweetClickListener {
        public void onClick(UpdateAPPAlertDialog sweetAlertDialog);
    }

    public UpdateAPPAlertDialog(Context context) {
        this(context, NORMAL_TYPE);
        mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.modal_in);
        mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.modal_out);
        mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.setVisibility(View.GONE);
                mDialogView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCloseFromCancel) {
                            UpdateAPPAlertDialog.super.cancel();
                        } else {
                            UpdateAPPAlertDialog.super.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public UpdateAPPAlertDialog(Context context, int alertType) {
        super(context, R.style.alert_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_update_dialog);

        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mContentMsgLayout = (LinearLayout) findViewById(R.id.loading);
        mContentProgressLayout = (LinearLayout) findViewById(R.id.progress);
        tvDownloadProgress = (TextView) findViewById(R.id.tv_downloadProgress);
        tvCancelDownload = (TextView) findViewById(R.id.tv_cancelDownload);
        pbDownload = (ProgressBar) findViewById(R.id.pb_progress);
        mTitleTextView = (TextView) findViewById(R.id.title_text);
        mContentTextView = (TextView) findViewById(R.id.content_text);
        mConfirmButton = (Button) findViewById(R.id.confirm_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mCloseButton = (FrameLayout) findViewById(R.id.fl_close);
        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mCloseButton.setOnClickListener(this);
        tvCancelDownload.setOnClickListener(this);

        setTitleText(mTitleText);
        setContentText(mContentText);
        setCancelText(mCancelText);
        setConfirmText(mConfirmText);

        // 注册广播接收者
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MY_BROADCAST");
        Log.i("way", "Dialog 中 注册广播接受者");
        getContext().registerReceiver(myReceiver, filter);
    }

    @Override
    public void dismiss() {
        Log.i("way", "Dialog 中 取消注册广播接受者");
        getContext().unregisterReceiver(myReceiver);
        super.dismiss();
    }

    public void startDownload() {
        mContentMsgLayout.setVisibility(View.GONE);
        mContentProgressLayout.setVisibility(View.VISIBLE);
        tvDownloadProgress.setText("正在为您更新 0%");
    }

    private void restore() {
        mConfirmButton.setVisibility(View.VISIBLE);
        mCancelButton.setVisibility(View.VISIBLE);
    }

    public String getTitleText() {
        return mTitleText;
    }

    public UpdateAPPAlertDialog setTitleText(String text) {
        mTitleText = text;
        if (mTitleTextView != null && mTitleText != null) {
            mTitleTextView.setText(mTitleText);
        }
        return this;
    }

    public String getContentText() {
        return mTitleText;
    }

    public UpdateAPPAlertDialog setContentText(String text) {
        mContentText = text;
        if (mContentTextView != null && mContentText != null) {
            mContentTextView.setText(mContentText);
        }
        return this;
    }


    public boolean isShowCancelButton() {
        return mShowCancel;
    }

    public UpdateAPPAlertDialog showCancelButton(boolean isShow) {
        mShowCancel = isShow;
        if (mCancelButton != null) {
            mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public String getCancelText() {
        return mCancelText;
    }

    public UpdateAPPAlertDialog setCancelText(String text) {
        mCancelText = text;
        if (mCancelButton != null && mCancelText != null) {
            showCancelButton(true);
            mCancelButton.setText(mCancelText);
        }
        return this;
    }

    public String getConfirmText() {
        return mConfirmText;
    }

    public UpdateAPPAlertDialog setConfirmText(String text) {
        mConfirmText = text;
        if (mConfirmButton != null && mConfirmText != null) {
            mConfirmButton.setText(mConfirmText);
        }
        return this;
    }

    public UpdateAPPAlertDialog setCancelClickListener(OnSweetClickListener listener) {
        mCancelClickListener = listener;
        return this;
    }

    public UpdateAPPAlertDialog setCancelDownloadListener(OnSweetClickListener listener) {
        mCancelDownloadListener = listener;
        return this;
    }

    public UpdateAPPAlertDialog setConfirmClickListener(OnSweetClickListener listener) {
        mConfirmClickListener = listener;
        return this;
    }

    protected void onStart() {
        mDialogView.startAnimation(mModalInAnim);
    }

    /**
     * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
     */
    @Override
    public void cancel() {
        dismissWithAnimation(true);
    }

    /**
     * The real Dialog.dismiss() will be invoked async-ly after the animation finishes.
     */
    public void dismissWithAnimation() {
        dismissWithAnimation(false);
    }

    private void dismissWithAnimation(boolean fromCancel) {
        mCloseFromCancel = fromCancel;
        mDialogView.startAnimation(mModalOutAnim);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel_button) {
            if (mCancelClickListener != null) {
                mCancelClickListener.onClick(UpdateAPPAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        } else if (v.getId() == R.id.confirm_button) {
            if (mConfirmClickListener != null) {
                mConfirmClickListener.onClick(UpdateAPPAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        } else if (v.getId() == R.id.fl_close) {
            if (mCancelClickListener != null) {
                mCancelClickListener.onClick(UpdateAPPAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        } else if (v.getId() == R.id.tv_cancelDownload) {
            // 取消下载
            if (mCancelDownloadListener != null) {
                mCancelDownloadListener.onClick(UpdateAPPAlertDialog.this);
            } else {
                dismiss();
            }
        }
    }

    // 注册广播接受者
    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("Status");
            int progress = intent.getIntExtra("Progress", 0);
            // start -- downloading -- finish -- error
            if (status.equals("start")) {
                if (mContentProgressLayout.getVisibility() != View.VISIBLE) {
                    mContentProgressLayout.setVisibility(View.VISIBLE);
                    mContentMsgLayout.setVisibility(View.GONE);
                }
                tvDownloadProgress.setText("正在为您更新 0%");
                pbDownload.setProgress(0);
            } else if (status.equals("downloading")) {
                if (mContentProgressLayout.getVisibility() != View.VISIBLE) {
                    mContentProgressLayout.setVisibility(View.VISIBLE);
                    mContentMsgLayout.setVisibility(View.GONE);
                }
                tvDownloadProgress.setText("正在为您更新 " + progress + "%");
                pbDownload.setProgress(progress);
            } else if (status.equals("finish")) {
                UpdateAPPAlertDialog.this.dismiss();
            } else if (status.equals("error")) {
                UpdateAPPAlertDialog.this.dismiss();
            } else {

            }
        }
    }
}