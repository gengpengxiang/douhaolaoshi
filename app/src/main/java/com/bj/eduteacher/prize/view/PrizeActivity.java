package com.bj.eduteacher.prize.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.WebviewActivity;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.community.main.view.CustomPopDialog;
import com.bj.eduteacher.prize.model.Prize;
import com.bj.eduteacher.prize.model.PrizeResult;
import com.bj.eduteacher.prize.presenter.PrizePresenter;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StatusBarCompat;
import com.bj.eduteacher.utils.T;
import com.bumptech.glide.Glide;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;

public class PrizeActivity extends BaseActivity implements IViewPrize {

    @BindView(R.id.tv_choujiangnum)
    TextView tvChoujiangnum;
    @BindView(R.id.tv_mydooubi)
    TextView tvMydooubi;
    @BindView(R.id.layout_prize)
    FrameLayout layoutPrize;
    @BindView(R.id.bt_start)
    LinearLayout btStart;
    private Unbinder unbinder;

    private PrizePresenter prizePresenter;
    //要添加到ArrayList<ImageView>集合里的控件id，注意顺序
    private int[] ivIds = {R.id.iv1, R.id.iv2, R.id.iv3, R.id.iv4, R.id.iv5,
            R.id.iv6, R.id.iv7, R.id.iv8};
    //转圈的次数
    private final int RUN_COUNT = 2;
    private ArrayList<ImageView> ivs = new ArrayList<>();
    private int timeC = 60;//变色时间间隔
    private int lightPosition = 0;//当前亮灯位置,从0开始
    private int runCount = RUN_COUNT;//需要转多少圈
    private int luckyPosition = 0;//中奖的幸运位置,从0开始

    private TimeCount timeCount, timeCount2;
    private String teacherPhoneNumber;
    private ArrayList<Prize.DataBean.JiangpinArrayBean> prizesList = new ArrayList<>();
    private int sychoujiangnum;//剩余抽奖次数
    private String choujiangCode,priceContent;
    private AnimationDrawable animationDrawable;
    private String unionid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarCompat.fullScreen(this);
        //改变底部导航栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        }


