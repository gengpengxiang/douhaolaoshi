package com.bj.eduteacher.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by zz379 on 2017/1/5.
 */

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //
        initToolbar();
        initView();
    }

    private void initToolbar() {
        LinearLayout llLeft = (LinearLayout) this.findViewById(R.id.header_ll_left);
        llLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });
        ImageView imgBack = (ImageView) this.findViewById(R.id.header_img_back);
        imgBack.setVisibility(View.VISIBLE);
        TextView tvTitle = (TextView) this.findViewById(R.id.header_tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("关于逗号老师");
    }

    private void initView() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("aboutCompany");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("aboutCompany");
        MobclickAgent.onPause(this);
    }
}
