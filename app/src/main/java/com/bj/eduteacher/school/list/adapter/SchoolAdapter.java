package com.bj.eduteacher.school.list.adapter;

import android.support.annotation.Nullable;

import com.bj.eduteacher.R;
import com.bj.eduteacher.school.list.model.School;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class SchoolAdapter extends BaseQuickAdapter<School.DataBean, BaseViewHolder> {

    public SchoolAdapter(int layoutResId, @Nullable List<School.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, School.DataBean item) {
        AutoUtils.auto(helper.itemView);
        SimpleDraweeView sv =helper.getView(R.id.iv_picture);
        sv.setImageURI(BASE_RESOURCE_URL+item.getSchoolimg());

        helper.setText(R.id.tv_name,item.getName());
        helper.setText(R.id.tv_quyu,item.getShengfen());
    }
}
