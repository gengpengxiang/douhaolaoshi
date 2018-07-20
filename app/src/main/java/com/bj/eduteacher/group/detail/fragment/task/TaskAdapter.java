package com.bj.eduteacher.group.detail.fragment.task;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bj.eduteacher.R;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2018/5/23 0023.
 */

public class TaskAdapter extends BaseQuickAdapter<TaskInfo.DataBean.RenwuBean, BaseViewHolder> {


    public TaskAdapter(int layoutResId, @Nullable List<TaskInfo.DataBean.RenwuBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TaskInfo.DataBean.RenwuBean item) {

        AutoUtils.auto(helper.itemView);

        helper.setText(R.id.tv_title,item.getTitle());
        helper.setText(R.id.tv_time,"截止日期: "+item.getJiezhitime());

        if(item.getType().equals("1")){//研讨
            if(item.getStatus()!=null&&item.getStatus().equals("1")){
                Glide.with(mContext).load(R.mipmap.ic_yantao_gray).crossFade().into((ImageView) helper.getView(R.id.iv_icon));
            }else {
                Glide.with(mContext).load(R.mipmap.ic_yantao_red).crossFade().into((ImageView) helper.getView(R.id.iv_icon));
            }
//            Glide.with(mContext).load(R.mipmap.ic_yantao_red).crossFade().into((ImageView) helper.getView(R.id.iv_icon));
        }
        if(item.getType().equals("2")){//资料
            if(item.getRestype().equals("1")){//PPT
                if(item.getStatus()!=null&&item.getStatus().equals("1")){
                    Glide.with(mContext).load(R.mipmap.ic_wendang_gray).crossFade().into((ImageView) helper.getView(R.id.iv_icon));
                }else {
                    Glide.with(mContext).load(R.mipmap.ic_wendang_red).crossFade().into((ImageView) helper.getView(R.id.iv_icon));
                }
//                Glide.with(mContext).load(R.mipmap.ic_wendang_red).crossFade().into((ImageView) helper.getView(R.id.iv_icon));
            }
            if(item.getRestype().equals("2")){//Video
                if(item.getStatus()!=null&&item.getStatus().equals("1")){
                    Glide.with(mContext).load(R.mipmap.ic_play_gray).crossFade().into((ImageView) helper.getView(R.id.iv_icon));
                }else {
                    Glide.with(mContext).load(R.mipmap.ic_play_red).crossFade().into((ImageView) helper.getView(R.id.iv_icon));
                }
//                Glide.with(mContext).load(R.mipmap.ic_play_red).crossFade().into((ImageView) helper.getView(R.id.iv_icon));
            }
        }
        if(item.getType().equals("3")){//课程
            if(item.getStatus()!=null&&item.getStatus().equals("1")){
                Glide.with(mContext).load(R.mipmap.ic_course_gray).crossFade().into((ImageView) helper.getView(R.id.iv_icon));
            }else {
                Glide.with(mContext).load(R.mipmap.ic_course_red).crossFade().into((ImageView) helper.getView(R.id.iv_icon));
            }
//            Glide.with(mContext).load(R.mipmap.ic_course_red).crossFade().into((ImageView) helper.getView(R.id.iv_icon));
        }

        if(item.getType().equals("4")){//测试
            if(item.getStatus()!=null&&item.getStatus().equals("1")){
                Glide.with(mContext).load(R.mipmap.ic_exam_gray).crossFade().into((ImageView) helper.getView(R.id.iv_icon));
            }else {
                Glide.with(mContext).load(R.mipmap.ic_exam_red).crossFade().into((ImageView) helper.getView(R.id.iv_icon));
            }
//            Glide.with(mContext).load(R.mipmap.ic_course_red).crossFade().into((ImageView) helper.getView(R.id.iv_icon));
        }

        if(item.getStatus()!=null&&item.getStatus().equals("1")){
            helper.setText(R.id.tv_status,"已完成");
        }else {
            helper.setText(R.id.tv_status,"");
        }
    }
}
