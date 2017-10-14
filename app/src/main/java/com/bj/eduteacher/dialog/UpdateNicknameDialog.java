package com.bj.eduteacher.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bj.eduteacher.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateNicknameDialog extends Dialog implements View.OnClickListener {

    private View mDialogView;
    private TextView mTitleTextView;
    private EditText mContentEdt;
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

    private AnimationSet mModalInAnim;
    private AnimationSet mModalOutAnim;
    private boolean mCloseFromCancel;
    public static final int NORMAL_TYPE = 0;

    public static interface OnSweetClickListener {
        public void onClick(UpdateNicknameDialog sweetAlertDialog, EditText mContentEdt);
    }

    public UpdateNicknameDialog(Context context) {
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
                            UpdateNicknameDialog.super.cancel();
                        } else {
                            UpdateNicknameDialog.super.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public UpdateNicknameDialog(Context context, int alertType) {
        super(context, R.style.alert_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_update_nickname_dialog);

        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mTitleTextView = (TextView) findViewById(R.id.title_text);
        mContentEdt = (EditText) findViewById(R.id.edt_reasonName);
        mConfirmButton = (Button) findViewById(R.id.confirm_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mCloseButton = (FrameLayout) findViewById(R.id.fl_close);
        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mCloseButton.setOnClickListener(this);

        mContentEdt.setFilters(new InputFilter[]{emojiFilter, specialCharFilter, new InputFilter.LengthFilter(8)});

        setTitleText(mTitleText);
        setContentText(mContentText);
        setCancelText(mCancelText);
        setConfirmText(mConfirmText);
    }

    InputFilter emojiFilter = new InputFilter() {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                Toast.makeText(getContext(), "不支持输入表情", Toast.LENGTH_SHORT).show();
                return "";
            }
            return null;
        }
    };

    InputFilter specialCharFilter = new InputFilter() {
        String regEx = "[\\s~·`!！～@#￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？?]";
        Pattern specialPattern = Pattern.compile(regEx, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher specialMatcher = specialPattern.matcher(source);
            if (specialMatcher.find()) {
                Toast.makeText(getContext(), "仅支持输入中英字母、数字", Toast.LENGTH_SHORT).show();
                return "";
            }
            return null;
        }
    };

    private void restore() {
        mConfirmButton.setVisibility(View.VISIBLE);
        mCancelButton.setVisibility(View.VISIBLE);
    }

    public String getTitleText() {
        return mTitleText;
    }

    public UpdateNicknameDialog setTitleText(String text) {
        mTitleText = text;
        if (mTitleTextView != null && mTitleText != null) {
            mTitleTextView.setText(mTitleText);
        }
        return this;
    }

    public String getContentText() {
        return mTitleText;
    }

    public UpdateNicknameDialog setContentText(String text) {
        mContentText = text;
        if (mContentEdt != null && mContentText != null) {
            mContentEdt.setText(mContentText);
        }
        return this;
    }


    public boolean isShowCancelButton() {
        return mShowCancel;
    }

    public UpdateNicknameDialog showCancelButton(boolean isShow) {
        mShowCancel = isShow;
        if (mCancelButton != null) {
            mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public String getCancelText() {
        return mCancelText;
    }

    public UpdateNicknameDialog setCancelText(String text) {
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

    public UpdateNicknameDialog setConfirmText(String text) {
        mConfirmText = text;
        if (mConfirmButton != null && mConfirmText != null) {
            mConfirmButton.setText(mConfirmText);
        }
        return this;
    }

    public UpdateNicknameDialog setCancelClickListener(OnSweetClickListener listener) {
        mCancelClickListener = listener;
        return this;
    }

    public UpdateNicknameDialog setConfirmClickListener(OnSweetClickListener listener) {
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
                mCancelClickListener.onClick(UpdateNicknameDialog.this, mContentEdt);
            } else {
                dismissWithAnimation();
            }
        } else if (v.getId() == R.id.confirm_button) {
            if (mConfirmClickListener != null) {
                mConfirmClickListener.onClick(UpdateNicknameDialog.this, mContentEdt);
            } else {
                dismissWithAnimation();
            }
        } else if (v.getId() == R.id.fl_close) {
            if (mCancelClickListener != null) {
                mCancelClickListener.onClick(UpdateNicknameDialog.this, mContentEdt);
            } else {
                dismissWithAnimation();
            }
        }
    }
}