package com.bj.eduteacher.community.details.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bj.eduteacher.R;
import com.bj.eduteacher.community.details.model.ArticleDetail;
import com.bj.eduteacher.community.utils.Base64Util;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;
import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;

/**
 * Created by Administrator on 2018/4/18 0018.
 */

public class ArticleDetailAdapter extends BaseQuickAdapter<ArticleDetail.DataBean.NewsContentBean, BaseViewHolder> {

    public ArticleDetailAdapter(int layoutResId, @Nullable List<ArticleDetail.DataBean.NewsContentBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ArticleDetail.DataBean.NewsContentBean item) {

        AutoUtils.auto(helper.itemView);

        //SimpleDraweeView simpleDraweeView = helper.getView(R.id.iv_pic);
        if(item.getContent().equals("")){
            helper.getView(R.id.tv_content).setVisibility(View.GONE);
        }else {
            helper.getView(R.id.tv_content).setVisibility(View.VISIBLE);
        }
        if(item.getImg().equals("")){
            //helper.getView(R.id.iv_pic).setVisibility(View.GONE);
        }else {
            //helper.getView(R.id.iv_pic).setVisibility(View.VISIBLE);
        }

        if(Base64Util.checkBase64(item.getContent())){
            helper.setText(R.id.tv_content, Base64Util.decode(item.getContent()));
        }else {
            helper.setText(R.id.tv_content, item.getContent());
        }

        //simpleDraweeView.setImageURI(BASE_RESOURCE_URL+item.getImg());
        //Glide.with(mContext).load(BASE_RESOURCE_URL+item.getImg()).centerCrop().crossFade().into((SimpleDraweeView) helper.getView(R.id.iv_pic));
        //simpleDraweeView.setAspectRatio(1.8f);
        final ImageView iv = (ImageView) helper.getView(R.id.iv_pic2);
        Glide.with(mContext).load(BASE_RESOURCE_URL+item.getImg()).crossFade().into((ImageView) helper.getView(R.id.iv_pic2));


    }
}
