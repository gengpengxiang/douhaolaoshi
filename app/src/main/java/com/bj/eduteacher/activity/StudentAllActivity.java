package com.bj.eduteacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.fragment.StudentAllFragment;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

/**
 * Created by zz379 on 2017/2/3.
 */

public class StudentAllActivity extends BaseActivity {

    private static final String ORDER_STUDENT_BY_NAME = "xm";
    private static final String ORDER_STUDENT_BY_SCORE = "qt";

    private String classId;
    private int currIndex;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private Fragment[] mTabs = new Fragment[2];
    private String[] mTabTitles = new String[]{"姓名顺序", "排名顺序"};

    private FragmentManager fm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_all);
        ButterKnife.bind(this);
        // 初始化页面
        initToolBar();
        initView();
        initData();
    }

    private void initToolBar() {
        TextView tvTitle = (TextView) this.findViewById(R.id.header_tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("全班学生");

        LinearLayout llHeaderLeft = (LinearLayout) this.findViewById(R.id.header_ll_left);
        llHeaderLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentAllActivity.this.finish();
            }
        });
        LinearLayout llHeaderRight = (LinearLayout) this.findViewById(R.id.header_ll_right);
        llHeaderRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentAllActivity.this, LevelDetailActivity.class);
                startActivity(intent);
            }
        });

        ImageView imgBack = (ImageView) this.findViewById(R.id.header_img_back);
        imgBack.setVisibility(View.VISIBLE);
        ImageView ivQuestion = (ImageView) this.findViewById(R.id.header_img_question);
        ivQuestion.setVisibility(View.VISIBLE);
    }

    private void initView() {
        fm = getSupportFragmentManager();

        mTabLayout = (TabLayout) this.findViewById(R.id.mTabLayout);
        mViewPager = (ViewPager) this.findViewById(R.id.mViewPager);
    }

    private void initData() {
        classId = getIntent().getExtras().getString(MLConfig.KEY_CLASS_ID, "");
        currIndex = getIntent().getExtras().getInt(MLProperties.BUNDLE_KEY_VIEWPAGER_INDEX, 0);

        StudentAllFragment sortNameFragment = new StudentAllFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MLConfig.KEY_CLASS_ID, classId);
        bundle.putString(MLConfig.KEY_CLASS_STUDENTS_ORDERBY, ORDER_STUDENT_BY_NAME);
        sortNameFragment.setArguments(bundle);
        mTabs[0] = sortNameFragment;

        StudentAllFragment sortScoreFragment = new StudentAllFragment();
        Bundle args = new Bundle();
        args.putString(MLConfig.KEY_CLASS_ID, classId);
        args.putString(MLConfig.KEY_CLASS_STUDENTS_ORDERBY, ORDER_STUDENT_BY_SCORE);
        sortScoreFragment.setArguments(args);
        mTabs[1] = sortScoreFragment;

        SimpleAdapter mAdapter = new SimpleAdapter(fm);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.setCurrentItem(currIndex);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class SimpleAdapter extends FragmentPagerAdapter {

        public SimpleAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mTabs[position];
        }

        @Override
        public int getCount() {
            return mTabs.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
