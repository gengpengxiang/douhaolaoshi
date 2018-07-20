package com.bj.eduteacher.answer.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bj.eduteacher.R;
import com.bj.eduteacher.answer.model.ExamResult;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/13 0013.
 */

public class ExamResultAdapter extends BaseQuickAdapter<ExamResult.DataBean.LogShitiBean,BaseViewHolder> {

    public ExamResultAdapter(int layoutResId, @Nullable List<ExamResult.DataBean.LogShitiBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ExamResult.DataBean.LogShitiBean item) {
        helper.setText(R.id.tv_answer, item.getShiti_ordernum()+"."+item.getShiti_title());

        helper.getView(R.id.tv_status).setVisibility(View.GONE);
        ImageView iv = helper.getView(R.id.iv_status);
        iv.setVisibility(View.VISIBLE);

        if(item.getStatus().equals("1")){
            iv.setImageResource(R.mipmap.icon_corrent);
        }else {
            iv.setImageResource(R.mipmap.icon_error);
        }
    }

}
