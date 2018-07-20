package com.bj.eduteacher.community.main.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import com.bj.eduteacher.R;
import com.bj.eduteacher.community.main.model.ArticleInfo;
import com.bj.eduteacher.community.utils.Base64Util;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.List;
import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;


/**
 * Created by Administrator on 2018/4/13 0013.
 */

public class ArticleInfoAdapter extends BaseQuickAdapter<ArticleInfo.DataBean, BaseViewHolder> {
    public ArticleInfoAdapter(@Nullable List<ArticleInfo.DataBean> data) {
        super(data);
        setMultiTypeDelegate(new MultiTypeDelegate<ArticleInfo.DataBean>() {
            @Override
            protected int getItemType(ArticleInfo.DataBean entity) {
                //根据你的实体类来判断布局类型
                return entity.type;
            }
        });

        getMultiTypeDelegate()
                .registerItemType(ArticleInfo.DataBean.FIRST_TYPE, R.layout.recycler_item_find_articleinfo1)
                .registerItemType(ArticleInfo.DataBean.SECOND_TYPE, R.layout.recycler_item_find_articleinfo2)
                .registerItemType(ArticleInfo.DataBean.THREE_TYPE, R.layout.recycler_item_find_articleinfo3);
    }

    @Override
    protected void convert(BaseViewHolder helper, ArticleInfo.DataBean item) {

        AutoUtils.auto(helper.itemView);

        helper.setText(R.id.tv_authorName,item.getAuthor());
        helper.setText(R.id.tv_createTime,item.getCreatetime());
        if(item.getTitle().equals("")){
            helper.getView(R.id.tv_articleTitle).setVisibility(View.GONE);
        }else {
            helper.getView(R.id.tv_articleTitle).setVisibility(View.VISIBLE);
            if(Base64Util.checkBase64(item.getTitle())){
                helper.setText(R.id.tv_articleTitle,Base64Util.decode(item.getTitle()));
            }else {
                helper.setText(R.id.tv_articleTitle,item.getTitle());
            }
        }

        SimpleDraweeView sv =helper.getView(R.id.iv_authorPhoto);
//        sv.setImageURI(BASE_RESOURCE_URL+item.getAuthorimg());
        sv.setImageURI(item.getAuthorimg_url());
        //add
        if(item.getOne_content()!=null){

            if(StringUtils.isEmpty(item.getOne_content().get(0).getContent())){
                helper.getView(R.id.tv_articleContent).setVisibility(View.GONE);
            }else {
                helper.getView(R.id.tv_articleContent).setVisibility(View.VISIBLE);
            }

            if(Base64Util.checkBase64(item.getOne_content().get(0).getContent())){
                helper.setText(R.id.tv_articleContent, Base64Util.decode(item.getOne_content().get(0).getContent()));
            }else {
                helper.setText(R.id.tv_articleContent, item.getOne_content().get(0).getContent());
            }
//            helper.setText(R.id.tv_articleContent, Base64Util.decode(item.getOne_content().get(0).getContent()));

        }else {
            helper.getView(R.id.tv_articleContent).setVisibility(View.GONE);
        }
//        helper.setText(R.id.tv_articleContent,Base64Util.decode(item.getOne_content().get(0).getContent()));
        helper.addOnClickListener(R.id.layout_like);
        helper.addOnClickListener(R.id.layout_comment);
        helper.addOnClickListener(R.id.layout_money);
        helper.addOnClickListener(R.id.layout_share);

        helper.setText(R.id.tv_like,item.getDianzan());
        helper.setText(R.id.tv_comment,item.getComment_num());
        if(item.getNews_dianzanstatus().equals("0")){
            Glide.with(mContext).load(R.mipmap.ic_like1).into((ImageView)helper.getView(R.id.iv_like));
        }
        if(item.getNews_dianzanstatus().equals("1")){
            Glide.with(mContext).load(R.mipmap.ic_like2).into((ImageView)helper.getView(R.id.iv_like));
        }

        switch (helper.getItemViewType()) {
            case ArticleInfo.DataBean.FIRST_TYPE:
                //AutoUtils.auto(helper.itemView);

                break;
            case ArticleInfo.DataBean.SECOND_TYPE:
                //AutoUtils.auto(helper.itemView);
                SimpleDraweeView v = helper.getView(R.id.iv_picture);
                v.setImageURI(BASE_RESOURCE_URL+item.getThree_content().get(0).getImg());
                Glide.with(mContext).load(BASE_RESOURCE_URL+item.getThree_content().get(0).getImg()).crossFade().centerCrop().into((SimpleDraweeView) helper.getView(R.id.iv_picture));
                break;
            case ArticleInfo.DataBean.THREE_TYPE:
               // AutoUtils.auto(helper.itemView);
                SimpleDraweeView v1 = helper.getView(R.id.iv1);
                SimpleDraweeView v2 = helper.getView(R.id.iv2);
                SimpleDraweeView v3 = helper.getView(R.id.iv3);
//                v1.setImageURI(BASE_RESOURCE_URL+item.getThree_content().get(0).getImg());
//                v2.setImageURI(BASE_RESOURCE_URL+item.getThree_content().get(1).getImg());
//                v3.setImageURI(BASE_RESOURCE_URL+item.getThree_content().get(2).getImg());


                Glide.with(mContext).load(BASE_RESOURCE_URL+item.getThree_content().get(0).getImg()).crossFade().into((SimpleDraweeView) helper.getView(R.id.iv1));
                Glide.with(mContext).load(BASE_RESOURCE_URL+item.getThree_content().get(1).getImg()).crossFade().into((SimpleDraweeView) helper.getView(R.id.iv2));
                Glide.with(mContext).load(BASE_RESOURCE_URL+item.getThree_content().get(2).getImg()).crossFade().into((SimpleDraweeView) helper.getView(R.id.iv3));


                break;
        }
    }

}
