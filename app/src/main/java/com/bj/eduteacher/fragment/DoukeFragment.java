package com.bj.eduteacher.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bj.eduteacher.BaseFragment;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.AnnualCaseAllActivity;
import com.bj.eduteacher.activity.CourseDetailActivity;
import com.bj.eduteacher.activity.DoukeDetailActivity;
import com.bj.eduteacher.activity.FamousTeacherAllActivity;
import com.bj.eduteacher.activity.FamousTeacherDetailActivity;
import com.bj.eduteacher.activity.LiveActivity;
import com.bj.eduteacher.activity.LiveAllActivity;
import com.bj.eduteacher.activity.PayProtocolActivity;
import com.bj.eduteacher.activity.ResPlayActivity;
import com.bj.eduteacher.activity.ResReviewActivity;
import com.bj.eduteacher.activity.ThanksNotifiActivity;
import com.bj.eduteacher.activity.WebviewActivity;
import com.bj.eduteacher.activity.ZhuanjiaAllActivity;
import com.bj.eduteacher.activity.ZhuanjiaDetailActivity;
import com.bj.eduteacher.adapter.DoukeListAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.dialog.TipsAlertDialog3;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.entity.OrderInfo;
import com.bj.eduteacher.entity.TradeInfo;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.model.CurLiveInfo;
import com.bj.eduteacher.model.MySelfInfo;
import com.bj.eduteacher.tool.Constants;
import com.bj.eduteacher.tool.ShowNameUtil;
import com.bj.eduteacher.utils.FrescoImageLoader;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.NetUtils;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.view.OnRecyclerItemClickListener;
import com.bj.eduteacher.widget.dialog.RadioGroupDialog;
import com.bj.eduteacher.widget.manager.SaveGridLayoutManager;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import io.reactivex.functions.Action;
import io.reactivex.functions.Function7;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zz379 on 2017/4/7.
 * 首页
 */

