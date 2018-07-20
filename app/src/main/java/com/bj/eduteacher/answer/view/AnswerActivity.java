package com.bj.eduteacher.answer.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.answer.dbhelper.AnswerDao;
import com.bj.eduteacher.answer.dbhelper.GreenDaoHelper;
import com.bj.eduteacher.answer.model.Answer;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.community.main.view.CustomPopDialog;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.T;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;

public class AnswerActivity extends BaseActivity {

    @BindView(R.id.header_img_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_left)
    TextView tvTitleLeft;
    @BindView(R.id.mViewPager)
    ViewPager mViewPager;
    @BindView(R.id.bt_last)
    TextView btLast;
    @BindView(R.id.bt_next)
    TextView btNext;
    @BindView(R.id.bt_nextfirst)
    TextView btNextfirst;
    @BindView(R.id.header_ll_left)
    LinearLayout headerLlLeft;
    private Unbinder unbinder;

    private int mPosition = 0;
    private final List<Fragment> mFragments = new ArrayList<>();
    private Integer num;
    private Bundle bundle;
    private AnswerDao mAnswerDao;
    private String examid;
    private String phone;
    private String unionid;
    private long submitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        initViews();
        mAnswerDao = GreenDaoHelper.getDaoSession().getAnswerDao();
    }

    private void initViews() {

        String questionNum = getIntent().getStringExtra("questionNum");
        num = Integer.valueOf(questionNum);
        examid = getIntent().getStringExtra("examid");
        tvTitleLeft.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        tvTitleLeft.setText("题目1/" + num);


        if (num == 1) {
            btNextfirst.setText("提交");
        }

        for (int i = 1; i < num + 1; i++) {
            AnswerDetailFragment fragment = new AnswerDetailFragment();
            bundle = new Bundle();
            bundle.putInt("page", i);
            fragment.setArguments(bundle);
            mFragments.add(fragment);

        }

        InfoAdapter adapter = new InfoAdapter(getSupportFragmentManager(), mFragments);

        mViewPager.setAdapter(adapter);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvTitleLeft.setText("题目" + (position + 1) + "/" + num);

                mPosition = position;

                if (position == 0) {
                    btNextfirst.setVisibility(View.VISIBLE);
                    btNext.setVisibility(View.GONE);
                    btLast.setVisibility(View.GONE);
                } else if (position == mFragments.size() - 1) {
                    btNextfirst.setVisibility(View.GONE);
                    btLast.setVisibility(View.VISIBLE);
                    btNext.setVisibility(View.VISIBLE);
                    btNext.setText("提交");
                } else if (0 < position && position < mFragments.size() - 1) {
                    btNextfirst.setVisibility(View.GONE);
                    btNext.setVisibility(View.VISIBLE);
                    btLast.setVisibility(View.VISIBLE);
                    btNext.setText("下一题");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(num);

        if (mFragments.size() == 1) {
            btLast.setVisibility(View.GONE);
            btNext.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        mAnswerDao.deleteAll();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void udateUI(MsgEvent event) {
        if (event.getAction().equals("submit")) {
//            String content = event.getDoubisum();

        }
    }


    @OnClick({R.id.header_ll_left, R.id.bt_nextfirst, R.id.bt_last, R.id.bt_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_ll_left:
                showQuitDialog();
                break;
            case R.id.bt_nextfirst:
                if (mPosition == mFragments.size() - 1) {
//                    if ((System.currentTimeMillis() - submitTime) <= 2000) {
//                        //submit();
//                        Toast.makeText(this, "as", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(this, getString(R.string.toast_home_exit_system), Toast.LENGTH_SHORT).show();
//                        submitTime = System.currentTimeMillis();
//                    }

                    if (System.currentTimeMillis() - submitTime < 500) {
                        return;
                    }
                    submitTime = System.currentTimeMillis();
                    submit();

                } else {
                    mViewPager.setCurrentItem(++mPosition);
                }
                break;
            case R.id.bt_last:
                //mViewPager.setCurrentItem(mPosition);
                if (mPosition == 0) {
                    //T.showShort(this, "已经是第一个");

                } else {
                    mViewPager.setCurrentItem(--mPosition);
                }
                break;
            case R.id.bt_next:
                if (mPosition == mFragments.size() - 1) {

                    if (System.currentTimeMillis() - submitTime < 500) {
                        return;
                    }
                    submitTime = System.currentTimeMillis();
                    submit();
                    ;
                } else {
                    mViewPager.setCurrentItem(++mPosition);
                }
                break;
        }
    }

    private void showQuitDialog() {
        CustomPopDialog.Builder dialogBuild = new CustomPopDialog.Builder(AnswerActivity.this);
        final CustomPopDialog dialog = dialogBuild.create2(R.layout.dialog_publish_cancel);
        dialog.setCanceledOnTouchOutside(false);
        TextView tv = (TextView) dialog.findViewById(R.id.title_text);
        tv.setText("离开后将不会有答题记录，是否确认离开？");
        dialog.findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing())
                    dialog.dismiss();
                finish();
            }
        });
        dialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }


    class InfoAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<String> mFragmentTitles = new ArrayList<>();

        public InfoAdapter(FragmentManager fm) {
            super(fm);
        }

        public InfoAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.fragments = list;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    private void submit() {
        final List<Answer> datas = mAnswerDao.loadAll();//已作答并插入数据库的数据集合，含Long _id
        final List<Answer> datas2 = new ArrayList<>();//已作答并插入数据库的数据集合，不含Long _id
        final List<Integer> list1 = new ArrayList<>();
        final List<Integer> list2 = new ArrayList<>();

        for (int i = 1; i < num + 1; i++) {
            list1.add(i);
        }

        for (int i = 0; i < datas.size(); i++) {
            datas2.add(new Answer(datas.get(i).getShiti_id(), datas.get(i).getDaan_teacher(), datas.get(i).getShiti_ordernum()));

            list2.add(datas.get(i).getShiti_ordernum());
        }
        Log.e("答案1", constructNewsContent(datas2));
        final String content = constructNewsContent(datas2);

        phone = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, " ");
        long currentTime = System.currentTimeMillis();
        int random = (int) (Math.random() * 900) + 100;
        final String logcode = phone + currentTime + random;

        if (datas2.size() == num) {
            Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
                @Override
                public void subscribe(final ObservableEmitter<BaseDataInfo> e) throws Exception {
                    OkGo.<String>post(BASE_URL + "index.php/grenwu/examtijiao")
                            .params("appkey", MLConfig.HTTP_APP_KEY)
                            .params("examid", examid)
                            .params("phone", phone)
                            .params("unionid", unionid)
                            .params("logcode", logcode)
                            .params("content", content)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    String str = response.body().toString();
                                    Log.e("提交答案结果", str);
                                    BaseDataInfo baseDataInfo = JSON.parseObject(str, new TypeReference<BaseDataInfo>() {
                                    });
                                    e.onNext(baseDataInfo);
                                    e.onComplete();
                                }
                            });
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaseDataInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaseDataInfo dataInfo) {
                            if (dataInfo.getRet().equals("1")) {
                                T.showShort(AnswerActivity.this, "提交成功");
                                EventBus.getDefault().post(new MsgEvent("submitsuccess"));
                                mAnswerDao.deleteAll();
                                Intent intent = new Intent(AnswerActivity.this, ExamResultActivity.class);
                                intent.putExtra("examid", examid);
                                intent.putExtra("logcode", logcode);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            CustomPopDialog.Builder dialogBuild = new CustomPopDialog.Builder(AnswerActivity.this);
            final CustomPopDialog dialog = dialogBuild.create2(R.layout.dialog_exam_submit_fail);
            dialog.setCanceledOnTouchOutside(false);
            TextView tv = (TextView) dialog.findViewById(R.id.title_text);
            tv.setText("您还有题目没有完成，请继续答题");
            dialog.findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog.isShowing())
                        dialog.dismiss();

                    List<Integer> diffList = getDiffrent(list1, list2);
                    Log.e("diff集合", diffList.size() + "");
                    Collections.sort(diffList);
                    mViewPager.setCurrentItem(diffList.get(0) - 1);

                }
            });

            dialog.setCancelable(false);
            dialog.show();
        }


    }

    public String constructNewsContent(List<Answer> content) {
        return JSON.toJSONString(content);
    }

    /**
     * 获取两个List的不同元素
     *
     * @param list1
     * @param list2
     * @return
     */
    private static List<Integer> getDiffrent(List<Integer> list1, List<Integer> list2) {
        long st = System.nanoTime();
        List<Integer> diff = new ArrayList<Integer>();
        List<Integer> maxList = list1;
        List<Integer> minList = list2;
        if (list2.size() > list1.size()) {
            maxList = list2;
            minList = list1;
        }
        Map<Integer, Integer> map = new HashMap<Integer, Integer>(maxList.size());
        for (Integer string : maxList) {
            map.put(string, 1);
        }
        for (Integer string : minList) {
            if (map.get(string) != null) {
                map.put(string, 2);
                continue;
            }
            diff.add(string);
        }
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                diff.add(entry.getKey());
            }
        }
        //System.out.println("getDiffrent5 total times "+(System.nanoTime()-st));
        return diff;

    }

}
