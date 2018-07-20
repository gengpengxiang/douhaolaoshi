package com.bj.eduteacher.course.fragment.study;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bj.eduteacher.R;
import com.bj.eduteacher.group.detail.fragment.member.Member;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2018/5/23 0023.
 */

public class StudyAdapter extends BaseQuickAdapter<CourseRes.DataBean.KcresxxlistBean,BaseViewHolder> {

    public StudyAdapter(int layoutResId, @Nullable List<CourseRes.DataBean.KcresxxlistBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CourseRes.DataBean.KcresxxlistBean item) {
        helper.setText(R.id.tv_title,item.getTitle());
        helper.setText(R.id.tv_duration,item.getShichang());
        helper.setText(R.id.tv_num,item.getViewnum()+"次学习");
        helper.setText(R.id.tv_progress,item.getRes_jindu());
        //上次播放视频
        if(item.getBefore_res_status()==1){
//            helper.getView(R.id.iv_lock).setVisibility(View.GONE);
            ImageView iv = helper.getView(R.id.iv_video);
            iv.setImageResource(R.mipmap.ic_play_red);
            TextView tvTitle = helper.getView(R.id.tv_title);
            TextView tvDuration = helper.getView(R.id.tv_duration);
            TextView tvNum = helper.getView(R.id.tv_num);
            TextView tvProgress = helper.getView(R.id.tv_progress);
            tvTitle.setTextColor(0xfffe5433);
//            tvDuration.setTextColor(0xfffe5433);
//            tvNum.setTextColor(0xfffe5433);
//            tvProgress.setTextColor(0xfffe5433);
        }else {
            ImageView iv = helper.getView(R.id.iv_video);
            iv.setImageResource(R.mipmap.ic_play_gray);
            TextView tvTitle = helper.getView(R.id.tv_title);
            TextView tvDuration = helper.getView(R.id.tv_duration);
            TextView tvNum = helper.getView(R.id.tv_num);
            TextView tvProgress = helper.getView(R.id.tv_progress);
            tvTitle.setTextColor(0xff515151);
            tvDuration.setTextColor(0xff828282);
            tvNum.setTextColor(0xff828282);
            tvProgress.setTextColor(0xff828282);
        }
        if(item.getMyType()==3){
            helper.getView(R.id.iv_lock).setVisibility(View.GONE);
        }if(item.getMyType()==1){
            helper.getView(R.id.iv_lock).setVisibility(View.GONE);
            helper.setText(R.id.tv_progress,"免费试学");
            TextView tvProgress = helper.getView(R.id.tv_progress);
            tvProgress.setTextColor(0xfffe5433);
        }
//        if(item.getMyType()==1){
//            helper.getView(R.id.iv_lock).setVisibility(View.GONE);
//            helper.setText(R.id.tv_progress,"免费试学");
//        }if(item.getMyType()==3){
//            helper.getView(R.id.iv_lock).setVisibility(View.GONE);
//            ImageView iv = helper.getView(R.id.iv_video);
//            iv.setImageResource(R.mipmap.ic_play_red);
//            TextView tvTitle = helper.getView(R.id.tv_title);
//            tvTitle.setTextColor(0xfffe5433);
//        }


        /*if(item.getMyType()==1){
            Log.e("第一条","mm");
            helper.getView(R.id.iv_lock).setVisibility(View.GONE);
            helper.setText(R.id.tv_progress,"免费试学");

            ImageView iv = helper.getView(R.id.iv_video);
            iv.setImageResource(R.mipmap.ic_play_red);
            TextView tvTitle = helper.getView(R.id.tv_title);
            TextView tvDuration = helper.getView(R.id.tv_duration);
            TextView tvNum = helper.getView(R.id.tv_num);
            TextView tvProgress = helper.getView(R.id.tv_progress);
            tvTitle.setTextColor(0xfffe5433);
            tvDuration.setTextColor(0xfffe5433);
            tvNum.setTextColor(0xfffe5433);
            tvProgress.setTextColor(0xfffe5433);
        }
        if(item.getMyType()==2){//未解锁
            Log.e("第二条数据","mm");
            helper.getView(R.id.iv_lock).setVisibility(View.VISIBLE);
            helper.setText(R.id.tv_progress,item.getRes_jindu());
        }
        if(item.getMyType()==3){//已解锁
            helper.getView(R.id.iv_lock).setVisibility(View.GONE);
            helper.setText(R.id.tv_progress,item.getRes_jindu());
        }
        if(item.getMyType()==4){//选中
            helper.getView(R.id.iv_lock).setVisibility(View.GONE);
            helper.setText(R.id.tv_progress,item.getRes_jindu());
            ImageView iv = helper.getView(R.id.iv_video);
            iv.setImageResource(R.mipmap.ic_play_red);
            TextView tvTitle = helper.getView(R.id.tv_title);
            TextView tvDuration = helper.getView(R.id.tv_duration);
            TextView tvNum = helper.getView(R.id.tv_num);
            TextView tvProgress = helper.getView(R.id.tv_progress);
            tvTitle.setTextColor(0xfffe5433);
            tvDuration.setTextColor(0xfffe5433);
            tvNum.setTextColor(0xfffe5433);
            tvProgress.setTextColor(0xfffe5433);
        }
        else {
            Log.e("例外","mm");
            ImageView iv = helper.getView(R.id.iv_video);
            iv.setImageResource(R.mipmap.ic_play_gray);
            TextView tvTitle = helper.getView(R.id.tv_title);
            TextView tvDuration = helper.getView(R.id.tv_duration);
            TextView tvNum = helper.getView(R.id.tv_num);
            TextView tvProgress = helper.getView(R.id.tv_progress);
            tvTitle.setTextColor(0xff515151);
            tvDuration.setTextColor(0xff828282);
            tvNum.setTextColor(0xff828282);
            tvProgress.setTextColor(0xff828282);
        }*/
    }


}
