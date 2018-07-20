package com.bj.eduteacher.school.detail.fragment.jianjie;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bj.eduteacher.BaseFragment;
import com.bj.eduteacher.R;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.school.list.model.School;
import com.bj.eduteacher.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class IntroduceFragment extends BaseFragment {
    @BindView(R.id.tv_jianjie)
    TextView tvJianjie;
    @BindView(R.id.tv_shizi)
    TextView tvShizi;
    @BindView(R.id.tv_linian)
    TextView tvLinian;
    Unbinder unbinder;
    @BindView(R.id.layout_shizi)
    LinearLayout layoutShizi;
    @BindView(R.id.layout_linian)
    LinearLayout layoutLinian;

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_school_jianjie, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        School.DataBean school = (School.DataBean) getActivity().getIntent().getSerializableExtra("school");
        tvJianjie.setText(school.getJianjie());
        tvShizi.setText(school.getShizi());
        tvLinian.setText(school.getLinian());

        if (StringUtils.isEmpty(school.getShizi())) {
            tvShizi.setVisibility(View.GONE);
            layoutShizi.setVisibility(View.GONE);
        }
        if (StringUtils.isEmpty(school.getLinian())) {
            tvLinian.setVisibility(View.GONE);
            layoutLinian.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    protected void bindViews(View view) {

    }

    @Override
    protected void processLogic() {

    }

    @Override
    protected void setListener() {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void udateUI(MsgEvent event) {
        if (event.getAction().equals("refreshschool")) {


        }
    }

}
