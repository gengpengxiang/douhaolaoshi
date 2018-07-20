package com.bj.eduteacher.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bj.eduteacher.BaseFragment;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.DoukeNewSearchActivity;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.utils.DensityUtils;
import com.bj.eduteacher.utils.T;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zz379 on 2017/8/16.
 * 新逗课页面
 */
public class DoukeNewFragment extends BaseFragment {

    @BindView(R.id.header_tv_title)
    TextView tvTitle;
    @BindView(R.id.mViewPager)
    ViewPager viewPager;
    @BindView(R.id.douke_course_indicator)
    ScrollIndicatorView indicator;
    @BindView(R.id.header_iv_search)
    ImageView ivSearch;

    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;

    private List<String> mDataList = new ArrayList<>();
    private MyAdapter mAdapter;

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_douhao_course, container, false);
        ButterKnife.bind(this, view);

        initToolbar();
        initView();
        initData();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        return null;
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

    private void initToolbar() {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("优课");
        ivSearch.setVisibility(View.VISIBLE);
    }

    private void initView() {
        indicator.setOnTransitionListener(new OnTransitionTextListener()
                .setColor(Color.parseColor("#FE5433"), Color.parseColor("#707070")));
        indicator.setScrollBar(new ColorBar(getActivity(), 0xFFFE5433, 4));

        viewPager.setOffscreenPageLimit(4);
        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        inflate = LayoutInflater.from(getActivity());
        mAdapter = new MyAdapter(getChildFragmentManager(), mDataList);
        indicatorViewPager.setAdapter(mAdapter);
    }

    private void initData() {
        getXuekeFromAPI();
    }

    private void getXuekeFromAPI() {
        Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<String>> e) throws Exception {
                try {
                    LmsDataService service = new LmsDataService();
                    List<String> dataList = service.getNewDoukeXuekeFromAPI();
                    if (!e.isDisposed()) {
                        e.onNext(dataList);
                        e.onComplete();
                    }
                } catch (InterruptedIOException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                } catch (InterruptedException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull List<String> strings) {
                        mDataList.clear();
                        mDataList.addAll(strings);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (getActivity() != null) {
                            T.showShort(getActivity(), "服务器开小差了，请稍后重试！");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private class MyAdapter extends IndicatorFragmentPagerAdapter {

        private List<String> dataList;

        public MyAdapter(FragmentManager fragmentManager, List<String> dataList) {
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
            textView.setText(dataList.get(position));

            int width = getTextWidth(textView);
            int padding = 0;
            if (getActivity() != null) {
                padding = DensityUtils.dp2px(getActivity(), 16);
            }
            //因为wrap的布局 字体大小变化会导致textView大小变化产生抖动，这里通过设置textView宽度就避免抖动现象
            //1.3f是根据上面字体大小变化的倍数1.3f设置
            textView.setWidth(width + padding * 2);

            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            DoukeNewItemFragment mainFragment = new DoukeNewItemFragment();
            Bundle bundle = new Bundle();
            bundle.putString("XueKe", dataList.get(position));
            mainFragment.setArguments(bundle);
            return mainFragment;
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

    @Override
    public void onPause() {
        super.onPause();
        disposables.clear();
    }

    @OnClick(R.id.header_ll_right)
    void clickSearch() {
        // 跳转到逗课搜索页面
        Intent intent = new Intent(getActivity(), DoukeNewSearchActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.act_alpha_in, R.anim.act_alpha_out);
    }
}
