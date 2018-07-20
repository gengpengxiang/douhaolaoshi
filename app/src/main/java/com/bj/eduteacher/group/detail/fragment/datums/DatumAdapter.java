package com.bj.eduteacher.group.detail.fragment.datums;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.bj.eduteacher.R;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;

/**
 * Created by Administrator on 2018/5/4 0004.
 */

public class DatumAdapter extends BaseQuickAdapter<Datums.DataBean.GroupZiliaolistBean,BaseViewHolder>{
    public DatumAdapter(int layoutResId, @Nullable List<Datums.DataBean.GroupZiliaolistBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Datums.DataBean.GroupZiliaolistBean item) {

        AutoUtils.auto(helper.itemView);

        SimpleDraweeView sv = helper.getView(R.id.iv_authorPhoto);
        sv.setImageURI(BASE_RESOURCE_URL+item.getImg());

        helper.setText(R.id.tv_authorName,item.getTitle());
        helper.setText(R.id.tv_authorDesc,item.getFilename());
        helper.setText(R.id.tv_previewNum,"浏览"+item.getViewnum());
        helper.getView(R.id.tv_price).setVisibility(View.INVISIBLE);
        helper.addOnClickListener(R.id.tv_buyRes);
        TextView tv = helper.getView(R.id.tv_buyRes);
        tv.setText("查看");
    }

    /*public DatumAdapter(int layoutResId, @Nullable List<List<Datums.DataBean>> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, List<Datums.DataBean> item) {

    }*/
}
