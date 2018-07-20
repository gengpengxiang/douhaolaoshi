package com.bj.eduteacher.adapter;

import android.support.annotation.Nullable;

import com.bj.eduteacher.R;
import com.bj.eduteacher.entity.DoukeInfo;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;

/**
 * Created by Administrator on 2018/5/31 0031.
 */

public class DoukeAdapter extends BaseQuickAdapter<DoukeInfo.DataBean,BaseViewHolder> {

    public DoukeAdapter(int layoutResId, @Nullable List<DoukeInfo.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DoukeInfo.DataBean item) {

        helper.setText(R.id.tv_title,item.getTitle());
        SimpleDraweeView sv = helper.getView(R.id.iv_authorPhoto);
        sv.setImageURI(BASE_RESOURCE_URL+item.getAuthorimg());

        helper.setText(R.id.tv_authorName,item.getAuthor());

        helper.setText(R.id.tv_authorDesc,item.getAuthorjianjie());

        SimpleDraweeView sv2 = helper.getView(R.id.iv_picture);
        sv2.setImageURI(BASE_RESOURCE_URL+item.getImg());
    }
}
