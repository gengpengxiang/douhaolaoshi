package com.bj.eduteacher.group.list.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bj.eduteacher.R;
import com.bj.eduteacher.group.list.model.GroupInfo;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;

/**
 * Created by Administrator on 2018/5/4 0004.
 */

public class GroupAdapter extends BaseQuickAdapter<GroupInfo.DataBean,BaseViewHolder> {


    public GroupAdapter(int layoutResId, @Nullable List<GroupInfo.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GroupInfo.DataBean item) {

        AutoUtils.auto(helper.itemView);

        SimpleDraweeView sv =helper.getView(R.id.iv_groupPhoto);
        sv.setImageURI(BASE_RESOURCE_URL+item.getLogo());
        helper.setText(R.id.tv_group_name,item.getName());
        helper.setText(R.id.tv_group_content,item.getJianjie());

        if(item.getUpdatestatus()!=null) {
            if (item.getUpdatestatus().equals("0")) {
                helper.getView(R.id.iv_remind).setVisibility(View.INVISIBLE);
            }
            if (item.getUpdatestatus().equals("1")) {
                helper.getView(R.id.iv_remind).setVisibility(View.VISIBLE);
            }
        }else {
            helper.getView(R.id.iv_remind).setVisibility(View.INVISIBLE);
        }
    }
}
