package com.bj.eduteacher.school.detail.fragment.youke;

import android.support.annotation.Nullable;

import com.bj.eduteacher.R;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;

/**
 * Created by Administrator on 2018/6/19 0019.
 */

public class YoukeAdapter extends BaseQuickAdapter<YoukeInfo.DataBean, BaseViewHolder> {

    public YoukeAdapter(int layoutResId, @Nullable List<YoukeInfo.DataBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, YoukeInfo.DataBean item) {
        AutoUtils.auto(helper.itemView);
        SimpleDraweeView sv =helper.getView(R.id.iv_authorPhoto);
        sv.setImageURI(BASE_RESOURCE_URL+item.getImg());

        helper.setText(R.id.tv_authorName,item.getName());
        helper.setText(R.id.tv_resNum,"资源"+item.getKrnum());
        helper.setText(R.id.tv_courseNum,"课时"+item.getKeshi());
    }
}
