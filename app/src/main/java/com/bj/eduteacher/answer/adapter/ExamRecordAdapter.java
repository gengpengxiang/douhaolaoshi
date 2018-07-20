package com.bj.eduteacher.answer.adapter;

import android.support.annotation.Nullable;

import com.bj.eduteacher.R;
import com.bj.eduteacher.answer.model.ExamRecord;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2018/7/9 0009.
 */

public class ExamRecordAdapter extends BaseQuickAdapter<ExamRecord.DataBean,BaseViewHolder> {


    public ExamRecordAdapter(int layoutResId, @Nullable List<ExamRecord.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ExamRecord.DataBean item) {

        helper.setText(R.id.tv_time,item.getCreatetime().substring(0,16));
        helper.setText(R.id.tv_accuracy,item.getShiti_right()+"/"+item.getShiti_num());

    }


}
