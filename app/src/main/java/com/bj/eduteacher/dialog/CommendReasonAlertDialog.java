package com.bj.eduteacher.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.TextView;

import com.bj.eduteacher.R;

import java.util.ArrayList;
import java.util.List;

public class CommendReasonAlertDialog extends Dialog implements View.OnClickListener {

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
    private OnSweetClickListener mCancelClickListener;
    private OnSweetClickListener mConfirmClickListener;
    private RecyclerView mRecyclerView;
    private List<CommendReasonInfo> mDataList = new ArrayList<>();
    private CommendReasonAdapter mAdapter;
    private onSweetContentItemClickListener mItemClickListener;

    private AnimationSet mModalInAnim;
    private AnimationSet mModalOutAnim;
    private boolean mCloseFromCancel;
    public static final int NORMAL_TYPE = 0;

    public static interface OnSweetClickListener {
        public void onClick(CommendReasonAlertDialog sweetAlertDialog);
    }

    public static interface onSweetContentItemClickListener {
        public void onClick(CommendReasonAlertDialog sweetAlertDialog, int position);
    }

    public CommendReasonAlertDialog(Context context) {
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
                            CommendReasonAlertDialog.super.cancel();
                        } else {
                            CommendReasonAlertDialog.super.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public CommendReasonAlertDialog(Context context, int alertType) {
        super(context, R.style.alert_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_commend_reason_dialog);

        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mTitleTextView = (TextView) findViewById(R.id.title_text);
        mContentTextView = (TextView) findViewById(R.id.content_text);
        mConfirmButton = (Button) findViewById(R.id.confirm_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);

        mAdapter = new CommendReasonAdapter(mDataList);
        mAdapter.setOnMyItemClickListener(new CommendReasonAdapter.OnMyItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (mItemClickListener != null) {
                    mItemClickListener.onClick(CommendReasonAlertDialog.this, position);
                } else {
                    CommendReasonAlertDialog.this.dismiss();
                }
            }
        });
        mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 3));
        mRecyclerView.setAdapter(mAdapter);


        setTitleText(mTitleText);
        setContentText(mContentText);
//        setContentRecyclerData(mDataList);
        setCancelText(mCancelText);
        setConfirmText(mConfirmText);
    }

    private void restore() {
        mConfirmButton.setVisibility(View.VISIBLE);
        mCancelButton.setVisibility(View.VISIBLE);
    }

    public String getTitleText() {
        return mTitleText;
    }

    public CommendReasonAlertDialog setTitleText(String text) {
        mTitleText = text;
        if (mTitleTextView != null && mTitleText != null) {
            mTitleTextView.setText(mTitleText);
        }
        return this;
    }

    public String getContentText() {
        return mTitleText;
    }

    public CommendReasonAlertDialog setContentText(String text) {
        mContentText = text;
        if (mContentTextView != null && mContentText != null) {
            mContentTextView.setText(mContentText);
        }
        return this;
    }

    public List<CommendReasonInfo> getContentRecyclerData() {
        return mDataList;
    }

    public CommendReasonAlertDialog setContentRecyclerData(List<CommendReasonInfo> dataList) {
        mDataList.clear();
        mDataList.addAll(dataList);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        return this;
    }


    public boolean isShowCancelButton() {
        return mShowCancel;
    }

    public CommendReasonAlertDialog showCancelButton(boolean isShow) {
        mShowCancel = isShow;
        if (mCancelButton != null) {
            mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public String getCancelText() {
        return mCancelText;
    }

    public CommendReasonAlertDialog setCancelText(String text) {
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

    public CommendReasonAlertDialog setConfirmText(String text) {
        mConfirmText = text;
        if (mConfirmButton != null && mConfirmText != null) {
            mConfirmButton.setText(mConfirmText);
        }
        return this;
    }

    public CommendReasonAlertDialog setCancelClickListener(OnSweetClickListener listener) {
        mCancelClickListener = listener;
        return this;
    }

    public CommendReasonAlertDialog setConfirmClickListener(OnSweetClickListener listener) {
        mConfirmClickListener = listener;
        return this;
    }

    public CommendReasonAlertDialog setmItemClickListener(onSweetContentItemClickListener mItemCliskListener) {
        this.mItemClickListener = mItemCliskListener;
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
        dismiss();
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
                mCancelClickListener.onClick(CommendReasonAlertDialog.this);
            } else {
                dismiss();
            }
        } else if (v.getId() == R.id.confirm_button) {
            if (mConfirmClickListener != null) {
                mConfirmClickListener.onClick(CommendReasonAlertDialog.this);
            } else {
                dismiss();
            }
        }
    }
}