package com.bj.eduteacher.master.adapter;

import android.support.annotation.Nullable;

import com.bj.eduteacher.R;
import com.bj.eduteacher.master.model.KyMasterInfo;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;

/**
 * Created by Administrator on 2018/6/22 0022.
 */

public class KyMasterAdapter extends BaseQuickAdapter<KyMasterInfo.DataBean.MasterDataBean,BaseViewHolder> {

    public KyMasterAdapter(int layoutResId, @Nullable List<KyMasterInfo.DataBean.MasterDataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, KyMasterInfo.DataBean.MasterDataBean item) {
        SimpleDraweeView sv = helper.getView(R.id.iv_authorPhoto);
        sv.setImageURI(BASE_RESOURCE_URL+item.getImg());
        helper.setText(R.id.tv_authorName,item.getName());
        helper.setText(R.id.tv_title,item.getTitle());
        helper.setText(R.id.tv_authorDesc,item.getSjianjie());
        helper.setText(R.id.tv_news_title,item.getNews());
    }
}
