package com.bj.eduteacher.course.fragment.discuss.comment;

import android.support.annotation.Nullable;

import com.bj.eduteacher.R;
import com.bj.eduteacher.community.utils.Base64Util;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/5/24 0024.
 */

public class CommentAdapter extends BaseQuickAdapter<NewCommentInfo.DataBean,BaseViewHolder>{

    public CommentAdapter(int layoutResId, @Nullable List<NewCommentInfo.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewCommentInfo.DataBean item) {

//        byte b[] = Base64.decode(item.getContent(), Base64.DEFAULT);
//        try {
//            String userReply = new String(b, "utf-8");
//            helper.setText(R.id.tv_commentContent,userReply);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        AutoUtils.auto(helper.itemView);

        if(Base64Util.checkBase64(item.getContent().trim())){
            helper.setText(R.id.tv_commentContent,Base64Util.decode(item.getContent()));
        }else {
            helper.setText(R.id.tv_commentContent,item.getContent());
        }

        String time = item.getCreatetime();
        if(isContainChinese(time)){
            helper.setText(R.id.tv_commentTime,item.getCreatetime());
        }else {
            helper.setText(R.id.tv_commentTime,item.getCreatetime().substring(0,10));
        }

        helper.setText(R.id.tv_commentName,item.getNicheng());
//        helper.setText(R.id.tv_commentTime,item.getCreatetime());
       // helper.setText(R.id.tv_commentContent,Base64Util.decode(item.getContent()));
        SimpleDraweeView sv =helper.getView(R.id.iv_userPhoto);
        sv.setImageURI(item.getUser_img_url());
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
}