public class DoukeFragment extends BaseFragment {

    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.header_tv_title)
    TextView tvTitle;
    @BindView(R.id.header_fl_notification)
    FrameLayout flHeaderNotifi;
    @BindView(R.id.header_img_notification)
    ImageView imgHeaderRightNotification;
    @BindView(R.id.header_img_notificationdot)
    ImageView imgHeaderRightNotificationdot;

    private String teacherPhoneNumber, phoneNumberBack;
    private boolean unReadMsg = false;

    private DoukeListAdapter mAdapter;
    private int currentPage = 1;
    public static long lastRefreshTime;
    private List<ArticleInfo> mDataList = new ArrayList<>();
    private List<ArticleInfo> mBannerList = new ArrayList<>();
    private final CompositeDisposable disposables = new CompositeDisposable();

    private Banner banner;
    List<String> headerImages = new ArrayList<>();
    private GridLayoutManager layoutManager;

    private IWXAPI api;
    private PopupWindow popPayDetail;
    private boolean isUserPaySuccess = false;  // 是否支付成功
    private String currTradeID = "";     // 当前商户订单号

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_douke, container, false);
        ButterKnife.bind(this, view);
        api = WXAPIFactory.createWXAPI(getActivity(), MLProperties.APP_DOUHAO_TEACHER_ID);

        initToolbar();
        initView();
        initData();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        tvTitle.setText(R.string.app_name);

        flHeaderNotifi.setVisibility(View.VISIBLE);
        imgHeaderRightNotification.setVisibility(View.VISIBLE);
        unReadMsg = true;
    }

    private void initView() {
        teacherPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
        // 下拉刷新控件
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new SaveGridLayoutManager(getActivity(), 6);
        mAdapter = new DoukeListAdapter(getActivity(), mDataList);
        // 添加header
        banner = (Banner) mAdapter.setHeaderView(R.layout.recycler_header_banner, mRecyclerView);
        initHeaderView();

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                if (position > 0) {
                    ArticleInfo item = mDataList.get(position - 1);
                    actionOnItemClick(item);
                }
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder holder, int position) {

            }
        });

        // set xRefreshView
        mXRefreshView.setMoveForHorizontal(true);   // 在手指横向移动的时候，让XRefreshView不拦截事件
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(true);
        mXRefreshView.restoreLastRefreshTime(lastRefreshTime);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(true);
        mXRefreshView.setEmptyView(R.layout.recycler_item_douke_empty);
        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                LL.i("刷新数据");
                currentPage = 1;

                getBannerInfoFromAPI();
                getRefreshDataList();
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                LL.i("加载更多数据");
                currentPage++;

                getDoukeList();
            }
        });
    }

    /**
     * 页面的点击动作
     *
     * @param item
     */
    private void actionOnItemClick(ArticleInfo item) {
        if (item.getShowType() == ArticleInfo.SHOW_TYPE_ZHUANJIA) {
            MobclickAgent.onEvent(getActivity(), "expert_click");
            Intent intent = new Intent(getActivity(), ZhuanjiaDetailActivity.class);
            intent.putExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_ID, item.getArticleID());
            intent.putExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_NAME, item.getAuthor());
            intent.putExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_TITLE, item.getTitle());
            intent.putExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_IMG, item.getAuthImg());
            startActivity(intent);
        } else if (item.getShowType() == ArticleInfo.SHOW_TYPE_ZHUANJIA_ALL) {
            String text = item.getTitle();
            if (text.endsWith("专家")) {
                Intent intent = new Intent(getActivity(), ZhuanjiaAllActivity.class);
                startActivity(intent);
            } else if (text.endsWith("名师")) {
                Intent intent = new Intent(getActivity(), FamousTeacherAllActivity.class);
                startActivity(intent);
            } else if (text.endsWith("直播")) {
                Intent intent = new Intent(getActivity(), LiveAllActivity.class);
                startActivity(intent);
            } else {
                if (bottomTabListener != null) {
                    bottomTabListener.onTabChange(1);
                }
            }
        } else if (item.getShowType() == ArticleInfo.SHOW_TYPE_ZHUANJIA_RES) {
            String price = item.getAgreeNumber();
            String buyType = item.getCommentNumber();

            if (!StringUtils.isEmpty(price) && !"0".equals(price) && "0".equals(buyType)) {
                // 如果资源不是免费，需要先登录
                if (StringUtils.isEmpty(teacherPhoneNumber)) {
                    IntentManager.toLoginActivity(getActivity(), IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                    return;
                }

                MobclickAgent.onEvent(getActivity(), "doc_buy");
                initPopViewPayDetail(item.getArticleID(), item.getAgreeNumber());
            } else {
                MobclickAgent.onEvent(getActivity(), "doc_look");
                String resID = item.getArticleID();
                String resName = item.getTitle();
                String previewUrl = item.getArticlePath();
                String downloadUrl = item.getArticlePicture();
                String resType = item.getPreviewType();  // 目前先根据这个类型来判断是否是视频
                if ("2".equals(resType)) {
                    Intent intent = new Intent(getActivity(), ResPlayActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), ResReviewActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_DOWNLOAD_URL, downloadUrl);
                    startActivity(intent);
                }
            }
        } else if (item.getShowType() == ArticleInfo.SHOW_TYPE_TEACHER) {
            Intent intent = new Intent(getActivity(), FamousTeacherDetailActivity.class);
            intent.putExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_ID, item.getArticleID());
            intent.putExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_NAME, item.getAuthor());
            intent.putExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_TITLE, item.getTitle());
            intent.putExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_IMG, item.getAuthImg());
            startActivity(intent);
        } else if (item.getShowType() == ArticleInfo.SHOW_TYPE_DOUKE) {
            String path = item.getArticlePath();
            String id = item.getArticleID();
            if (!StringUtils.isEmpty(path)) {
                Intent intent = new Intent(getActivity(), DoukeDetailActivity.class);
                intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, id);
                intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_URL, path);
                startActivity(intent);
            } else {
                T.showShort(getActivity(), "页面不存在");
            }
        } else if (item.getShowType() == ArticleInfo.SHOW_TYPE_LIVE) {
            // 观看直播需要先进行登录
            if (StringUtils.isEmpty(teacherPhoneNumber)) {
                IntentManager.toLoginActivity(getActivity(), IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                return;
            }

            if (item.getAuthDesc().equals(MySelfInfo.getInstance().getId())) {
                returnBackRoom();
            } else {
                String price = item.getAgreeNumber();
                String buyType = item.getCommentNumber();

                if (!StringUtils.isEmpty(price) && !"0".equals(price) && "0".equals(buyType)) {
                    initPopViewPayDetailForLive(item.getArticleID(), item.getAuthDesc(), item.getAgreeNumber());
                } else {
                    MySelfInfo.getInstance().setIdStatus(Constants.MEMBER);
                    MySelfInfo.getInstance().setJoinRoomWay(false);
                    CurLiveInfo.setHostID(item.getAuthDesc());
                    String phone;
                    if (!StringUtils.isEmpty(item.getAuthDesc())) {
                        phone = item.getAuthDesc().substring(3);
                        if (!StringUtils.isEmpty(phone) && phone.length() > 10) {
                            phone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                        }
                    } else {
                        phone = "";
                    }
                    CurLiveInfo.setHostName(ShowNameUtil.getFirstNotNullParams(item.getNickname(), item.getAuthor(), phone));
                    CurLiveInfo.setHostAvator(item.getAuthImg());
                    CurLiveInfo.setRoomNum(Integer.valueOf(item.getArticleID()));
                    CurLiveInfo.setTitle(item.getTitle());
                    CurLiveInfo.setCoverurl(item.getArticlePicture());
                    checkJoinLive();
                }
            }
        } else if (item.getShowType() == ArticleInfo.SHOW_TYPE_LATEST_RES) {
            String previewType = item.getPreviewType();
            // 如果是文章，跳转到文章详情页面
            if ("文章".equals(previewType)) {
                // 跳转到逗课页面
                Intent intent = new Intent(getActivity(), DoukeDetailActivity.class);
                intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, item.getArticleID());
                intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_URL, item.getArticlePath());
                startActivity(intent);
                return;
            }
            // 如果是资源，跳转到资源页面
            String price = item.getAgreeNumber();
            String buyType = item.getCommentNumber();

            if (!StringUtils.isEmpty(price) && !"0".equals(price) && "0".equals(buyType)) {
                // 如果资源不是免费，需要先登录
                if (StringUtils.isEmpty(teacherPhoneNumber)) {
                    IntentManager.toLoginActivity(getActivity(), IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                    return;
                }

                MobclickAgent.onEvent(getActivity(), "doc_buy");
                initPopViewPayDetail(item.getArticleID(), item.getAgreeNumber());
            } else {
                if ("视频".equals(previewType)) {
                    // 跳转动视频播放页面
                    String resID = item.getArticleID();
                    String resName = item.getTitle();
                    String previewUrl = item.getArticlePath();

                    Intent intent = new Intent(getActivity(), ResPlayActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                    startActivity(intent);
                } else {
                    // 跳转到文档页面
                    String resID = item.getArticleID();
                    String resName = item.getTitle();
                    String previewUrl = item.getArticlePath();
                    String downloadUrl = item.getAuthor();

                    Intent intent = new Intent(getActivity(), ResReviewActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_DOWNLOAD_URL, downloadUrl);
                    startActivity(intent);
                }
            }
        } else if (item.getShowType() == ArticleInfo.SHOW_TYPE_COURSE) {
            Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
            Bundle args = new Bundle();
            args.putString("CourseID", item.getArticleID());
            args.putString("CourseTitle", item.getTitle());
            args.putString("CourseLearnNum", item.getReplyCount());
            args.putString("CourseResNum", item.getReadNumber());
            args.putString("CoursePicture", item.getArticlePicture());

            args.putString("CoursePrice", item.getAgreeNumber());
            args.putString("CourseBuyStatus", item.getCommentNumber());
            args.putString("CourseDesc", item.getAuthDesc());
            args.putString("CourseZhengshu", item.getAuthImg());
            args.putString("CourseShuoming", item.getContent());

            intent.putExtras(args);
            startActivity(intent);
        }
    }

    private void returnBackRoom() {
        Intent intent = new Intent(getActivity(), LiveActivity.class);
        MySelfInfo.getInstance().setIdStatus(Constants.HOST);
        MySelfInfo.getInstance().setJoinRoomWay(true);
        CurLiveInfo.setHostID(MySelfInfo.getInstance().getId());
        CurLiveInfo.setRoomNum(MySelfInfo.getInstance().getMyRoomNum());
        String sxbTitle = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_SXB_Title, "");
        String sxbPic = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_SXB_Picture, "");
        CurLiveInfo.setTitle(sxbTitle);
        if (!StringUtils.isEmpty(sxbPic)) {
            CurLiveInfo.setCoverurl(sxbPic.substring(sxbPic.lastIndexOf("/") + 1));
        }
        intent.putExtra("HostComeBack", true);
        startActivity(intent);
    }

    private void checkJoinLive() {
        if (TextUtils.isEmpty(MySelfInfo.getInstance().getGuestRole())) {
            final String[] roles = new String[]{getString(R.string.str_video_sd), getString(R.string.str_video_ld)};
            final String[] values = new String[]{Constants.SD_GUEST, Constants.LD_GUEST};

            RadioGroupDialog roleDialog = new RadioGroupDialog(getActivity(), roles);

            roleDialog.setTitle(R.string.str_video_qulity);
            roleDialog.setOnItemClickListener(new RadioGroupDialog.onItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    MySelfInfo.getInstance().setGuestRole(values[position]);
                    MySelfInfo.getInstance().writeToCache(getActivity());
                    Intent intent = new Intent(getActivity(), LiveActivity.class);
                    startActivity(intent);
                }
            });
            roleDialog.show();
        } else {
            Intent intent = new Intent(getActivity(), LiveActivity.class);
            startActivity(intent);
        }
    }

    private void initHeaderView() {
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new FrescoImageLoader());
        //设置图片集合
        // banner.setImages(headerImages);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.Default);
        //设置标题集合（当banner样式有显示title时）
        // banner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(4000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                MobclickAgent.onEvent(getActivity(), "banner_click");
                if ("zj".equals(mBannerList.get(position).getTitle())) {
                    // 跳转到专家详情页面
                    Intent intent = new Intent(getActivity(), ZhuanjiaDetailActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_ID, mBannerList.get(position).getArticleID());
                    startActivity(intent);
                } else if ("dk".equals(mBannerList.get(position).getTitle())) {
                    // 跳转到逗课页面
                    Intent intent = new Intent(getActivity(), DoukeDetailActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, mBannerList.get(position).getArticleID());
                    intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_URL, mBannerList.get(position).getArticlePath());
                    startActivity(intent);
                } else if ("sp".equals(mBannerList.get(position).getTitle())) {
                    // 跳转动视频播放页面
                    String resID = mBannerList.get(position).getArticleID();
                    String resName = mBannerList.get(position).getContent();
                    String previewUrl = mBannerList.get(position).getArticlePath();

                    Intent intent = new Intent(getActivity(), ResPlayActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                    startActivity(intent);
                } else if ("huodong".equals(mBannerList.get(position).getTitle())) {
                    // 年会活动，案例投票
                    Intent intent = new Intent(getActivity(), AnnualCaseAllActivity.class);
                    intent.putExtra("huodongID", mBannerList.get(position).getArticleID());
                    startActivity(intent);
                } else if ("zy".equals(mBannerList.get(position).getTitle())) {
                    // 跳转到文档页面
                    String resID = mBannerList.get(position).getArticleID();
                    String resName = mBannerList.get(position).getContent();
                    String previewUrl = mBannerList.get(position).getArticlePath();
                    String downloadUrl = mBannerList.get(position).getAuthor();

                    Intent intent = new Intent(getActivity(), ResReviewActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_DOWNLOAD_URL, downloadUrl);
                    startActivity(intent);
                } else {
                    // 其余的情况跳转到一个单纯的webView页面
                    String resName = mBannerList.get(position).getContent();
                    String previewUrl = mBannerList.get(position).getArticlePath();

                    Intent intent = new Intent(getActivity(), WebviewActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                    startActivity(intent);
                }
            }
        });
    }

    private void initData() {
        currentPage = 1;
        mDataList.clear();

        getBannerInfoFromAPI();
        getRefreshDataList();
    }

    private void getRefreshDataList() {
        if (!NetUtils.isConnected(getActivity())) {
            T.showShort(getActivity(), "无法连接到网络，请检查您的网络设置");
            cleanXRefreshView();
            return;
        }
        final LmsDataService mService = new LmsDataService();
        // 获取专家卡片列表
        Observable<List<ArticleInfo>> observable1 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                List<ArticleInfo> dataList = mService.getMasterCardsFromAPI(currentPage, 5);
                if (dataList.size() >= 5) {
                    ArticleInfo articleInfo = mService.getMasterCountFromAPI();
                    int count = Integer.parseInt(articleInfo.getReplyCount());
                    if (count > 5) {    // 超过5位专家，显示查看全部
                        dataList.add(new ArticleInfo("查看全部" + count + "位专家", ArticleInfo.SHOW_TYPE_ZHUANJIA_ALL));
                    }
                }
                e.onNext(dataList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        // 获取逗课列表
        Observable<List<ArticleInfo>> observable2 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                List<ArticleInfo> dataList = mService.getDouKeListFromAPI(currentPage);
                e.onNext(dataList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        // 获取专家日课
        Observable<List<ArticleInfo>> observable3 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                // SystemClock.sleep(1000);
                List<ArticleInfo> dataList = mService.getMasterRikeFromAPI(teacherPhoneNumber);
                e.onNext(dataList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        // 获取每日一课列表
        Observable<List<ArticleInfo>> observable4 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                // SystemClock.sleep(1000);
                List<ArticleInfo> dataList = mService.getHomePageLatestRes(teacherPhoneNumber);
                e.onNext(dataList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        // 获取名师卡片列表
        Observable<List<ArticleInfo>> observable5 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                List<ArticleInfo> dataList = mService.getFamousTeacherCardsFromAPI(currentPage, 8);
                if (dataList.size() >= 8) {
                    ArticleInfo articleInfo = mService.getFamousTeacherCountFromAPI();
                    int count = Integer.parseInt(articleInfo.getReplyCount());
                    if (count > 8) {    // 超过8位名师，显示查看全部
                        dataList.add(new ArticleInfo("查看全部" + count + "位名师", ArticleInfo.SHOW_TYPE_ZHUANJIA_ALL));
                    }
                }
                e.onNext(dataList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        // 获取名师成长课程列表
        Observable<List<ArticleInfo>> observable6 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                // SystemClock.sleep(1000);
                List<ArticleInfo> dataList = mService.getHomePageCourseList(teacherPhoneNumber);
                e.onNext(dataList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        // 获取正在直播的列表
        Observable<List<ArticleInfo>> observable7 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                List<ArticleInfo> liveList = mService.getLiveListFromAPI(teacherPhoneNumber, "1", 2, 0);
                e.onNext(liveList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, observable3, observable4, observable5, observable6, observable7
                , new Function7<List<ArticleInfo>, List<ArticleInfo>, List<ArticleInfo>, List<ArticleInfo>, List<ArticleInfo>, List<ArticleInfo>, List<ArticleInfo>, List<ArticleInfo>>() {
                    @Override
                    public List<ArticleInfo> apply(@NonNull List<ArticleInfo> data1, @NonNull List<ArticleInfo> data2, @NonNull List<ArticleInfo> data3, @NonNull List<ArticleInfo> data4, @NonNull List<ArticleInfo> data5, @NonNull List<ArticleInfo> data6, @NonNull List<ArticleInfo> data7) throws Exception {
                        List<ArticleInfo> dataList = new ArrayList<>();
                        if (data4.size() > 0) {
                            dataList.add(new ArticleInfo("每日一课", ArticleInfo.SHOW_TYPE_DECORATION));
                            dataList.addAll(data4);
                        }
                        if (data7.size() > 0) {
                            dataList.add(new ArticleInfo("正在直播", ArticleInfo.SHOW_TYPE_DECORATION));
                            dataList.addAll(data7);
                            dataList.add(new ArticleInfo("查看全部直播", ArticleInfo.SHOW_TYPE_ZHUANJIA_ALL));
                        }
                        if (data6.size() > 0) {
                            dataList.add(new ArticleInfo("名师成长课程", ArticleInfo.SHOW_TYPE_DECORATION));
                            dataList.addAll(data6);
                        }
                        if (data3.size() > 0) {
                            dataList.add(new ArticleInfo("逗课精选", ArticleInfo.SHOW_TYPE_DECORATION));
                            dataList.addAll(data3);
                            dataList.add(new ArticleInfo("查看全部", ArticleInfo.SHOW_TYPE_ZHUANJIA_ALL));
                        }
                        if (data1.size() > 0) {
                            dataList.add(new ArticleInfo("专家驻场", ArticleInfo.SHOW_TYPE_DECORATION));
                            dataList.addAll(data1);
                        }
                        if (data5.size() > 0) {
                            dataList.add(new ArticleInfo("名师堂", ArticleInfo.SHOW_TYPE_DECORATION));
                            dataList.addAll(data5);
                        }
                        if (data2.size() > 0) {
                            dataList.add(new ArticleInfo("干货精选", ArticleInfo.SHOW_TYPE_DECORATION));
                            dataList.addAll(data2);
                        }
                        return dataList;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<ArticleInfo> result) {
                        loadRefreshData(result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LL.e(e);
                        hideLoadingDialog();
                        cleanXRefreshView();
                        T.showShort(getActivity(), "服务器开小差了，请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadRefreshData(List<ArticleInfo> result) {
        hideLoadingDialog();

        lastRefreshTime = mXRefreshView.getLastRefreshTime();
        mXRefreshView.stopRefresh();
        mDataList.clear();
        mDataList.addAll(result);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter.getAdapterItemCount() == 0) {
                    return 6;
                }

                if (mAdapter.getItemViewType(position) == DoukeListAdapter.ShowType.ITEM_TYPE_ZHUANJIA_RES.ordinal()) {
                    return 2;
                } else if (mAdapter.getItemViewType(position) == DoukeListAdapter.ShowType.ITEM_TYPE_TEACHER.ordinal()) {
                    return 3;
                } else {
                    return 6;
                }
            }
        });

        mAdapter.notifyDataSetChanged();
        if (null == mAdapter.getCustomLoadMoreView()) {
            mAdapter.setCustomLoadMoreView(new XRefreshViewFooter(getActivity()));
        }
    }

    private void getDoukeList() {
        if (!NetUtils.isConnected(getActivity())) {
            T.showShort(getActivity(), "无法连接到网络，请检查您的网络设置");
            cleanXRefreshView();
            return;
        }
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                List<ArticleInfo> dataList = mService.getDouKeListFromAPI(currentPage);
                e.onNext(dataList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<ArticleInfo> result) {
                        loadData(result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LL.e(e);
                        cleanXRefreshView();
                        T.showShort(getActivity(), "服务器开小差了，请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadData(List<ArticleInfo> list) {
        lastRefreshTime = mXRefreshView.getLastRefreshTime();
        if (list == null || list.size() < 10) {
            mXRefreshView.setPullLoadEnable(false);
        }
        mXRefreshView.stopLoadMore();
        // 更新数据
        mDataList.addAll(list);
        mAdapter.notifyDataSetChanged();
        if (null == mAdapter.getCustomLoadMoreView()) {
            mAdapter.setCustomLoadMoreView(new XRefreshViewFooter(getActivity()));
        }
    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }

    private void getBannerInfoFromAPI() {
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                LmsDataService dataService = new LmsDataService();
                List<ArticleInfo> list = dataService.getBannerInfoFromAPI();
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<ArticleInfo> result) {
                        loadBannerInfo(result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.getMessage();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadBannerInfo(List<ArticleInfo> result) {
        mBannerList.clear();
        mBannerList.addAll(result);
        headerImages.clear();
        for (ArticleInfo item : result) {
            headerImages.add(item.getArticlePicture());
        }

        //设置图片集合
        banner.setImages(headerImages);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        phoneNumberBack = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
        if (!phoneNumberBack.equals(teacherPhoneNumber)) {
            // 前后两次手机号不同的时候刷新数据
            teacherPhoneNumber = phoneNumberBack;
            initData();
        }
        if (!StringUtils.isEmpty(teacherPhoneNumber)) {
            unReadMsg = false;
            imgHeaderRightNotificationdot.setVisibility(View.GONE);
            startGetUnReadMessageNumber();  // 开始轮询获取消息
        }
        // 查询订单
        if (!StringUtils.isEmpty(currTradeID)) {
            queryTheTradeStateFromAPI(currTradeID);
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        MobclickAgent.onPageStart("douke");
        if (getActivity() != null) {
            phoneNumberBack = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
            if (!phoneNumberBack.equals(teacherPhoneNumber)) {
                // 前后两次手机号不同的时候刷新数据
                teacherPhoneNumber = phoneNumberBack;
                initData();
            }
        }
    }

    @Override
    protected void onInVisible() {
        super.onInVisible();
        MobclickAgent.onPageEnd("douke");
    }

    public void cleanNotice() {
        // 退出账号的时候 停止轮询
        disposables.clear();
        unReadMsg = false;
        imgHeaderRightNotificationdot.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        // 停止轮询
        disposables.clear();
    }

    @OnClick(R.id.header_ll_right)
    void actionHeaderRightClick() {
        if (StringUtils.isEmpty(teacherPhoneNumber)) {
            IntentManager.toLoginActivity(getActivity(), IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
            return;
        }
        // 所有消息置为已读
        unReadMsg = false;
        imgHeaderRightNotificationdot.setVisibility(View.GONE);
        // 跳转到感谢列表页面
        Intent intent = new Intent(getActivity(), ThanksNotifiActivity.class);
        startActivity(intent);
    }

    /**
     * 轮询查找未读消息
     */
    private void startGetUnReadMessageNumber() {
        LL.i("DoukeFragment -- onNext()................");
        final LmsDataService service = new LmsDataService();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<Integer> integer) throws Exception {
                if (integer.isDisposed()) return;
                Disposable disposable = Schedulers.io()
                        .createWorker()
                        .schedulePeriodically(new Runnable() {
                            @Override
                            public void run() {
                                Integer result;
                                try {
                                    result = service.getTeacherUnReadMessageNumberFromAPI(teacherPhoneNumber);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    LL.e(e);
                                    result = 0;
                                }
                                integer.onNext(result);
                            }
                        }, 1, 5, TimeUnit.SECONDS);
                disposables.add(disposable);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        LL.i("DoukeFragment -- 取消观察....");
                    }
                })
                .subscribe(new Observer<Integer>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (StringUtils.isEmpty(teacherPhoneNumber)) {
                            unReadMsg = false;
                            imgHeaderRightNotificationdot.setVisibility(View.GONE);
                            return;
                        }
                        if (integer > 0) {
                            unReadMsg = true;
                            imgHeaderRightNotificationdot.setVisibility(View.VISIBLE);
                        } else {
                            unReadMsg = false;
                            imgHeaderRightNotificationdot.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LL.e(e);
                        if (unReadMsg) {
                            imgHeaderRightNotificationdot.setVisibility(View.VISIBLE);
                        } else {
                            imgHeaderRightNotificationdot.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /*************************** 支付 **************************************/
    private void initPopViewPayDetail(final String masterid, final String realPrice) {
        View popView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_pay_masterres_detail, null);
        popPayDetail = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popPayDetail.setAnimationStyle(R.style.MyPopupWindow_anim_style);
        popPayDetail.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popPayDetail.setFocusable(true);
        popPayDetail.setOutsideTouchable(true);
        popPayDetail.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(getActivity(), 1f);
            }
        });
        TextView tvRealPay = (TextView) popView.findViewById(R.id.tv_realPay);
        tvRealPay.setText("¥ " + (Double.parseDouble(realPrice)) / 100);
        TextView tvReportProtocol = (TextView) popView.findViewById(R.id.tv_report_protocol);
        tvReportProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PayProtocolActivity.class);
                startActivity(intent);
            }
        });
        TextView tvPay = (TextView) popView.findViewById(R.id.tv_payReport);
        tvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开始支付,隐藏支付窗口
                hidePopViewPayDetail();
                // 获取订单号
                getTheOrderFromAPI(masterid, String.valueOf(realPrice), "masterres");
                MobclickAgent.onEvent(getActivity(), "masterres_pay");
            }
        });
        // 显示页面
        showPopViewPayDetail();
    }

    private void initPopViewPayDetailForLive(final String masterid, final String sxbroomuser, final String realPrice) {
        View popView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_pay_masterres_detail, null);
        popPayDetail = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popPayDetail.setAnimationStyle(R.style.MyPopupWindow_anim_style);
        popPayDetail.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popPayDetail.setFocusable(true);
        popPayDetail.setOutsideTouchable(true);
        popPayDetail.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(getActivity(), 1f);
            }
        });
        TextView tvRealPay = (TextView) popView.findViewById(R.id.tv_realPay);
        tvRealPay.setText("¥ " + (Double.parseDouble(realPrice)) / 100);
        TextView tvReportProtocol = (TextView) popView.findViewById(R.id.tv_report_protocol);
        tvReportProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PayProtocolActivity.class);
                startActivity(intent);
            }
        });
        TextView tvPay = (TextView) popView.findViewById(R.id.tv_payReport);
        tvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开始支付,隐藏支付窗口
                hidePopViewPayDetail();
                // 获取订单号
                getTheOrderFromAPIForLive(masterid, sxbroomuser, String.valueOf(realPrice), "suixinbo");
            }
        });
        // 显示页面
        showPopViewPayDetail();
    }

    private void showPopViewPayDetail() {
        if (popPayDetail != null && !popPayDetail.isShowing()) {
            setBackgroundAlpha(getActivity(), 0.5f);
            popPayDetail.showAtLocation(mRecyclerView, Gravity.BOTTOM, 0, popPayDetail.getHeight());
        }
    }

    private void hidePopViewPayDetail() {
        if (popPayDetail != null && popPayDetail.isShowing()) {
            popPayDetail.dismiss();
            popPayDetail = null;
        }
    }

    /**
     * 设置背景透明度
     *
     * @param activity
     * @param bgAlpha
     */
    public void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 生成订单
     */
    private void getTheOrderFromAPI(final String masterresid, final String price, final String payType) {
        Observable.create(new ObservableOnSubscribe<OrderInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<OrderInfo> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                OrderInfo info = mService.getTheOrderInfoFromAPI(masterresid, price, teacherPhoneNumber, payType);
                e.onNext(info);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // showLoadingDialog();
                    }

                    @Override
                    public void onNext(OrderInfo orderInfo) {
                        // 获取订单成功后 发起微信支付
                        if ("SUCCESS".equals(orderInfo.getResult_code())
                                && "SUCCESS".equals(orderInfo.getReturn_code())
                                && "OK".equals(orderInfo.getReturn_msg())) {
                            startPay(orderInfo);
                        } else {
                            // 获取订单失败
                            T.showShort(getActivity(), "订单生成失败");
                        }
                        hideLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getTheOrderFromAPIForLive(final String masterresid, final String sxbroomuser, final String price, final String payType) {
        Observable.create(new ObservableOnSubscribe<OrderInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<OrderInfo> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                OrderInfo info = mService.getTheOrderInfoFromAPIForLive(masterresid, sxbroomuser, price, teacherPhoneNumber, payType);
                e.onNext(info);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // showLoadingDialog();
                    }

                    @Override
                    public void onNext(OrderInfo orderInfo) {
                        // 获取订单成功后 发起微信支付
                        if ("SUCCESS".equals(orderInfo.getResult_code())
                                && "SUCCESS".equals(orderInfo.getReturn_code())
                                && "OK".equals(orderInfo.getReturn_msg())) {
                            startPay(orderInfo);
                        } else {
                            // 获取订单失败
                            T.showShort(getActivity(), "订单生成失败");
                        }
                        hideLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 开始支付
     *
     * @param orderInfo
     */
    private void startPay(OrderInfo orderInfo) {
        if (api.getWXAppSupportAPI() >= com.tencent.mm.opensdk.constants.Build.PAY_SUPPORTED_SDK_INT) {
            currTradeID = orderInfo.getOut_trade_no();  // 获取当前商户订单号
            PayReq request = new PayReq();
            request.appId = orderInfo.getAppid();
            request.partnerId = orderInfo.getMch_id();   // 商户号
            request.prepayId = orderInfo.getPrepay_id();
            request.packageValue = "Sign=WXPay";
            request.nonceStr = orderInfo.getNonce_str();
            request.timeStamp = orderInfo.getTimeStamp();
            request.extData = "app data";
            request.sign = orderInfo.getSign();
            api.sendReq(request);
        } else {
            T.showShort(getActivity(), "当前手机暂不支持该功能");
        }
    }

    /**
     * 查询订单支付状态
     *
     * @param tradeID
     */
    private void queryTheTradeStateFromAPI(final String tradeID) {
        Observable.create(new ObservableOnSubscribe<TradeInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<TradeInfo> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                TradeInfo info = mService.getTheTradeInfoFromAPI(tradeID);
                e.onNext(info);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TradeInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TradeInfo tradeInfo) {
                        currTradeID = "";
                        if ("SUCCESS".equals(tradeInfo.getResult_code())) {
                            if ("SUCCESS".equals(tradeInfo.getTrade_state())) {
                                showAlertDialog("支付成功", "您现在就可以去查看完整资料啦");
                            } else if ("NOTPAY".equals(tradeInfo.getTrade_state())) {
                                showAlertDialog("支付失败", "支付遇到问题，请重试");
                            }
                        } else {
                            showAlertDialog("支付失败", "支付遇到问题，请重试");
                        }
                        // 刷新页面
                        // showLoadingDialog();
                        getRefreshDataList();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void showAlertDialog(String title, String content) {
        TipsAlertDialog3 dialog = new TipsAlertDialog3(getActivity());
        dialog.setTitleText(title);
        dialog.setContentText(content);
        dialog.setConfirmText("好的");
        dialog.setConfirmClickListener(new TipsAlertDialog3.OnSweetClickListener() {
            @Override
            public void onClick(TipsAlertDialog3 sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });
        dialog.show();
    }

    /********* 在fragment中切换tab *********/

    private ChangeBottomTabListener bottomTabListener;

    public void setBottomTabListener(ChangeBottomTabListener bottomTabListener) {
        this.bottomTabListener = bottomTabListener;
    }

}
