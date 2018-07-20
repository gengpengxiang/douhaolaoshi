package com.bj.eduteacher.adapter;

import android.support.annotation.Nullable;

import com.bj.eduteacher.R;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.utils.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by Administrator on 2018/6/10 0010.
 */

public class CourseAdapter extends BaseQuickAdapter<ArticleInfo,BaseViewHolder>{

    public CourseAdapter(int layoutResId, @Nullable List<ArticleInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ArticleInfo itemInfo) {

        SimpleDraweeView sv = helper.getView(R.id.iv_picture);
        sv.setImageURI(itemInfo.getArticlePicture());
        //sv.setAspectRatio(1.536f);

        helper.setText(R.id.tv_courseName,itemInfo.getTitle());
        helper.setText(R.id.tv_teachers,"主讲人：" + itemInfo.getAuthor());
        helper.setText(R.id.tv_resNum,"共" + itemInfo.getReadNumber() + "课时");
        helper.setText(R.id.tv_learnNum,itemInfo.getReplyCount() + "人已学习");
        String price = itemInfo.getAgreeNumber();
        String buyType = itemInfo.getCommentNumber();
        if (StringUtils.isEmpty(price) || "0".equals(price)) {
            //holder.tvPrice.setText("免费");
            helper.setText(R.id.tv_price,"免费");
        } else {
            if ("0".equals(buyType)) {
                //holder.tvPrice.setText("¥ " + (Double.parseDouble(price)) / 100);
                helper.setText(R.id.tv_price,"¥ " + (Double.parseDouble(price)) / 100);
            } else {
                //holder.tvPrice.setText("已购");
                helper.setText(R.id.tv_price,"已购");
            }
        }

//        holder.ivPicture.setImageURI(itemInfo.getArticlePicture());
//        holder.ivPicture.setAspectRatio(1.536f);
//
//        // 标题、老师、资源数量、学习人数
//        holder.tvCourseName.setText(itemInfo.getTitle());
//        //holder.tvCourseName.setText("我是666");
//        holder.tvTeachers.setText("主讲人：" + itemInfo.getAuthor());
//        holder.tvResNumber.setText("共" + itemInfo.getReadNumber() + "课时");
//
//        holder.tvLearnNumber.setText(itemInfo.getReplyCount() + "人已学习");
//        // 价格
//        String price = itemInfo.getAgreeNumber();
//        String buyType = itemInfo.getCommentNumber();
//        if (StringUtils.isEmpty(price) || "0".equals(price)) {
//            holder.tvPrice.setText("免费");
//        } else {
//            if ("0".equals(buyType)) {
//                holder.tvPrice.setText("¥ " + (Double.parseDouble(price)) / 100);
//            } else {
//                holder.tvPrice.setText("已购");
//            }
//        }

    }
}
