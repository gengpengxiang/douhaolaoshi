package com.bj.eduteacher.master.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.bj.eduteacher.R;
import com.bj.eduteacher.master.model.JyMasterInfo;
import com.bj.eduteacher.master.model.KyMasterInfo;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;

/**
 * Created by Administrator on 2018/6/22 0022.
 */

public class JyMasterAdapter extends BaseQuickAdapter<JyMasterInfo.DataBean.MasterDataBean,BaseViewHolder> {

    public JyMasterAdapter(int layoutResId, @Nullable List<JyMasterInfo.DataBean.MasterDataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, JyMasterInfo.DataBean.MasterDataBean item) {
        SimpleDraweeView sv = helper.getView(R.id.iv_authorPhoto);
        sv.setImageURI(BASE_RESOURCE_URL+item.getImg());
        helper.setText(R.id.tv_authorName,item.getName());
        helper.setText(R.id.tv_authorDesc,item.getXuekeshengfen());

        if(item.getNewupdate().equals("1")){
            helper.getView(R.id.tv_newTeacher).setVisibility(View.VISIBLE);
        }else {
            helper.getView(R.id.tv_newTeacher).setVisibility(View.INVISIBLE);
        }
    }
}