        setContentView(R.layout.activity_prize);
        unbinder = ButterKnife.bind(this);
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID,"");
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        prizePresenter = new PrizePresenter(this, this);


        if(unionid.equals("0")){
            prizePresenter.getPrizeInfo(teacherPhoneNumber,"");
        }else {
            prizePresenter.getPrizeInfo(teacherPhoneNumber,unionid);
        }

        startAnimation();
        initTableLayout();
    }

    @Override
    protected void initStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4以下不支持状态栏变色
            //注意了，这里使用了第三方库 StatusBarUtil，目的是改变状态栏的alpha
            StatusBarUtil.setTransparentForImageView(PrizeActivity.this, null);
            //这里是重设我们的title布局的topMargin，StatusBarUtil提供了重设的方法，但是我们这里有两个布局
            //TODO 关于为什么不把Toolbar和@layout/layout_uc_head_title放到一起，是因为需要Toolbar来占位，防止AppBarLayout折叠时将title顶出视野范围
            int statusBarHeight = getStatusBarHeight(PrizeActivity.this);

        }
    }
    private int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    private void startAnimation() {
        animationDrawable = new AnimationDrawable();
        animationDrawable.addFrame(ContextCompat.getDrawable(this, R.mipmap.bg_prize_anim1), 300);
        animationDrawable.addFrame(ContextCompat.getDrawable(this, R.mipmap.bg_prize_anim2), 300);
        animationDrawable.setOneShot(false);
        layoutPrize.setBackground(animationDrawable);
        animationDrawable.start();
    }
    private void initTableLayout() {

        for (int ivId : ivIds) {
            ImageView iv = (ImageView) findViewById(ivId);
            iv.setEnabled(false);
            ivs.add(iv);
        }
    }

    @Override
    public void getPrizeInfo(Prize prize) {

        sychoujiangnum = prize.getData().getUser_syxianzhi_num();

        prizesList.addAll(prize.getData().getJiangpin_array());

        for (int i = 0; i < ivs.size(); i++) {
            Glide.with(this).load(BASE_RESOURCE_URL + prize.getData().getJiangpin_array().get(i).getImg()).fitCenter().into(ivs.get(i));
        }

        tvMydooubi.setText("我的积分：" + prize.getData().getUser_doubinum_sum());
        tvChoujiangnum.setText("今天还剩" + sychoujiangnum + "次抽奖机会");
        if(sychoujiangnum<1){
            btStart.setBackgroundResource(R.mipmap.bg_prize_gray);
        }
    }

    @Override
    public void getPrizeResult(PrizeResult prizeResult) {
        priceContent = prizeResult.getData().getGet_price_content();
        choujiangCode = prizeResult.getData().getGet_price_code();
        sychoujiangnum = prizeResult.getData().getUser_sychoujiang_num();
        tvMydooubi.setText("我的积分：" + prizeResult.getData().getUser_doubinum_sum());
        tvChoujiangnum.setText("今天还剩" + sychoujiangnum + "次抽奖机会");
        if(sychoujiangnum<1){
            btStart.setBackgroundResource(R.mipmap.bg_prize_gray);
        }

        btStart.setEnabled(false);
        //runCount为需要转多少圈
        runCount = RUN_COUNT;//RUN_COUNT为转圈的次数
        timeC = 100;//变色时间间隔
        ivs.get(luckyPosition).setBackground(ContextCompat.getDrawable(this,
                R.drawable.bg_prize_item_normal));
        luckyPosition = Integer.parseInt(prizeResult.getData().getGet_price_code()) - 1;//自己设置中奖位置
        timeCount = (TimeCount) new TimeCount(timeC * 9, timeC);
        timeCount.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timeCount != null) {
            timeCount.cancel();
        }
        if (timeCount2 != null) {
            timeCount2.cancel();
        }
        if(animationDrawable.isRunning()){
            animationDrawable.stop();
            animationDrawable = null;
            //recycleAnimationDrawable(animationDrawable);
        }
        prizePresenter.onDestory();
        unbinder.unbind();
    }

    @OnClick({R.id.bt_back, R.id.bt_rule,R.id.bt_start})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_back:
                finish();
                break;
            case R.id.bt_rule:
                Intent intent = new Intent(this, WebviewActivity.class);
                String resName = "逗号老师幸运大抽奖活动规则";
                //String previewUrl = "http://mp.weixin.qq.com/s/8hF-IQVrBhMjGtUS3bprow";
                String previewUrl = "https://douhaolaoshi.gamepku.com/index.php/wenzhang/index/969";
                //add
               // String previewUrl = "https://douhaolaoshi.gamepku.com/index.php/wenzhang/index/1101";
                intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                startActivity(intent);
                break;
            case R.id.bt_start:
                if(sychoujiangnum<1){
                    T.showShort(this,"抽奖次数已用尽");
                }else {
                    prizePresenter.getPrizeResult(teacherPhoneNumber,unionid);
                }
                break;
        }
    }

    /**
     * 用来控制ImageButton状态切换的倒计时
     */
    class TimeCount extends CountDownTimer {
        TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            lightPosition = 0;//当前亮灯位置,从0开始
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //如果是最后一次滚动
            if (runCount > 0) {
                //lightPosition为当前亮灯位置,从0开始
                if (lightPosition > 0) {
                    ivs.get(lightPosition - 1).setBackground(
                            ContextCompat.getDrawable(PrizeActivity.this,
                                    R.drawable.bg_prize_item_normal));
                }
                if (lightPosition < 8) {
                    ivs.get(lightPosition).setBackground(ContextCompat.getDrawable(PrizeActivity.this,
                            R.drawable.bg_prize_item_selected));
                }

            } else if (runCount == 0) {
                if (lightPosition <= luckyPosition) {
                    if (lightPosition > 0) {
                        ivs.get(lightPosition - 1).setBackground(ContextCompat.getDrawable(PrizeActivity.this,
                                R.drawable.bg_prize_item_normal));
                    }
                    if (lightPosition < 8) {
                        ivs.get(lightPosition).setBackground(ContextCompat.getDrawable(PrizeActivity.this,
                                R.drawable.bg_prize_item_selected));
                    }
                }
            }

            lightPosition++;
        }

        @Override
        public void onFinish() {
            ImageView ivLast = ivs.get(7);
            if (runCount != 0) {

                ivLast.setBackground(ContextCompat.getDrawable(PrizeActivity.this,
                        R.drawable.bg_prize_item_normal));
                //最后几转速度变慢
//                if (runCount < 3) timeC += 200;
                if (runCount < 3) timeC += 100;
                //在设定的所转圈数内开启新的倒计时切换item样式
//                new TimeCount(timeC * 9, timeC).start();
                timeCount2 = (TimeCount) new TimeCount(timeC * 9, timeC);
                timeCount2.start();
                runCount--;
            }
            //如果是最后一圈且计时也已经结束
            if (runCount == 0 && lightPosition == 8) {
                //动画未执行完退出
                if (btStart != null) {
                    btStart.setEnabled(true);
                }
                if(choujiangCode.equals("8")){
                    T.showShort(PrizeActivity.this,  priceContent);
                }else {
                    showPrizeDialog();
                }
                if (luckyPosition != ivs.size()) {
//                    ivLast.setBackground(ContextCompat.getDrawable(PrizeActivity.this,
//                            R.drawable.bg_prize_item_normal));
                }
            }

        }
    }

    private void showPrizeDialog() {
        CustomPopDialog.Builder dialogBuild = new CustomPopDialog.Builder(PrizeActivity.this);
        final CustomPopDialog dialog = dialogBuild.create2(R.layout.dialog_win_prize);
        dialog.setCanceledOnTouchOutside(false);
        TextView tvPrizeInfo = (TextView) dialog.findViewById(R.id.tv_prizeInfo);
        tvPrizeInfo.setText(priceContent);
        dialog.findViewById(R.id.bt_know).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
        dialog.show();
    }
}
