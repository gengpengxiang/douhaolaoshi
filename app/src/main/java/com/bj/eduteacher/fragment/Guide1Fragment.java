package com.bj.eduteacher.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bj.eduteacher.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by zz379 on 2017/8/11.
 */

public class Guide1Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guide_1, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("guide1");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("guide1");
    }
}
