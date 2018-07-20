package com.bj.eduteacher.adapter;

import android.support.annotation.Nullable;
import android.util.Base64;

import com.bj.eduteacher.R;
import com.bj.eduteacher.entity.SubTopicCommentInfo;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Administrator on 2018/5/24 0024.
 */

public class SubTopicCommentAdapter extends BaseQuickAdapter<SubTopicCommentInfo.DataBean.ReplaydataBean,BaseViewHolder>{

    public SubTopicCommentAdapter(int layoutResId, @Nullable List<SubTopicCommentInfo.DataBean.ReplaydataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SubTopicCommentInfo.DataBean.ReplaydataBean item) {

        byte b[] = Base64.decode(item.getContent(), Base64.DEFAULT);
        try {
            String userReply = new String(b, "utf-8");
            helper.setText(R.id.tv_userReplyContent1,userReply);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        helper.setText(R.id.tv_userReplyName1,item.getNicheng());
        helper.setText(R.id.tv_userReplyTime1,item.getUpdatetime());
        SimpleDraweeView sv =helper.getView(R.id.iv_userReplyPhoto1);
        sv.setImageURI(item.getImg_url());
    }
}
