package com.bj.eduteacher.group.detail.fragment.member;

import android.support.annotation.Nullable;

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

public class MemberAdapter extends BaseQuickAdapter<Member.DataBean,BaseViewHolder>{

    public MemberAdapter(int layoutResId, @Nullable List<Member.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Member.DataBean item) {

        AutoUtils.auto(helper.itemView);
        SimpleDraweeView sv = helper.getView(R.id.iv_authorPhoto);
//        sv.setImageURI(BASE_RESOURCE_URL+item.getImg());
        sv.setImageURI(item.getImg_url());

        helper.setText(R.id.tv_name,item.getNicheng());
    }
}
