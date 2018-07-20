package com.bj.eduteacher.school.list.view;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.school.list.model.Province;
import com.bj.eduteacher.school.list.model.School;
import com.bj.eduteacher.school.list.presenter.SchoolPresenter;
import com.bj.eduteacher.utils.DensityUtils;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SchoolAllActivity extends BaseActivity implements IViewSchool {

    @BindView(R.id.header_tv_title)
    TextView headerTvTitle;
    @BindView(R.id.header_img_back)
    ImageView headerImgBack;
    @BindView(R.id.scrollIndicator)
    ScrollIndicatorView indicator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.lineView)
    View lineView;
    private Unbinder unbinder;
    private SchoolPresenter presenter;
    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;
    private MyAdapter mAdapter;
    private List<Province.DataBean> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_school_all);
        //改变底部导航栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        }


        unbinder = ButterKnife.bind(this);
        initViews();
        presenter = new SchoolPresenter(this, this);
        presenter.getProvinces();
    }

    private void initViews() {
        headerImgBack.setVisibility(View.VISIBLE);
        headerTvTitle.setVisibility(View.VISIBLE);
        headerTvTitle.setText("全部学校");

        indicator.setOnTransitionListener(new OnTransitionTextListener()
                .setColor(Color.parseColor("#FE5433"), Color.parseColor("#707070")));
        indicator.setScrollBar(new ColorBar(this, 0xFFFE5433, 4));

        viewPager.setOffscreenPageLimit(4);
        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        inflate = LayoutInflater.from(this);
        mAdapter = new MyAdapter(getSupportFragmentManager(), dataList);
        indicatorViewPager.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        presenter.onDestory();
    }

    @OnClick(R.id.header_img_back)
    public void onClick() {
        finish();
    }

    @Override
    public void getProvincesSuccess(List<Province.DataBean> provinceList) {
        dataList.clear();
        dataList.addAll(provinceList);
        mAdapter.notifyDataSetChanged();

        lineView.setVisibility(View.VISIBLE);
    }

    @Override
    public void getSchoolsSuccess(List<School.DataBean> schoolList) {

    }

    private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        private List<Province.DataBean> dataList;

        public MyAdapter(FragmentManager fragmentManager, List<Province.DataBean> dataList) {
            super(fragmentManager);
            this.dataList = dataList;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.tab_douhao_course, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(dataList.get(position).getName());

            int width = getTextWidth(textView);
            int padding = 0;
            if (this != null) {
                padding = DensityUtils.dp2px(SchoolAllActivity.this, 16);
            }
            //因为wrap的布局 字体大小变化会导致textView大小变化产生抖动，这里通过设置textView宽度就避免抖动现象
            //1.3f是根据上面字体大小变化的倍数1.3f设置
            textView.setWidth(width + padding * 2);

            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {

            SchoolItemFragment schoolItemFragment = new SchoolItemFragment();
            Bundle bundle = new Bundle();
            bundle.putString("Province", dataList.get(position).getName());
            schoolItemFragment.setArguments(bundle);

            return schoolItemFragment;
        }

        private int getTextWidth(TextView textView) {
            if (textView == null) {
                return 0;
            }
            Rect bounds = new Rect();
            String text = textView.getText().toString();
            Paint paint = textView.getPaint();
            paint.getTextBounds(text, 0, text.length(), bounds);
            int width = bounds.left + bounds.width();
            return width;
        }
    }
}
